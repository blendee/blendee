package org.blendee.sql;

import java.util.List;

import org.blendee.jdbc.TablePath;

/**
 * 深さにより探索を制限する {@link RelationshipResolver} です。
 * @author 千葉 哲嗣
 */
public class DepthRelationshipResolver implements RelationshipResolver {

	private static final int depth = 5;

	@Override
	public boolean canTraverse(List<TablePath> relationshipPath, TablePath target) {
		return relationshipPath.size() <= depth;
	}
}
