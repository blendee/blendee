package org.blendee.selector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.blendee.internal.U;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Column;
import org.blendee.sql.NotFoundException;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;

/**
 * @author 千葉 哲嗣
 */
abstract class AbstractColumnRepository implements ColumnRepository {

	private final RelationshipFactory factory = ContextManager.get(RelationshipFactory.class);

	private final Map<String, TablePathSource> tablePathMap = new TreeMap<>();

	private boolean changed = false;

	@Override
	public synchronized Column[] getColumns(String id) {
		TablePathSource table = tablePathMap.get(id);
		if (table == null) return Column.EMPTY_ARRAY;

		TablePath path = table.getTablePath();
		if (!path.exists()) return Column.EMPTY_ARRAY;

		Collection<ColumnSource> columns = table.getColumnSources();
		if (columns.size() == 0) return Column.EMPTY_ARRAY;
		Relationship root = factory.getInstance(path);
		List<Column> list = new LinkedList<>();
		for (ColumnSource column : columns) {
			try {
				list.add(column.find(root));
			} catch (NotFoundException e) {}
		}

		Collections.sort(list);
		return list.toArray(new Column[list.size()]);
	}

	@Override
	public synchronized void addColumn(String id, Column column, String... usingClassNames) {
		TablePathSource source = setTablePathInternal(
			id,
			column.getRelationship().getRoot().getTablePath(),
			Arrays.asList(usingClassNames));
		if (!source.add(column)) return;
		changed = true;
	}

	@Override
	public synchronized void removeColumn(String id, Column column) {
		TablePathSource source = tablePathMap.get(id);
		if (source == null) return;
		if (!source.remove(column)) return;
		changed = true;
	}

	@Override
	public synchronized void renameID(String oldID, String newId, String... usingClassNames) {
		if (oldID.equals(newId)) return;
		TablePathSource oldPath = tablePathMap.remove(oldID);
		if (oldPath == null) return;
		tablePathMap.remove(newId);
		TablePathSource newPath = setTablePathInternal(
			newId,
			oldPath.getTablePath(),
			Arrays.asList(usingClassNames));
		oldPath.putAllColumns(newPath);
		changed = true;
	}

	@Override
	public synchronized boolean contains(String id) {
		return tablePathMap.containsKey(id);
	}

	@Override
	public synchronized boolean containsColumn(String id, Column column) {
		TablePathSource source = tablePathMap.get(id);
		if (source == null) return false;
		return source.contains(column);
	}

	@Override
	public synchronized String[] getIDs() {
		Set<String> keys = tablePathMap.keySet();
		String[] result = keys.toArray(new String[keys.size()]);
		Arrays.sort(result);
		return result;
	}

	@Override
	public synchronized void remove(String id) {
		if (tablePathMap.remove(id) == null) return;
		changed = true;
	}

	@Override
	public synchronized long getMarkClearedTimestamp(String id) {
		TablePathSource source = tablePathMap.get(id);
		if (source == null) throw new IllegalStateException(id + " は存在しません");
		return source.timestamp;
	}

	@Override
	public synchronized void markColumn(String id, Column column, String... usingClassNames) {
		TablePathSource source = tablePathMap.get(id);
		if (source == null) return;
		changed |= source.addUsingClasses(Arrays.asList(usingClassNames));
		ColumnSource columnSource = source.get(column);
		if (columnSource == null || columnSource.mark) return;
		columnSource.mark = true;
		changed = true;
	}

	@Override
	public synchronized void clear(String id, long timestamp) {
		TablePathSource source = tablePathMap.get(id);
		if (source == null) return;

		source.clearUsingClasses();

		for (ColumnSource columnSource : source.getColumnSources())
			columnSource.mark = false;

		source.timestamp = timestamp;
		changed = true;
	}

	@Override
	public synchronized boolean marks(String id, Column column) {
		TablePathSource source = tablePathMap.get(id);
		if (source == null) return false;
		ColumnSource columnSource = source.get(column);
		if (columnSource == null) return false;
		return columnSource.mark;
	}

	@Override
	public String[] getUsingClassNames(String id) {
		TablePathSource source = tablePathMap.get(id);
		if (source == null) return U.STRING_EMPTY_ARRAY;

		return source.usings.toArray(new String[source.usings.size()]);
	}

