package org.blendee.support;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import org.blendee.sql.Bindable;
import org.blendee.sql.Binder;
import org.blendee.sql.Column;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.CriteriaFactory.ComparisonOperator;
import org.blendee.sql.CriteriaFactory.Match;
import org.blendee.sql.CriteriaFactory.NullComparisonOperator;
import org.blendee.sql.binder.BigDecimalBinder;
import org.blendee.sql.binder.BooleanBinder;
import org.blendee.sql.binder.DoubleBinder;
import org.blendee.sql.binder.FloatBinder;
import org.blendee.sql.binder.IntBinder;
import org.blendee.sql.binder.LongBinder;
import org.blendee.sql.binder.StringBinder;
import org.blendee.sql.binder.TimestampBinder;
import org.blendee.sql.binder.UUIDBinder;

/**
 * 検索条件を追加する抽象規定クラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 * @param <O> 論理演算用 {@link LogicalOperators}
 */
public abstract class CriteriaColumn<O extends LogicalOperators<?>> {

	private final CriteriaContext context;

	private final Column column;

	CriteriaColumn(CriteriaContext context, Column column) {
		this.context = context;
		this.column = column;
	}

	abstract O logocalOperators();

	Column column() {
		return column;
	}

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return QueryRelationship が WHERE 句用の場合、そのタイプに応じた {@link CriteriaContext}
	 */
	CriteriaContext getContext() {
		return context;
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O eq(BigDecimal value) {
		getContext()
			.addCriteria(CriteriaFactory.create(column(), new BigDecimalBinder(value)));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O eq(boolean value) {
		getContext().addCriteria(
			CriteriaFactory.create(column(), new BooleanBinder(value)));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O eq(double value) {
		getContext().addCriteria(
			CriteriaFactory.create(column(), new DoubleBinder(value)));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O eq(float value) {
		getContext().addCriteria(
			CriteriaFactory.create(column(), new FloatBinder(value)));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O eq(int value) {
		getContext().addCriteria(
			CriteriaFactory.create(column(), new IntBinder(value)));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O eq(long value) {
		getContext().addCriteria(
			CriteriaFactory.create(column(), new LongBinder(value)));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O eq(String value) {
		getContext().addCriteria(
			CriteriaFactory.create(column(), value));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O eq(Timestamp value) {
		getContext().addCriteria(
			CriteriaFactory.create(column(), new TimestampBinder(value)));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O eq(UUID value) {
		getContext().addCriteria(
			CriteriaFactory.create(column(), new UUIDBinder(value)));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O eq(Bindable value) {
		getContext().addCriteria(
			CriteriaFactory.create(column(), value));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O eq(SelectColumn another) {
		return addAnotherColumnCriteria(another.column(), "{0} = {1}");
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O eq(CriteriaColumn<?> another) {
		return addAnotherColumnCriteria(another.column, "{0} = {1}");
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param subquery 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O eq(QueryBuilder subquery) {
		getContext().addCriteria(CriteriaFactory.createSubquery(ComparisonOperator.EQ, column, subquery.toSelectStatementBuilder()));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ne(BigDecimal value) {
		compare(ComparisonOperator.NE, new BigDecimalBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ne(boolean value) {
		compare(ComparisonOperator.NE, new BooleanBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ne(double value) {
		compare(ComparisonOperator.NE, new DoubleBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ne(float value) {
		compare(ComparisonOperator.NE, new FloatBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ne(int value) {
		compare(ComparisonOperator.NE, new IntBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ne(long value) {
		compare(ComparisonOperator.NE, new LongBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ne(String value) {
		compare(ComparisonOperator.NE, new StringBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ne(Timestamp value) {
		compare(ComparisonOperator.NE, new TimestampBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ne(UUID value) {
		compare(ComparisonOperator.NE, new UUIDBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ne(Bindable value) {
		compare(ComparisonOperator.NE, value);
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの <> 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ne(SelectColumn another) {
		return addAnotherColumnCriteria(another.column(), "{0} <> {1}");
	}

	/**
	 * 条件句に、このカラムの <> 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ne(CriteriaColumn<?> another) {
		return addAnotherColumnCriteria(another.column, "{0} <> {1}");
	}

	/**
	 * 条件句に、このカラムの <> 条件を追加します。
	 * @param subquery 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ne(QueryBuilder subquery) {
		getContext().addCriteria(CriteriaFactory.createSubquery(ComparisonOperator.NE, column, subquery.toSelectStatementBuilder()));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O lt(BigDecimal value) {
		compare(ComparisonOperator.LT, new BigDecimalBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O lt(boolean value) {
		compare(ComparisonOperator.LT, new BooleanBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O lt(double value) {
		compare(ComparisonOperator.LT, new DoubleBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O lt(float value) {
		compare(ComparisonOperator.LT, new FloatBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O lt(int value) {
		compare(ComparisonOperator.LT, new IntBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O lt(long value) {
		compare(ComparisonOperator.LT, new LongBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O lt(String value) {
		compare(ComparisonOperator.LT, new StringBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O lt(Timestamp value) {
		compare(ComparisonOperator.LT, new TimestampBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O lt(UUID value) {
		compare(ComparisonOperator.LT, new UUIDBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O lt(Bindable value) {
		compare(ComparisonOperator.LT, value);
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの < 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O lt(SelectColumn another) {
		return addAnotherColumnCriteria(another.column(), "{0} < {1}");
	}

	/**
	 * 条件句に、このカラムの < 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O lt(CriteriaColumn<?> another) {
		return addAnotherColumnCriteria(another.column, "{0} < {1}");
	}

	/**
	 * 条件句に、このカラムの < 条件を追加します。
	 * @param subquery 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O lt(QueryBuilder subquery) {
		getContext().addCriteria(CriteriaFactory.createSubquery(ComparisonOperator.LT, column, subquery.toSelectStatementBuilder()));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O gt(BigDecimal value) {
		compare(ComparisonOperator.GT, new BigDecimalBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O gt(boolean value) {
		compare(ComparisonOperator.GT, new BooleanBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O gt(double value) {
		compare(ComparisonOperator.GT, new DoubleBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O gt(float value) {
		compare(ComparisonOperator.GT, new FloatBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O gt(int value) {
		compare(ComparisonOperator.GT, new IntBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O gt(long value) {
		compare(ComparisonOperator.GT, new LongBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O gt(String value) {
		compare(ComparisonOperator.GT, new StringBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O gt(Timestamp value) {
		compare(ComparisonOperator.GT, new TimestampBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O gt(UUID value) {
		compare(ComparisonOperator.GT, new UUIDBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O gt(Bindable value) {
		compare(ComparisonOperator.GT, value);
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの > 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O gt(SelectColumn another) {
		return addAnotherColumnCriteria(another.column(), "{0} > {1}");
	}

	/**
	 * 条件句に、このカラムの > 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O gt(CriteriaColumn<?> another) {
		return addAnotherColumnCriteria(another.column, "{0} > {1}");
	}

	/**
	 * 条件句に、このカラムの > 条件を追加します。
	 * @param subquery 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O gt(QueryBuilder subquery) {
		getContext().addCriteria(CriteriaFactory.createSubquery(ComparisonOperator.GT, column, subquery.toSelectStatementBuilder()));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O le(BigDecimal value) {
		compare(ComparisonOperator.LE, new BigDecimalBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O le(boolean value) {
		compare(ComparisonOperator.LE, new BooleanBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O le(double value) {
		compare(ComparisonOperator.LE, new DoubleBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O le(float value) {
		compare(ComparisonOperator.LE, new FloatBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O le(int value) {
		compare(ComparisonOperator.LE, new IntBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O le(long value) {
		compare(ComparisonOperator.LE, new LongBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O le(String value) {
		compare(ComparisonOperator.LE, new StringBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O le(Timestamp value) {
		compare(ComparisonOperator.LE, new TimestampBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O le(UUID value) {
		compare(ComparisonOperator.LE, new UUIDBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O le(Bindable value) {
		compare(ComparisonOperator.LE, value);
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの <= 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O le(SelectColumn another) {
		return addAnotherColumnCriteria(another.column(), "{0} <= {1}");
	}

	/**
	 * 条件句に、このカラムの <= 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O le(CriteriaColumn<?> another) {
		return addAnotherColumnCriteria(another.column, "{0} <= {1}");
	}

	/**
	 * 条件句に、このカラムの <= 条件を追加します。
	 * @param subquery 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O le(QueryBuilder subquery) {
		getContext().addCriteria(CriteriaFactory.createSubquery(ComparisonOperator.LE, column, subquery.toSelectStatementBuilder()));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ge(BigDecimal value) {
		compare(ComparisonOperator.GE, new BigDecimalBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ge(boolean value) {
		compare(ComparisonOperator.GE, new BooleanBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ge(double value) {
		compare(ComparisonOperator.GE, new DoubleBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ge(float value) {
		compare(ComparisonOperator.GE, new FloatBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ge(int value) {
		compare(ComparisonOperator.GE, new IntBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ge(long value) {
		compare(ComparisonOperator.GE, new LongBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ge(String value) {
		compare(ComparisonOperator.GE, new StringBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ge(Timestamp value) {
		compare(ComparisonOperator.GE, new TimestampBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ge(UUID value) {
		compare(ComparisonOperator.GE, new UUIDBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ge(Bindable value) {
		compare(ComparisonOperator.GE, value);
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの >= 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ge(SelectColumn another) {
		return addAnotherColumnCriteria(another.column(), "{0} >= {1}");
	}

	/**
	 * 条件句に、このカラムの >= 条件を追加します。
	 * @param another 他方のカラム
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ge(CriteriaColumn<?> another) {
		return addAnotherColumnCriteria(another.column, "{0} >= {1}");
	}

	/**
	 * 条件句に、このカラムの >= 条件を追加します。
	 * @param subquery 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O ge(QueryBuilder subquery) {
		getContext().addCriteria(CriteriaFactory.createSubquery(ComparisonOperator.GE, column, subquery.toSelectStatementBuilder()));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの条件を追加します。
	 * @see ComparisonOperator
	 * @param operator 比較演算子
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O compare(ComparisonOperator operator, Bindable value) {
		getContext().addCriteria(
			CriteriaFactory.create(operator, column(), value));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの LIKE 条件を追加します。
	 * @see Match
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O LIKE(String value) {
		getContext().addCriteria(
			CriteriaFactory.createLikeCriteria(Match.OPTIONAL, column(), value));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの LIKE 条件を追加します。
	 * @see Match
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O NOT_LIKE(String value) {
		getContext().addCriteria(
			CriteriaFactory.createNotLikeCriteria(Match.OPTIONAL, column(), value));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの LIKE 条件を追加します。
	 * @see Match
	 * @param type LIKE 検索の一致タイプ
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O LIKE(Match type, String value) {
		getContext().addCriteria(
			CriteriaFactory.createLikeCriteria(type, column(), value));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの LIKE 条件を追加します。
	 * @see Match
	 * @param type LIKE 検索の一致タイプ
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O NOT_LIKE(Match type, String value) {
		getContext().addCriteria(
			CriteriaFactory.createNotLikeCriteria(type, column(), value));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの IN 条件を追加します。
	 * @param values 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O IN(String... values) {
		getContext().addCriteria(
			CriteriaFactory.createInCriteria(column(), values));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの IN 条件を追加します。
	 * @param values 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O IN(Number... values) {
		getContext().addCriteria(
			CriteriaFactory.createInCriteria(column(), values));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの IN 条件を追加します。
	 * @param values 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O IN(Timestamp... values) {
		getContext().addCriteria(
			CriteriaFactory.createInCriteria(column(), values));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの IN 条件を追加します。
	 * @param values 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O IN(Bindable... values) {
		getContext().addCriteria(
			CriteriaFactory.createInCriteria(column(), values));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの IN 条件を追加します。
	 * @param subquery 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O IN(QueryBuilder subquery) {
		getContext().addCriteria(Subquery.createCriteria(subquery.toSelectStatementBuilder(), false, column()));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの NOT IN 条件を追加します。
	 * @param values 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O NOT_IN(String... values) {
		getContext().addCriteria(
			CriteriaFactory.createNotInCriteria(column(), values));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの NOT IN 条件を追加します。
	 * @param values 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O NOT_IN(Number... values) {
		getContext().addCriteria(
			CriteriaFactory.createNotInCriteria(column(), values));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの NOT IN 条件を追加します。
	 * @param values 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O NOT_IN(Timestamp... values) {
		getContext().addCriteria(
			CriteriaFactory.createNotInCriteria(column(), values));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの NOT IN 条件を追加します。
	 * @param values 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O NOT_IN(Bindable... values) {
		getContext().addCriteria(
			CriteriaFactory.createNotInCriteria(column(), values));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの IN 条件を追加します。
	 * @param subquery 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O NOT_IN(QueryBuilder subquery) {
		getContext().addCriteria(Subquery.createCriteria(subquery.toSelectStatementBuilder(), true, column()));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの BETWEEN 条件を追加します。
	 * @param v1 from
	 * @param v2 to
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O BETWEEN(Number v1, Number v2) {
		getContext().addCriteria(
			CriteriaFactory.createBetweenCriteria(column(), v1, v2));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの BETWEEN 条件を追加します。
	 * @param v1 from
	 * @param v2 to
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O BETWEEN(String v1, String v2) {
		getContext().addCriteria(
			CriteriaFactory.createBetweenCriteria(column(), v1, v2));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの BETWEEN 条件を追加します。
	 * @param v1 from
	 * @param v2 to
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O BETWEEN(Timestamp v1, Timestamp v2) {
		getContext().addCriteria(
			CriteriaFactory.createBetweenCriteria(column(), v1, v2));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの BETWEEN 条件を追加します。
	 * @param v1 from
	 * @param v2 to
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O BETWEEN(Binder v1, Binder v2) {
		getContext().addCriteria(
			CriteriaFactory.createBetweenCriteria(column(), v1, v2));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの NOT BETWEEN 条件を追加します。
	 * @param v1 from
	 * @param v2 to
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O NOT_BETWEEN(Number v1, Number v2) {
		getContext().addCriteria(
			CriteriaFactory.createNotBetweenCriteria(column(), v1, v2));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの NOT BETWEEN 条件を追加します。
	 * @param v1 from
	 * @param v2 to
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O NOT_BETWEEN(String v1, String v2) {
		getContext().addCriteria(
			CriteriaFactory.createNotBetweenCriteria(column(), v1, v2));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの NOT BETWEEN 条件を追加します。
	 * @param v1 from
	 * @param v2 to
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O NOT_BETWEEN(Timestamp v1, Timestamp v2) {
		getContext().addCriteria(
			CriteriaFactory.createNotBetweenCriteria(column(), v1, v2));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの NOT BETWEEN 条件を追加します。
	 * @param v1 from
	 * @param v2 to
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O NOT_BETWEEN(Binder v1, Binder v2) {
		getContext().addCriteria(
			CriteriaFactory.createNotBetweenCriteria(column(), v1, v2));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの IS NULL 条件を追加します。
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O IS_NULL() {
		getContext().addCriteria(
			CriteriaFactory.create(NullComparisonOperator.IS_NULL, column()));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの IS NOT NULL 条件を追加します。
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O IS_NOT_NULL() {
		getContext().addCriteria(
			CriteriaFactory.create(NullComparisonOperator.IS_NOT_NULL, column()));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの条件を追加します。
	 * @see CriteriaFactory#createCriteria(String, Column, Bindable)
	 * @param clause 条件句の元になるテンプレート
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link QueryBuilder}
	 */
	public O add(String clause, Bindable value) {
		getContext().addCriteria(
			CriteriaFactory.createCriteria(clause, column(), value));

		return logocalOperators();
	}

	private O addAnotherColumnCriteria(Column another, String template) {
		getContext().addCriteria(
			CriteriaFactory.createCriteria(
				template,
				new Column[] { column(), another },
				Bindable.EMPTY_ARRAY));

		return logocalOperators();
	}
}
