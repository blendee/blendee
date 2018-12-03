package org.blendee.support;

/**
 * JOIN の対象を表すインターフェイスです。
 * @author 千葉 哲嗣
 * @param <R> joint の型
 */
public interface RightTable<R extends OnRightRelationship<?>> {

	/**
	 * JOIN の右側を構成する {@link OnRightRelationship} を返します。
	 * @return joint
	 */
	R joint();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスの大元の {@link SelectStatement}
	 */
	SelectStatement getSelectStatement();
}
