package org.blendee.support;

/**
 * HAVING 句に新しい条件を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 * @param <O> 論理演算用 {@link LogicalOperators}
 */
public class HavingQueryColumn<O extends LogicalOperators> extends CriteriaQueryColumn<O> {

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param relationship 条件作成に必要な情報を持った {@link QueryRelationship}
	 * @param name カラム名
	 */
	public HavingQueryColumn(QueryRelationship relationship, String name) {
		super(relationship, name);
	}

	@SuppressWarnings("unchecked")
	@Override
	O logocalOperators() {
		return (O) relationship.getRoot().getHavingLogicalOperators();
	}
}
