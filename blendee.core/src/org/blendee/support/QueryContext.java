package org.blendee.support;

/**
 * 自動生成される QueryRelationship クラスのインスタンスのタイプを表すインターフェイスです。
 * <br>
 * 内部使用を目的としています。
 *
 * @param <T> このインターフェイスの実装クラスに対応したカラムクラス
 * @author 千葉 哲嗣
 */
@FunctionalInterface
public interface QueryContext<T> {

	/**
	 * 参照用
	 */
	public static final QueryContext<ReferenceQueryColumn> REFERENCE = (relationship, name) -> new ReferenceQueryColumn(
		relationship,
		name);

	/**
	 * WHERE 句用
	 *
	 * @param <O> {@link Query} 実装
	 * @return WHERE 句用 QueryContext
	 */
	public static <O extends LogicalOperators> QueryContext<WhereQueryColumn<O>> newBuilder() {
		return (relationship, name) -> new WhereQueryColumn<>(relationship, name);
	}

	/**
	 * このインスタンスに対応したカラムインスタンスを生成します。
	 *
	 * @param relationship 条件作成に必要な情報を持った {@link QueryRelationship}
	 * @param name カラム名
	 * @return カラムインスタンス
	 */
	T buildQueryColumn(QueryRelationship relationship, String name);
}
