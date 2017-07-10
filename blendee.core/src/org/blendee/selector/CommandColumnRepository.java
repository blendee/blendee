package org.blendee.selector;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.blendee.internal.CollectionMap;
import org.blendee.internal.Queue;
import org.blendee.internal.QueueElement;
import org.blendee.internal.U;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Column;

/**
 * UNDO REDO が可能になった {@link ColumnRepository} です。
 * @author 千葉 哲嗣
 */
public class CommandColumnRepository implements ColumnRepository {

	private final Map<String, TablePathContainer> addIDs = new HashMap<>();

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
	 * @param repository ベースとなる {@link ColumnRepository}
	 */
	public CommandColumnRepository(ColumnRepository repository) {
		this.repository = repository;
		commands.addLast(current);
	}

	@Override
	public synchronized TablePath getTablePath(String id) {
		TablePathContainer container = addIDs.get(id);
		if (container != null) return container.path;
		if (removeIDs.contains(id)) return null;
		return repository.getTablePath(id);
	}

	@Override
	public synchronized void add(String id, TablePath path, String... usingClassNames) {
		addCommand(new AddIDCommand(id, path, usingClassNames));
	}

	@Override
	public synchronized void remove(String id) {
		addCommand(new RemoveIDCommand(id, getUsingClassNames(id)));
	}

	@Override
	public synchronized String[] getUsingClassNames(String id) {
		if (clears.containsKey(id)) return U.STRING_EMPTY_ARRAY;

		TablePathContainer container = addIDs.get(id);
		if (container != null) return container.usingClassNames;

		if (removeIDs.contains(id)) return null;

		return repository.getUsingClassNames(id);
	}

	@Override
	public synchronized void renameID(String oldId, String newId, String... usingClassNames) {
		if (oldId.equals(newId)) return;
		if (getTablePath(oldId) == null) return;
		addCommand(new RenameIdCommand(oldId, newId, usingClassNames));
	}

	@Override
	public synchronized Column[] getColumns(String id) {
		if (getTablePath(id) == null) return Column.EMPTY_ARRAY;
		Set<Column> columns = new HashSet<>();
		columns.addAll(Arrays.asList(repository.getColumns(id)));
		columns.removeAll(removeColumns.get(id).stream().map(c -> c.column).collect(Collectors.toList()));
		columns.addAll(addColumns.get(id).stream().map(c -> c.column).collect(Collectors.toList()));
		Column[] result = columns.toArray(new Column[columns.size()]);
		Arrays.sort(result);
		return result;
	}

	@Override
	public synchronized void addColumn(String id, Column column, String... usingClassNames) {
		addCommand(new AddColumnCommand(id, column, usingClassNames));
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
		if (removeColumns.get(id).contains(new ColumnContainer(column))) return false;
		if (addColumns.get(id).contains(new ColumnContainer(column))) return true;
		return repository.containsColumn(id, column);
	}

	@Override
	public long getMarkClearedTimestamp(String id) {
		Long timestamp = clears.get(id);
		if (timestamp == null) return repository.getMarkClearedTimestamp(id);
		return timestamp.longValue();
	}

	@Override
	public void markColumn(String id, Column column, String... usingClassNames) {
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
			(key, value) -> repository.add(key, value.path, value.usingClassNames));

		for (String id : removeIDs)
			repository.remove(id);

		for (String id : addColumns.keySet())
			for (ColumnContainer container : addColumns.get(id))
				repository.addColumn(id, container.column, container.usingClassNames);

