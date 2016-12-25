package jp.ats.blendee.support;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import jp.ats.blendee.orm.DataObjectIterator;
import jp.ats.blendee.orm.UpdatableDataObject;
import jp.ats.blendee.sql.Relationship;

class DataObjectManager {

	private final DataObjectIterator<UpdatableDataObject> iterator;

	private final LinkedList<QueryRelationship> route;

	Map<Relationship, UpdatableDataObject> current;

	DataObjectManager(DataObjectIterator<UpdatableDataObject> iterator, LinkedList<QueryRelationship> relations) {
		this.iterator = iterator;
		route = new LinkedList<>(relations);
		route.pop();
		next();
	}

	boolean prepared() {
		return current != null;
	}

	void next() {
		if (!iterator.hasNext()) {
			current = null;
			return;
		}

		current = convert(iterator.next());
	}

	UpdatableDataObject current(Relationship key) {
		return current == null ? null : current.get(key);
	}

	private Map<Relationship, UpdatableDataObject> convert(UpdatableDataObject source) {
		Map<Relationship, UpdatableDataObject> map = new HashMap<>();
		map.put(source.getRelationship(), source);
		for (QueryRelationship relation : route) {
			Relationship include = relation.getRelationship();
			source = source.getDataObject(include.getCrossReference().getForeignKeyName());
			map.put(include, source);
		}

		return map;
	}

	void close() {
		iterator.close();
	}
}
