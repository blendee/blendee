package org.blendee.sql;

import java.util.List;

import org.blendee.jdbc.TablePath;

public class DepthRelationshipResolver implements RelationshipResolver {

	private static final int depth = 5;

	@Override
	public boolean canTraverse(List<TablePath> relationshipPath, TablePath target) {
		return relationshipPath.size() <= depth;
	}
}
