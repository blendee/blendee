package org.blendee.support;

import org.blendee.sql.Column;

/**
 * ON 句に新しい条件を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 * @param <O> 論理演算用 {@link LogicalOperators}
 */
public class OnRightQueryColumn<O extends LogicalOperators<?>> extends OnQueryColumn<O> {

	private final Query root;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 */
	@SuppressWarnings("javadoc")
	public OnRightQueryColumn(Query root, QueryCriteriaContext context, Column column) {
		super(context, column);
		this.root = root;
	}

	@SuppressWarnings("unchecked")
	@Override
	O logocalOperators() {
		return (O) root.getOnRightLogicalOperators();
	}
}