		for (String id : removeColumns.keySet())
			for (ColumnContainer container : removeColumns.get(id))
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
	 * @return {@link #undo()} 可能な場合、 true
	 */
	public synchronized boolean canUndo() {
		return current.hasPrevious();
	}

	/**
	 * {@link #redo()} 可能かどうか調査します。
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

	private static class TablePathContainer {

		private final TablePath path;

		private final String[] usingClassNames;

		private TablePathContainer(TablePath path, String[] usingClassNames) {
			this.path = path;
			this.usingClassNames = usingClassNames;
		}
	}

	private static class ColumnContainer {

		private final Column column;

		private final String[] usingClassNames;

		private ColumnContainer(Column column) {
			this.column = column;
			this.usingClassNames = U.STRING_EMPTY_ARRAY;
		}

		private ColumnContainer(Column column, String[] usingClassNames) {
			this.column = column;
			this.usingClassNames = usingClassNames;
		}

		@Override
		public boolean equals(Object others) {
			if (!(others instanceof ColumnContainer)) return false;
			return column.equals(((ColumnContainer) others).column);
		}

		@Override
		public int hashCode() {
			return column.hashCode();
		}
	}

	private static class MyCollectionMap extends CollectionMap<String, ColumnContainer> {

		@Override
		protected Collection<ColumnContainer> createNewCollection() {
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

	private class AddIDCommand extends Command {

		private final TablePath path;

		private final RemoveIDCommand removeID;

		private final String[] usings;

		private final long timestamp = System.currentTimeMillis();

		private AddIDCommand(String id, TablePath path, String[] usings) {
			super(id);

			TablePath removePath = getTablePath(id);
			if (removePath != null) {
				removeID = new RemoveIDCommand(id, usings);
			} else {
				removeID = null;
			}

			this.path = path;
			this.usings = usings;
		}

		@Override
		void undo() {
			removeIDs.add(id);
			addIDs.remove(id);
			clears.remove(id);
			if (removeID != null) removeID.undo();
		}

		@Override
		void redo() {
			if (removeID != null) removeID.redo();
			removeIDs.remove(id);

			addIDs.put(id, new TablePathContainer(path, usings));
			clears.put(id, timestamp);
		}
	}

	private class RemoveIDCommand extends Command {

		private final TablePath path;

		private final RemoveColumnCommand[] availableColumnCommands;

		private final String[] usings;

		private RemoveIDCommand(String id, String[] usings) {
			super(id);
			path = getTablePath(id);
			this.usings = usings;
			Column[] availables = getColumns(id);
			availableColumnCommands = new RemoveColumnCommand[availables.length];
			for (int i = 0; i < availables.length; i++) {
				availableColumnCommands[i] = new RemoveColumnCommand(id, availables[i], usings);
			}
		}

		@Override
		void undo() {
			addIDs.put(id, new TablePathContainer(path, usings));
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

		private final RemoveIDCommand removeOldIDCommand;

		private final RemoveIDCommand removeNewIDCommand;

		private final AddIDCommand addIDCommand;

		private final AddColumnCommand[] addColumnCommands;

		private RenameIdCommand(String oldID, String newID, String[] usings) {
			super(null);

			removeOldIDCommand = new RemoveIDCommand(oldID, usings);
			if (getTablePath(newID) != null) {
				removeNewIDCommand = new RemoveIDCommand(newID, usings);
			} else {
				removeNewIDCommand = null;
			}

			addIDCommand = new AddIDCommand(newID, getTablePath(oldID), usings);
			Column[] columns = getColumns(oldID);
			addColumnCommands = new AddColumnCommand[columns.length];
			for (int i = 0; i < columns.length; i++) {
				addColumnCommands[i] = new AddColumnCommand(newID, columns[i], usings);
			}
		}

		@Override
		void undo() {
			for (AddColumnCommand command : addColumnCommands)
				command.undo();

			addIDCommand.undo();
			if (removeNewIDCommand != null) removeNewIDCommand.undo();
			removeOldIDCommand.undo();
		}

		@Override
		void redo() {
			removeOldIDCommand.redo();
			if (removeNewIDCommand != null) removeNewIDCommand.redo();
			addIDCommand.redo();
			for (AddColumnCommand command : addColumnCommands)
				command.redo();
		}
	}

	private class AddColumnCommand extends Command {

		private final Column column;

		private final AddIDCommand addID;

		private final String[] usings;

		private AddColumnCommand(String id, Column column, String[] usings) {
			super(id);
			this.column = column;
			this.usings = usings;
			if (getTablePath(id) == null) {
				addID = new AddIDCommand(
					id,
					column.getRelationship().getRoot().getTablePath(),
					usings);
			} else {
				addID = null;
			}
		}

		@Override
		void undo() {
			if (addID != null) addID.undo();
			ColumnContainer container = new ColumnContainer(column, usings);
			removeColumns.get(id).add(container);
			addColumns.get(id).remove(container);
		}

		@Override
		void redo() {
			if (addID != null) addID.redo();
			ColumnContainer container = new ColumnContainer(column, usings);
			removeColumns.get(id).remove(container);
			addColumns.get(id).add(container);
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
			ColumnContainer container = new ColumnContainer(column, usings);
			addColumns.get(id).add(container);
			removeColumns.get(id).remove(container);
		}

		@Override
		void redo() {
			ColumnContainer container = new ColumnContainer(column, usings);
			addColumns.get(id).remove(container);
			removeColumns.get(id).add(container);
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
