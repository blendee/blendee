package org.blendee.support;

import org.blendee.sql.Column;

/**
 * ON 句に新しい条件を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 * @param <O> 論理演算用 {@link LogicalOperators}
 */
public class OnRightColumn<O extends LogicalOperators<?>> extends OnColumn<O> {

	private final SelectStatement root;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 */
	@SuppressWarnings("javadoc")
	public OnRightColumn(SelectStatement root, CriteriaContext context, Column column) {
		super(context, column);
		this.root = root;
	}

	@SuppressWarnings("unchecked")
	@Override
	O logocalOperators() {
		return (O) root.getOnRightLogicalOperators();
	}
}
