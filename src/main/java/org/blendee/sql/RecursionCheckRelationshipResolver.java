package org.blendee.sql;

import java.util.List;

import org.blendee.jdbc.TablePath;

/**
 * 再帰を検出することにより探索を制限する {@link RelationshipResolver} です。
 * @author 千葉 哲嗣
 */
public class RecursionCheckRelationshipResolver implements RelationshipResolver {

	@Override
	public boolean canTraverse(List<TablePath> relationshipPath, TablePath target) {
		return !relationshipPath.contains(target);
	}
}
