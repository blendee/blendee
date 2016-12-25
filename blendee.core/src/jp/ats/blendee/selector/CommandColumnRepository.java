package jp.ats.blendee.selector;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import jp.ats.blendee.internal.CollectionMap;
import jp.ats.blendee.internal.Queue;
import jp.ats.blendee.internal.QueueElement;
import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.ResourceLocator;
import jp.ats.blendee.sql.Column;

/**
 * UNDO REDO が可能になった {@link ColumnRepository} です。
 *
 * @author 千葉 哲嗣
 */
public class CommandColumnRepository implements ColumnRepository {

	private final Map<String, Container> addIDs = new HashMap<>();

	private final Set<String> removeIDs = new HashSet<>();

	private final MyCollectionMap addColumns = new MyCollectionMap();

	private final MyCollectionMap removeColumns = new MyCollectionMap();

	private final Map<String, Long> clears = new HashMap<>();

	private final ColumnRepository repository;

	private final Queue<Command> commands = new Queue<Command>();

	private Command current = new Command(null) {

		@Override
		void undo() {}

		@Override
		void redo() {}
	};

	private Command commitPoint = current;

	/**
	 * ベースとなる {@link ColumnRepository} を元に、このクラスのインスタンスを生成します。
	 *
	 * @param repository ベースとなる {@link ColumnRepository}
	 */
	public CommandColumnRepository(ColumnRepository repository) {
		this.repository = repository;
		commands.addLast(current);
	}

	@Override
	public synchronized ResourceLocator getResourceLocator(String id) {
		Container container = addIDs.get(id);
		if (container != null) return container.locator;
		if (removeIDs.contains(id)) return null;
		return repository.getResourceLocator(id);
	}

	@Override
	public synchronized void add(String id, ResourceLocator locator, Class<?>... usings) {
		addCommand(new AddIdCommand(id, locator, convertUsing(usings)));
	}

	@Override
	public synchronized void remove(String id) {
		addCommand(new RemoveIdCommand(id, getUsingClassNames(id)));
	}

	@Override
	public synchronized String[] getUsingClassNames(String id) {
		if (clears.containsKey(id)) return U.STRING_EMPTY_ARRAY;

		Container container = addIDs.get(id);
		if (container != null) return container.usings;

		if (removeIDs.contains(id)) return null;

		return repository.getUsingClassNames(id);
	}

	@Override
	public synchronized void renameID(String oldId, String newId, Class<?>... usings) {
		if (oldId.equals(newId)) return;
		if (getResourceLocator(oldId) == null) return;
		addCommand(new RenameIdCommand(oldId, newId, convertUsing(usings)));
	}

	@Override
	public synchronized Column[] getColumns(String id) {
		if (getResourceLocator(id) == null) return Column.EMPTY_ARRAY;
		Set<Column> columns = new HashSet<>();
		columns.addAll(Arrays.asList(repository.getColumns(id)));
		columns.removeAll(removeColumns.get(id));
		columns.addAll(addColumns.get(id).stream().map(c -> c.column).collect(Collectors.toList()));
		Column[] result = columns.toArray(new Column[columns.size()]);
		Arrays.sort(result);
		return result;
	}

	@Override
	public synchronized void addColumn(String id, Column column, Class<?>... usings) {
		addCommand(new AddColumnCommand(id, column, convertUsing(usings)));
	}

	@Override
	public synchronized void removeColumn(String id, Column column) {
		addCommand(new RemoveColumnCommand(id, column, getUsingClassNames(id)));
	}

	@Override
	public synchronized boolean contains(String id) {
		if (removeIDs.contains(id)) return false;
		if (addIDs.containsKey(id)) return true;
		return repository.contains(id);
	}

	@Override
	public synchronized boolean containsColumn(String id, Column column) {
		if (removeColumns.get(id).contains(column)) return false;
		if (addColumns.get(id).contains(column)) return true;
		return repository.containsColumn(id, column);
	}

	@Override
	public long getMarkClearedTimestamp(String id) {
		Long timestamp = clears.get(id);
		if (timestamp == null) return repository.getMarkClearedTimestamp(id);
		return timestamp.longValue();
	}

	@Override
	public void markColumn(String id, Column column, Class<?>... usings) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void clear(String id, long timestamp) {
		addCommand(new ClearCommand(id));
	}

	@Override
	public boolean marks(String id, Column column) {
		if (clears.containsKey(id)) return false;
		return repository.marks(id, column);
	}

	@Override
	public synchronized boolean canCommit() {
		return commitPoint != current || repository.canCommit();
	}

