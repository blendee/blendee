package org.blendee.sql;

import java.util.List;

import org.blendee.jdbc.TablePath;

public class RecursionCheckRelationshipResolver implements RelationshipResolver {

	@Override
	public boolean canTraverse(List<TablePath> relationshipPath, TablePath target) {
		return !relationshipPath.contains(target);
	}
}
