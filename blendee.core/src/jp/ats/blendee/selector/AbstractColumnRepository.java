package jp.ats.blendee.selector;

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
import java.util.stream.Collectors;

import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.BlendeeContext;
import jp.ats.blendee.jdbc.ResourceLocator;
import jp.ats.blendee.sql.Column;
import jp.ats.blendee.sql.NotFoundException;
import jp.ats.blendee.sql.Relationship;
import jp.ats.blendee.sql.RelationshipFactory;

/**
 * @author 千葉 哲嗣
 */
abstract class AbstractColumnRepository implements ColumnRepository {

	private final RelationshipFactory factory = BlendeeContext.get(RelationshipFactory.class);

	private final Map<String, LocationSource> locationMap = new TreeMap<>();

	private boolean changed = false;

	@Override
	public synchronized Column[] getColumns(String id) {
		LocationSource locationSource = locationMap.get(id);
		if (locationSource == null) return Column.EMPTY_ARRAY;

		ResourceLocator locator = locationSource.getLocator();
		if (!locator.exists()) return Column.EMPTY_ARRAY;

		Collection<ColumnSource> columns = locationSource.getColumnSources();
		if (columns.size() == 0) return Column.EMPTY_ARRAY;
		Relationship root = factory.getInstance(locator);
		List<Column> list = new LinkedList<>();
		for (ColumnSource source : columns) {
			try {
				list.add(source.find(root));
			} catch (NotFoundException e) {}
		}

		Collections.sort(list);
		return list.toArray(new Column[list.size()]);
	}

	@Override
	public synchronized void addColumn(String id, Column column, Class<?>... usings) {
		LocationSource locationSource = setResourceLocatorInternal(
			id,
			column.getRelationship().getRoot().getResourceLocator(),
			convert(usings));
		if (!locationSource.add(column)) return;
		changed = true;
	}

	@Override
	public synchronized void removeColumn(String id, Column column) {
		LocationSource locationSource = locationMap.get(id);
		if (locationSource == null) return;
		if (!locationSource.remove(column)) return;
		changed = true;
	}

	@Override
	public synchronized void renameID(String oldID, String newId, Class<?>... usings) {
		if (oldID.equals(newId)) return;
		LocationSource oldLocation = locationMap.remove(oldID);
		if (oldLocation == null) return;
		locationMap.remove(newId);
		LocationSource newLocation = setResourceLocatorInternal(newId, oldLocation.getLocator(), convert(usings));
		oldLocation.putAllColumns(newLocation);
		changed = true;
	}

	@Override
	public synchronized boolean contains(String id) {
		return locationMap.containsKey(id);
	}

	@Override
	public synchronized boolean containsColumn(String id, Column column) {
		LocationSource locationSource = locationMap.get(id);
		if (locationSource == null) return false;
		return locationSource.contains(column);
	}

	@Override
	public synchronized String[] getIDs() {
		Set<String> keys = locationMap.keySet();
		String[] result = keys.toArray(new String[keys.size()]);
		Arrays.sort(result);
		return result;
	}

	@Override
	public synchronized void remove(String id) {
		if (locationMap.remove(id) == null) return;
		changed = true;
	}

	@Override
	public synchronized long getMarkClearedTimestamp(String id) {
		LocationSource source = locationMap.get(id);
		if (source == null) throw new IllegalStateException(id + " は存在しません");
		return source.timestamp;
	}

	@Override
	public synchronized void markColumn(String id, Column column, Class<?>... usings) {
		LocationSource locationSource = locationMap.get(id);
		if (locationSource == null) return;
		changed |= locationSource.addUsingClasses(convert(usings));
		ColumnSource columnSource = locationSource.get(column);
		if (columnSource == null || columnSource.mark) return;
		columnSource.mark = true;
		changed = true;
	}

	@Override
	public synchronized void clear(String id, long timestamp) {
		LocationSource locationSource = locationMap.get(id);
		if (locationSource == null) return;

		locationSource.clearUsingClasses();

		for (ColumnSource columnSource : locationSource.getColumnSources())
			columnSource.mark = false;

		locationSource.timestamp = timestamp;
		changed = true;
	}

	@Override
	public synchronized boolean marks(String id, Column column) {
		LocationSource locationSource = locationMap.get(id);
		if (locationSource == null) return false;
		ColumnSource columnSource = locationSource.get(column);
		if (columnSource == null) return false;
		return columnSource.mark;
	}