	@Override
	public synchronized void add(String id, TablePath path, String... usingClassNames) {
		setTablePathInternal(id, path, Arrays.asList(usingClassNames));
	}

	@Override
	public synchronized TablePath getTablePath(String id) {
		TablePathSource source = tablePathMap.get(id);
		if (source == null) return null;
		return source.getTablePath();
	}

	@Override
	public synchronized String[] getErrorMessages(String id) {
		TablePathSource table = tablePathMap.get(id);
		if (table == null) return new String[0];

		TablePath path = table.getTablePath();
		if (!path.exists()) return new String[] { path + " は存在しません" };

		Collection<ColumnSource> columns = table.getColumnSources();
		if (columns.size() == 0) return new String[0];

		Relationship root = factory.getInstance(path);
		List<String> messages = new LinkedList<>();
		for (ColumnSource column : columns) {
			try {
				column.find(root);
			} catch (NotFoundException e) {
				messages.add(e.getLocalizedMessage());
			}
		}
		return messages.toArray(new String[messages.size()]);
	}

	@Override
	public synchronized void correctErrors(String id) {
		TablePathSource table = tablePathMap.get(id);
		if (table == null) return;

		TablePath path = table.getTablePath();
		if (!path.exists()) return;

		Collection<ColumnSource> columns = table.getColumnSources();
		if (columns.size() == 0) return;

		Relationship root = factory.getInstance(path);
		for (Iterator<ColumnSource> i = columns.iterator(); i.hasNext();) {
			ColumnSource column = i.next();
			try {
				column.find(root);
			} catch (NotFoundException e) {
				i.remove();
				changed = true;
			}
		}
	}

	@Override
	public synchronized void commit() {
		if (!changed) return;
		write(tablePathMap);
		changed = false;
	}

	@Override
	public synchronized void rollback() {
		tablePathMap.clear();
		read(tablePathMap);
		changed = false;
	}

	@Override
	public synchronized boolean canCommit() {
		return changed;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	void initialize() {
		read(tablePathMap);
	}

	abstract void read(Map<String, TablePathSource> tablePathMap);

	abstract void write(Map<String, TablePathSource> tablePathMap);

	private TablePathSource setTablePathInternal(String id, TablePath path, Collection<String> usings) {
		TablePathSource source = tablePathMap.get(id);
		if (source != null) {
			if (source.getTablePath().equals(path)) {
				changed |= source.addUsingClasses(usings);
				return source;
			}

			source = new TablePathSource(
				id,
				path,
				source.getUsingClasses(),
				System.currentTimeMillis());
		} else {
			source = new TablePathSource(
				id,
				path,
				usings,
				System.currentTimeMillis());
		}

		source = new TablePathSource(id, path, source.getUsingClasses(), System.currentTimeMillis());
		source.addUsingClasses(usings);
		tablePathMap.put(id, source);
		changed = true;

		return source;
	}

	static class TablePathSource {

		private final String id;

		private final TablePath path;

		private final Set<String> usings = new HashSet<>();

		private final Map<String, ColumnSource> columnMap = new TreeMap<>();

		private long timestamp;

		TablePathSource(String id, TablePath path, Collection<String> usings, long timestamp) {
			this.id = id;
			this.path = path;
			this.usings.addAll(usings);
			this.timestamp = timestamp;
		}

		TablePathSource(
			String id,
			TablePath path,
			Collection<String> using,
			Collection<ColumnSource> sources,
			long timestamp) {
			this.id = id;
			this.path = path;
			this.usings.addAll(usings);
			this.timestamp = timestamp;
			for (ColumnSource source : sources) {
				columnMap.put(source.getPath(), source);
			}
		}

		@Override
		public String toString() {
			String usingString = "{" + String.join(",", usings) + "}";
			return "id=" + id + "&path=" + path + "&using=" + usingString + "&timestamp=" + timestamp;
		}

		String getId() {
			return id;
		}

		TablePath getTablePath() {
			return path;
		}

		long getTimestamp() {
			return timestamp;
		}

		void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}

		void add(ColumnSource columnSource) {
			columnMap.put(columnSource.getPath(), columnSource);
		}

		boolean addUsingClasses(Collection<String> usings) {
			return this.usings.addAll(Objects.requireNonNull(usings));
		}

		void clearUsingClasses() {
			usings.clear();
		}

		Set<String> getUsingClasses() {
			return new HashSet<>(usings);
		}

		Collection<ColumnSource> getColumnSources() {
			return columnMap.values();
		}

		ColumnSource get(Column column) {
			return columnMap.get(createPathString(column));
		}

		boolean contains(Column column) {
			return columnMap.containsKey(createPathString(column));
		}

		boolean add(Column column) {
			ColumnPath path = new ColumnPath(column);
			if (columnMap.containsKey(path.pathString)) return false;
			columnMap.put(path.pathString, new ColumnSource(path, false));
			return true;
		}

		boolean remove(Column column) {
			if (columnMap.remove(createPathString(column)) == null) return false;
			return true;
		}

		void putAllColumns(TablePathSource another) {
			another.columnMap.putAll(columnMap);
		}

		private static String createPathString(Column column) {
			return new ColumnPath(column).pathString;
		}
	}

