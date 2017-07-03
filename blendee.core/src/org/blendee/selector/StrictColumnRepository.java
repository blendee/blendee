package org.blendee.selector;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.blendee.internal.CollectionMap;
import org.blendee.internal.U;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Column;

/**
 * @author 千葉 哲嗣
 */
class StrictColumnRepository implements ColumnRepository {

	private final Map<String, TablePath> locationMap = new TreeMap<>();

	private final CollectionMap<String, Column> columnMap = new CollectionMap<String, Column>(TreeMap.class) {

		@Override
		protected Collection<Column> createNewCollection() {
			return new TreeSet<>();
		}
	};

	private final CollectionMap<String, String> usingMap = new CollectionMap<>();

	private final Map<String, String[]> errorMessagesMap = new HashMap<>();

	StrictColumnRepository(final ColumnRepository repository) {
		final String[] ids = repository.getIDs();
		for (int i = 0; i < ids.length; i++) {
			String id = ids[i];
			locationMap.put(id, repository.getTablePath(id));
			Column[] columns = repository.getColumns(id);
			columnMap.get(id).addAll(Arrays.asList(columns));
			usingMap.get(id).addAll(Arrays.asList(repository.getUsingClassNames(id)));
			errorMessagesMap.put(id, repository.getErrorMessages(id));
		}
	}

	@Override
	public TablePath getTablePath(String id) {
		return locationMap.get(id);
	}

	@Override
	public void add(String id, TablePath path, String... usingClassNames) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void renameID(String oldId, String newId, String... usingClassNames) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Column[] getColumns(String id) {
		Collection<Column> columns = columnMap.get(id);
		return columns.toArray(new Column[columns.size()]);
	}

	@Override
	public void addColumn(String id, Column column, String... usingClassNames) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeColumn(String id, Column column) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(String id) {
		return locationMap.containsKey(id);
	}

	@Override
	public boolean containsColumn(String id, Column column) {
		return columnMap.get(id).contains(column);
	}

	@Override
	public String[] getUsingClassNames(String id) {
		Collection<String> usings = usingMap.get(id);
		return usings.toArray(new String[usings.size()]);
	}

	@Override
	public long getMarkClearedTimestamp(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void markColumn(String id, Column column, String... usingClassNames) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear(String id, long timestamp) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean marks(String id, Column column) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canCommit() {
		return false;
	}

	@Override
	public String[] getIDs() {
		Set<String> ids = locationMap.keySet();
		return ids.toArray(new String[ids.size()]);
	}

	@Override
	public String[] getErrorMessages(String id) {
		return errorMessagesMap.get(id);
	}

	@Override
	public void correctErrors(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void commit() {}

	@Override
	public void rollback() {}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
