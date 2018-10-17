package org.blendee.support;

import org.blendee.sql.Column;

/**
 * WHERE 句に新しい条件を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 * @param <O> 論理演算用 {@link LogicalOperators}
 */
public class WhereColumn<O extends LogicalOperators<?>> extends CriteriaColumn<O> {

	private final Statement root;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 */
	@SuppressWarnings("javadoc")
	public WhereColumn(Statement root, CriteriaContext context, Column column) {
		super(root, context, column);
		this.root = root;
	}

	@SuppressWarnings("unchecked")
	@Override
	O logocalOperators() {
		return (O) root.getWhereLogicalOperators();
	}
}