	static class ColumnSource {

		private final ForeignKeySource[] foreignKeySources;

		private final String columnName;

		private final String path;

		private boolean mark;

		ColumnSource(ColumnPath path, boolean mark) {
			this.foreignKeySources = path.getForeignKeySources();
			this.columnName = path.columnName;
			this.path = path.pathString;
			this.mark = mark;
		}

		@Override
		public String toString() {
			return path + "&mark=" + mark;
		}

		boolean getMark() {
			return mark;
		}

		void setMark(boolean mark) {
			this.mark = mark;
		}

		String getPath() {
			return path;
		}

		Column find(Relationship root) {
			Relationship current = root;
			for (ForeignKeySource foreignKey : foreignKeySources) {
				current = foreignKey.delegateFind(current);
			}

			return current.getColumn(columnName);
		}
	}

	static class ColumnPath {

		private static final String foreignKeyNameSign = "@";

		private final List<ForeignKeySource> foreignKeySources = new LinkedList<>();

		private final String pathString;

		private String columnName;

		ColumnPath(String pathString) {
			this.pathString = pathString;
			String[] pathElements = pathString.split(",");
			for (int i = 0; i < pathElements.length - 1; i++) {
				String pathElement = pathElements[i];
				if (pathElement.startsWith(foreignKeyNameSign)) {
					foreignKeySources.add(new ForeignKeySource(pathElement.substring(1)));
				} else {
					foreignKeySources.add(new ForeignKeySource(pathElement.split("\\+")));
				}
			}

			columnName = pathElements[pathElements.length - 1];
		}

		ColumnPath(Column column) {
			Relationship relationship = column.getRelationship();
			columnName = column.getName();
			List<Relationship> relations = new LinkedList<>();
			relationship.addParentTo(relations);
			relations.add(relationship);
			relations.remove(relationship.getRoot());
			List<String> pathElements = new LinkedList<>();
			for (Relationship parent : relations) {
				String foreignKeyName = parent.getCrossReference().getForeignKeyName();
				if (foreignKeyName == null || foreignKeyName.length() == 0) {
					String[] columnNames = parent.getCrossReference().getForeignKeyColumnNames();
					foreignKeySources.add(new ForeignKeySource(columnNames));
					pathElements.add(String.join("+", columnNames));
				} else {
					foreignKeySources.add(new ForeignKeySource(foreignKeyName));
					pathElements.add(foreignKeyNameSign + foreignKeyName);
				}
			}

			pathElements.add(columnName);
			pathString = String.join(",", pathElements);
		}

		private ForeignKeySource[] getForeignKeySources() {
			return foreignKeySources.toArray(new ForeignKeySource[foreignKeySources.size()]);
		}
	}

	private static class ForeignKeySource {

		private final String foreignKeyName;

		private final String[] columnNames;

		private ForeignKeySource(String foreignKeyName) {
			this.foreignKeyName = foreignKeyName;
			columnNames = null;
		}

		private ForeignKeySource(String[] columnNames) {
			foreignKeyName = null;
			this.columnNames = columnNames.clone();
		}

		private boolean isForeignKeyName() {
			return foreignKeyName != null;
		}

		private Relationship delegateFind(Relationship parent) {
			if (isForeignKeyName()) return parent.find(foreignKeyName);
			return parent.find(columnNames);
		}
	}
}
