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
public abstract class OnQueryColumn<O extends LogicalOperators<?>> extends CriteriaQueryColumn<O> {

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 */
	@SuppressWarnings("javadoc")
	protected OnQueryColumn(QueryCriteriaContext context, Column column) {
		super(context, column);
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O eq(OnQueryColumn<?> another) {
		getContext().addCriteria(
			CriteriaFactory.createCriteria(
				"{0} = {1}",
				new Column[] { column(), another.column() },
				Bindable.EMPTY_ARRAY));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの <> 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ne(OnQueryColumn<?> another) {
		getContext().addCriteria(
			CriteriaFactory.createCriteria(
				"{0} <> {1}",
				new Column[] { column(), another.column() },
				Bindable.EMPTY_ARRAY));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの < 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O lt(OnQueryColumn<?> another) {
		getContext().addCriteria(
			CriteriaFactory.createCriteria(
				"{0} < {1}",
				new Column[] { column(), another.column() },
				Bindable.EMPTY_ARRAY));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの > 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O gt(OnQueryColumn<?> another) {
		getContext().addCriteria(
			CriteriaFactory.createCriteria(
				"{0} > {1}",
				new Column[] { column(), another.column() },
				Bindable.EMPTY_ARRAY));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの <= 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O le(OnQueryColumn<?> another) {
		getContext().addCriteria(
			CriteriaFactory.createCriteria(
				"{0} <= {1}",
				new Column[] { column(), another.column() },
				Bindable.EMPTY_ARRAY));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの >= 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ge(OnQueryColumn<?> another) {
		getContext().addCriteria(
			CriteriaFactory.createCriteria(
				"{0} >= {1}",
				new Column[] { column(), another.column() },
				Bindable.EMPTY_ARRAY));

		return logocalOperators();
	}
}
