package org.blendee.assist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.blendee.orm.DataObject;
import org.blendee.orm.DataObjectIterator;
import org.blendee.sql.Relationship;

class DataObjectManager {

	private final DataObjectIterator iterator;

	private final LinkedList<OneToManyBehavior> route;

	Map<Relationship, DataObject> current;

	DataObjectManager(DataObjectIterator iterator, List<OneToManyBehavior> relations) {
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
		var map = new HashMap<Relationship, DataObject>();
		map.put(source.getRelationship(), source);
		for (var relation : route) {
			var include = relation.getRelationship();
			source = source.getDataObject(include.getCrossReference().getForeignKeyName());
			map.put(include, source);
		}

		return map;
	}

	void close() {
		iterator.close();
	}
}