	@Override
	public synchronized String[] getIDs() {
		Set<String> ids = new LinkedHashSet<>();
		ids.addAll(Arrays.asList(repository.getIDs()));
		ids.removeAll(removeIDs);
		ids.addAll(addIDs.keySet());
		String[] result = ids.toArray(new String[ids.size()]);
		Arrays.sort(result);
		return result;
	}

	@Override
	public synchronized String[] getErrorMessages(String id) {
		return repository.getErrorMessages(id);
	}

	@Override
	public synchronized void correctErrors(String id) {
		repository.correctErrors(id);
	}

	@Override
	public synchronized void commit() {
		addIDs.forEach(
			(key, value) -> repository.add(key, value.locator, convert(value.usings)));

		for (String id : removeIDs)
			repository.remove(id);

		for (String id : addColumns.keySet())
			for (Container container : addColumns.get(id))
				repository.addColumn(id, container.column, convert(container.usings));

		for (String id : removeColumns.keySet())
			for (Container container : removeColumns.get(id))
				repository.removeColumn(id, container.column);

		for (Entry<String, Long> entry : clears.entrySet())
			repository.clear(entry.getKey(), entry.getValue());

		repository.commit();
		clearAll();
		commitPoint = current;
	}

	@Override
	public synchronized void rollback() {
		clearAll();
		repository.rollback();
		Command first = commands.getFirst();
		commands.clear();
		commands.addFirst(first);
		current = first;
		commitPoint = first;
	}

	/**
	 * 一動作分戻します。
	 */
	public synchronized void undo() {
		current.undo();
		current = current.getPrevious();
	}

	/**
	 * {@link #undo()} で戻した直近の一動作分を再実行します。
	 */
	public synchronized void redo() {
		current = current.getNext();
		current.redo();
	}

	/**
	 * {@link #undo()} 可能かどうか調査します。
	 *
	 * @return {@link #undo()} 可能な場合、 true
	 */
	public synchronized boolean canUndo() {
		return current.hasPrevious();
	}

