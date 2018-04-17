package org.blendee.support;

import org.blendee.sql.Bindable;
import org.blendee.sql.Column;
import org.blendee.sql.CriteriaFactory;

/**
 * WHERE 句に新しい条件を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 * @param <O> 論理演算用 {@link LogicalOperators}
 */
public class OnQueryColumn<O extends LogicalOperators<?>> extends CriteriaQueryColumn<O> {

	private final Query root;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 */
	@SuppressWarnings("javadoc")
	public OnQueryColumn(Query root, QueryCriteriaContext context, Column column) {
		super(context, column);
		this.root = root;
	}

	@SuppressWarnings("unchecked")
	@Override
	O logocalOperators() {
		return (O) root.getOnLogicalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O eq(QueryColumn another) {
		getContext().addCriteria(
			CriteriaFactory.createCriteria(
				"{0} = {1}",
				new Column[] { column(), another.getColumn() },
				Bindable.EMPTY_ARRAY));

		return logocalOperators();
	}
}
