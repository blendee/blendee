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

	private final QueryCriteriaContext context;

	private final Column column;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 */
	@SuppressWarnings("javadoc")
	public HavingQueryColumn(Query root, QueryCriteriaContext context, Column column) {
		this.root = root;
		this.context = context;
		this.column = column;
	}

	@Override
	Column column() {
		return column;
	}

	@SuppressWarnings("unchecked")
	@Override
	O logocalOperators() {
		return (O) root.getHavingLogicalOperators();
	}

	@Override
	QueryCriteriaContext getContext() {
		return context;
	}
}
