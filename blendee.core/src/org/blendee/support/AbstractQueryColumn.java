package org.blendee.support;

import org.blendee.internal.U;
import org.blendee.sql.Column;

/**
 * {@link Query} 操作に使用する項目クラスの抽象基底クラスです。
 * @author 千葉 哲嗣
 */
public abstract class AbstractQueryColumn {

	final QueryRelationship relationship;

	final Column column;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param helper 条件作成に必要な情報を持った {@link QueryRelationship}
	 * @param name カラム名
	 */
	public AbstractQueryColumn(QueryRelationship helper, String name) {
		relationship = helper;
		column = helper.getRelationship().getColumn(name);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
