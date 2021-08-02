package org.blendee.assist;

import org.blendee.sql.Relationship;
import org.blendee.sql.RuntimeId;

/**
 * Blendee が生成する各文の基底インターフェイスです。
 * @author 千葉 哲嗣
 */
public interface Statement {

	/**
	 * この Query のルート {@link Relationship} を返します。
	 * @return ルート {@link Relationship}
	 */
	Relationship getRootRealtionship();

	/**
	 * この Query の WHERE 句用 {@link LogicalOperators} を返します。
	 * @return {@link LogicalOperators}
	 */
	LogicalOperators<?> getWhereLogicalOperators();

	/**
	 * この文が持つ {@link RuntimeId} を返します。
	 * @return {@link RuntimeId}
	 */
	RuntimeId getRuntimeId();

	/**
	 * @param forSubquery true の場合、カラムにテーブルIDが付与される
	 */
	void forSubquery(boolean forSubquery);
}
