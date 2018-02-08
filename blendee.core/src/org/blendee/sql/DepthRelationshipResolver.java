package org.blendee.sql;

import java.util.List;

import org.blendee.jdbc.ManagementSubject;
import org.blendee.jdbc.TablePath;

/**
 * 深さにより探索を制限する {@link RelationshipResolver} です。
 * @author 千葉 哲嗣
 */
public class DepthRelationshipResolver implements RelationshipResolver, ManagementSubject {

	private static final int defaultDepth = 5;

	private int depth = defaultDepth;

	/**
	 * 制限する深さを変更します。
	 * @param depth 新しい深さ
	 */
	public synchronized void setDepth(int depth) {
		this.depth = depth;
	}

	@Override
	public boolean canTraverse(List<TablePath> relationshipPath, TablePath target) {
		return relationshipPath.size() <= getDepth();
	}

	private synchronized int getDepth() {
		return depth;
	}
}
