package org.blendee.assist;

import org.blendee.sql.Binder;
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
	 * @param root {@link SelectStatement}
	 * @param context {@link CriteriaContext}
	 * @param column {@link Column}
	 * @param values {@link Binder}
	 */
	public WhereColumn(Statement root, CriteriaContext context, Column column, Binder[] values) {
		super(root, context, column, values);
		this.root = root;
	}

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param root {@link SelectStatement}
	 * @param context {@link CriteriaContext}
	 * @param column {@link Column}
	 */
	public WhereColumn(Statement root, CriteriaContext context, Column column) {
		this(root, context, column, Binder.EMPTY_ARRAY);
	}

	@SuppressWarnings("unchecked")
	@Override
	O logocalOperators() {
		return (O) root.getWhereLogicalOperators();
	}
}