	/**
	 * {@link #redo()} 可能かどうか調査します。
	 *
	 * @return {@link #redo()} 可能な場合、 true
	 */
	public synchronized boolean canRedo() {
		return current.hasNext();
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private void clearAll() {
		addIDs.clear();
		removeIDs.clear();
		addColumns.clear();
		removeColumns.clear();
		clears.clear();
	}

	private void addCommand(Command command) {
		while (current.hasNext()) {
			current.getNext().remove();
		}
		commands.addLast(command);
		current = command;
		current.redo();
	}

	private static String[] convertUsing(Class<?>[] usings) {
		List<String> names = Arrays.asList(usings).stream().map(
			c -> c.getName()).collect(Collectors.toList());
		return names.toArray(new String[names.size()]);
	}

	private static Class<?>[] convert(String[] classNames) {
		List<Class<?>> list = Arrays.asList(classNames).stream().flatMap(name -> {
			try {
				return Arrays.asList(Class.forName(name)).stream();
			} catch (Exception e) {
				return Arrays.asList(new Class<?>[] {}).stream();
			}
		}).collect(Collectors.toList());

		return list.toArray(new Class<?>[list.size()]);
	}

	private static class Container {

		private final ResourceLocator locator;

		private final Column column;

		private final String[] usings;

		private Container(ResourceLocator locator, String[] usings) {
			this.locator = locator;
			column = null;
			this.usings = usings;
		}

		private Container(Column column, String[] usings) {
			locator = null;
			this.column = column;
			this.usings = usings;
		}
	}

	private static class MyCollectionMap extends CollectionMap<String, Container> {

		@Override
		protected Collection<Container> createNewCollection() {
			return new HashSet<>();
		}
	}

	private static abstract class Command extends QueueElement {

		final String id;

		Command(String id) {
			this.id = id;
		}

		@Override
		public Command getPrevious() {
			return (Command) super.getPrevious();
		}

		@Override
		public Command getNext() {
			return (Command) super.getNext();
		}

		abstract void undo();

		abstract void redo();
	}

	private class AddIdCommand extends Command {

		private final ResourceLocator locator;

		private final RemoveIdCommand removeId;

		private final String[] usings;

		private final long timestamp = System.currentTimeMillis();

		private AddIdCommand(String id, ResourceLocator locator, String[] usings) {
			super(id);

			ResourceLocator removeLocator = getResourceLocator(id);
			if (removeLocator != null) {
				removeId = new RemoveIdCommand(id, usings);
			} else {
				removeId = null;
			}

			this.locator = locator;
			this.usings = usings;
		}

		@Override
		void undo() {
			removeIDs.add(id);
			addIDs.remove(id);
			clears.remove(id);
			if (removeId != null) removeId.undo();
		}

		@Override
		void redo() {
			if (removeId != null) removeId.redo();
			removeIDs.remove(id);

			addIDs.put(id, new Container(locator, usings));
			clears.put(id, timestamp);
		}
	}

	private class RemoveIdCommand extends Command {

		private final ResourceLocator locator;

		private final RemoveColumnCommand[] availableColumnCommands;

		private final String[] usings;

		private RemoveIdCommand(String id, String[] usings) {
			super(id);
			locator = getResourceLocator(id);
			this.usings = usings;
			Column[] availables = getColumns(id);
			availableColumnCommands = new RemoveColumnCommand[availables.length];
			for (int i = 0; i < availables.length; i++) {
				availableColumnCommands[i] = new RemoveColumnCommand(id, availables[i], usings);
			}
		}

		@Override
		void undo() {
			addIDs.put(id, new Container(locator, usings));
			removeIDs.remove(id);
			for (RemoveColumnCommand command : availableColumnCommands)
				command.undo();
		}

		@Override
		void redo() {
			addIDs.remove(id);
			removeIDs.add(id);
			for (RemoveColumnCommand command : availableColumnCommands)
				command.redo();
		}
	}

	private class RenameIdCommand extends Command {

		private final RemoveIdCommand removeOldIdCommand;

		private final RemoveIdCommand removeNewIdCommand;

		private final AddIdCommand addIdCommand;

		private final AddColumnCommand[] addColumnCommands;

		private RenameIdCommand(String oldId, String newId, String[] usings) {
			super(null);

			removeOldIdCommand = new RemoveIdCommand(oldId, usings);
			if (getResourceLocator(newId) != null) {
				removeNewIdCommand = new RemoveIdCommand(newId, usings);
			} else {
				removeNewIdCommand = null;
			}

			addIdCommand = new AddIdCommand(newId, getResourceLocator(oldId), usings);
			Column[] columns = getColumns(oldId);
			addColumnCommands = new AddColumnCommand[columns.length];
			for (int i = 0; i < columns.length; i++) {
				addColumnCommands[i] = new AddColumnCommand(newId, columns[i], usings);
			}
		}

		@Override
		void undo() {
			for (AddColumnCommand command : addColumnCommands)
				command.undo();

			addIdCommand.undo();
			if (removeNewIdCommand != null) removeNewIdCommand.undo();
			removeOldIdCommand.undo();
		}

		@Override
		void redo() {
			removeOldIdCommand.redo();
			if (removeNewIdCommand != null) removeNewIdCommand.redo();
			addIdCommand.redo();
			for (AddColumnCommand command : addColumnCommands)
				command.redo();
		}
	}

	private class AddColumnCommand extends Command {

		private final Column column;

		private final AddIdCommand addId;

		private final String[] usings;

		private AddColumnCommand(String id, Column column, String[] usings) {
			super(id);
			this.column = column;
			this.usings = usings;
			if (getResourceLocator(id) == null) {
				addId = new AddIdCommand(
					id,
					column.getRelationship().getRoot().getResourceLocator(),
					usings);
			} else {
				addId = null;
			}
		}

		@Override
		void undo() {
			if (addId != null) addId.undo();
			removeColumns.get(id).add(new Container(column, usings));
			addColumns.get(id).remove(column);
		}

		@Override
		void redo() {
			if (addId != null) addId.redo();
			removeColumns.get(id).remove(column);
			addColumns.get(id).add(new Container(column, usings));
		}
	}

	private class RemoveColumnCommand extends Command {

		private final Column column;

		private final String[] usings;

		private RemoveColumnCommand(String id, Column column, String[] usings) {
			super(id);
			this.column = column;
			this.usings = usings;
		}

		@Override
		void undo() {
			addColumns.get(id).add(new Container(column, usings));
			removeColumns.get(id).remove(column);
		}

		@Override
		void redo() {
			addColumns.get(id).remove(column);
			removeColumns.get(id).add(new Container(column, usings));
		}
	}

	private class ClearCommand extends Command {

		private final long timestamp;

		private final Long oldTimestamp;

		private ClearCommand(String id) {
			super(id);
			timestamp = System.currentTimeMillis();
			oldTimestamp = clears.get(id);
		}

		@Override
		void undo() {
			if (oldTimestamp == null) {
				clears.remove(id);
			} else {
				clears.put(id, oldTimestamp);
			}
		}

		@Override
		void redo() {
			clears.put(id, timestamp);
		}
	}
}