	@Override
	public String[] getUsingClassNames(String id) {
		LocationSource locationSource = locationMap.get(id);
		if (locationSource == null) return U.STRING_EMPTY_ARRAY;

		return locationSource.usings.toArray(new String[locationSource.usings.size()]);
	}

	@Override
	public synchronized void add(String id, ResourceLocator locator, Class<?>... using) {
		setResourceLocatorInternal(id, locator, convert(using));
	}

	@Override
	public synchronized ResourceLocator getResourceLocator(String id) {
		LocationSource source = locationMap.get(id);
		if (source == null) return null;
		return source.getLocator();
	}

	@Override
	public synchronized String[] getErrorMessages(String id) {
		LocationSource locationSource = locationMap.get(id);
		if (locationSource == null) return new String[0];

		ResourceLocator locator = locationSource.getLocator();
		if (!locator.exists()) return new String[] { locator + " は存在しません" };

		Collection<ColumnSource> columns = locationSource.getColumnSources();
		if (columns.size() == 0) return new String[0];

		Relationship root = factory.getInstance(locator);
		List<String> messages = new LinkedList<>();
		for (ColumnSource source : columns) {
			try {
				source.find(root);
			} catch (NotFoundException e) {
				messages.add(e.getLocalizedMessage());
			}
		}
		return messages.toArray(new String[messages.size()]);
	}

	@Override
	public synchronized void correctErrors(String id) {
		LocationSource locationSource = locationMap.get(id);
		if (locationSource == null) return;

		ResourceLocator locator = locationSource.getLocator();
		if (!locator.exists()) return;

		Collection<ColumnSource> columns = locationSource.getColumnSources();
		if (columns.size() == 0) return;

		Relationship root = factory.getInstance(locator);
		for (Iterator<ColumnSource> i = columns.iterator(); i.hasNext();) {
			ColumnSource source = i.next();
			try {
				source.find(root);
			} catch (NotFoundException e) {
				i.remove();
				changed = true;
			}
		}
	}

	@Override
	public synchronized void commit() {
		if (!changed) return;
		write(locationMap);
		changed = false;
	}

	@Override
	public synchronized void rollback() {
		locationMap.clear();
		read(locationMap);
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
		read(locationMap);
	}

	abstract void read(Map<String, LocationSource> locationMap);

	abstract void write(Map<String, LocationSource> locationMap);

	private static Collection<String> convert(Class<?>[] usings) {
		return Arrays.asList(usings).stream().map(c -> c.getName()).collect(Collectors.toList());
	}

	private LocationSource setResourceLocatorInternal(String id, ResourceLocator locator, Collection<String> usings) {
		LocationSource source = locationMap.get(id);
		if (source != null) {
			if (source.getLocator().equals(locator)) {
				changed |= source.addUsingClasses(usings);
				return source;
			}

			source = new LocationSource(
				id,
				locator,
				source.getUsingClasses(),
				System.currentTimeMillis());
		} else {
			source = new LocationSource(
				id,
				locator,
				usings,
				System.currentTimeMillis());
		}

		source = new LocationSource(id, locator, source.getUsingClasses(), System.currentTimeMillis());
		source.addUsingClasses(usings);
		locationMap.put(id, source);
		changed = true;

		return source;
	}

	static class LocationSource {

		private final String id;

		private final ResourceLocator locator;

		private final Set<String> usings = new HashSet<>();

		private final Map<String, ColumnSource> columnMap = new TreeMap<>();

		private long timestamp;

		LocationSource(String id, ResourceLocator locator, Collection<String> usings, long timestamp) {
			this.id = id;
			this.locator = locator;
			this.usings.addAll(usings);
			this.timestamp = timestamp;
		}

		LocationSource(
			String id,
			ResourceLocator locator,
			Collection<String> using,
			Collection<ColumnSource> sources,
			long timestamp) {
			this.id = id;
			this.locator = locator;
			this.usings.addAll(usings);
			this.timestamp = timestamp;
			for (ColumnSource source : sources) {
				columnMap.put(source.getPath(), source);
			}
		}

		@Override
		public String toString() {
			String usingString = "{" + String.join(",", usings) + "}";
			return "id=" + id + "&location=" + locator + "&using=" + usingString + "&timestamp=" + timestamp;
		}

		String getId() {
			return id;
		}

		ResourceLocator getLocator() {
			return locator;
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

		void putAllColumns(LocationSource another) {
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
