package org.blendee.support;

import org.blendee.sql.RuntimeId;

/**
 * Blendee が生成する各文の基底インターフェイスです。
 * @author 千葉 哲嗣
 */
public interface Statement {

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
