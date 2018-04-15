package org.blendee.support;

/**
 * 自動生成される QueryRelationship クラスのインスタンスのタイプを表すインターフェイスです。<br>
 * 内部使用を目的としています。
 * @param <T> このインターフェイスの実装クラスに対応したカラムクラス
 * @author 千葉 哲嗣
 */
@FunctionalInterface
public interface QueryContext<T> {

	/**
	 * 参照用
	 */
	static final QueryContext<QueryColumn> OTHER = (relationship, name) -> new QueryColumn(
		relationship.getRelationship(),
		name);

	/**
	 * WHERE 句用
	 * @param <O> {@link Query} 実装
	 * @return WHERE 句用 QueryContext
	 */
	static <O extends LogicalOperators<?>> QueryContext<WhereQueryColumn<O>> newWhereBuilder() {
		return (relationship, name) -> new WhereQueryColumn<>(
			relationship.getRoot(),
			relationship.getContext(),
			relationship.getRelationship().getColumn(name));
	}

	/**
	 * HAVING 句用
	 * @param <O> {@link Query} 実装
	 * @return HAVING 句用 QueryContext
	 */
	static <O extends LogicalOperators<?>> QueryContext<HavingQueryColumn<O>> newHavingBuilder() {
		return (relationship, name) -> new HavingQueryColumn<>(
			relationship.getRoot(),
			relationship.getContext(),
			relationship.getRelationship().getColumn(name));
	}

	/**
	 * このインスタンスに対応したカラムインスタンスを生成します。
	 * @param relationship 条件作成に必要な情報を持った {@link QueryRelationship}
	 * @param name カラム名
	 * @return カラムインスタンス
	 */
	T buildQueryColumn(QueryRelationship relationship, String name);
}
