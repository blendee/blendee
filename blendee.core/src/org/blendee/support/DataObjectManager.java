package org.blendee.support;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.blendee.orm.DataObject;
import org.blendee.orm.DataObjectIterator;
import org.blendee.sql.Relationship;

class DataObjectManager {

	private final DataObjectIterator iterator;

	private final LinkedList<QueryRelationship> route;

	Map<Relationship, DataObject> current;

	DataObjectManager(DataObjectIterator iterator, LinkedList<QueryRelationship> relations) {
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

	DataObject current(Relationship key) {
		return current == null ? null : current.get(key);
	}

	private Map<Relationship, DataObject> convert(DataObject source) {
		Map<Relationship, DataObject> map = new HashMap<>();
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
