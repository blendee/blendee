package org.blendee.support;

import org.blendee.sql.Column;

/**
 * HAVING 句に新しい条件を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 * @param <O> 論理演算用 {@link LogicalOperators}
 */
public class HavingQueryColumn<O extends LogicalOperators<?>> extends CriteriaQueryColumn<O> {

	private final Query root;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 */
	@SuppressWarnings("javadoc")
	public HavingQueryColumn(Query root, QueryCriteriaContext context, Column column) {
		super(context, column);
		this.root = root;
	}

	@SuppressWarnings("unchecked")
	@Override
	O logocalOperators() {
		return (O) root.getHavingLogicalOperators();
	}
}
