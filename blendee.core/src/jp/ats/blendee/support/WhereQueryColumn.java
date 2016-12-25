package jp.ats.blendee.support;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import jp.ats.blendee.sql.Bindable;
import jp.ats.blendee.sql.Column;
import jp.ats.blendee.sql.ConditionFactory;
import jp.ats.blendee.sql.ConditionFactory.ComparisonOperator;
import jp.ats.blendee.sql.ConditionFactory.Match;
import jp.ats.blendee.sql.ConditionFactory.NullComparisonOperator;
import jp.ats.blendee.sql.binder.BigDecimalBinder;
import jp.ats.blendee.sql.binder.BooleanBinder;
import jp.ats.blendee.sql.binder.DoubleBinder;
import jp.ats.blendee.sql.binder.FloatBinder;
import jp.ats.blendee.sql.binder.IntBinder;
import jp.ats.blendee.sql.binder.LongBinder;
import jp.ats.blendee.sql.binder.StringBinder;
import jp.ats.blendee.sql.binder.TimestampBinder;
import jp.ats.blendee.sql.binder.UUIDBinder;

/**
 * WHERE 句に新しい条件を追加するクラスです。
 * <br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 *
 * @author 千葉 哲嗣
 * @param <Q> 連続呼び出し用 {@link Query}
 */
public class WhereQueryColumn<Q> {

	private final QueryRelationship relationship;

	private final Column column;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 *
	 * @param relationship 条件作成に必要な情報を持った {@link QueryRelationship}
	 * @param name カラム名
	 */
	public WhereQueryColumn(QueryRelationship relationship, String name) {
		this.relationship = relationship;
		column = relationship.getRelationship().getColumn(name);
	}

	/**
	 * WHERE 句に、このカラムの = 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q eq(BigDecimal value) {
		relationship.getContext()
			.addCondition(relationship, ConditionFactory.createCondition(column, new BigDecimalBinder(value)));

		return root();
	}

	/**
	 * WHERE 句に、このカラムの = 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q eq(boolean value) {
		relationship.getContext().addCondition(
			relationship,
			ConditionFactory.createCondition(column, new BooleanBinder(value)));

		return root();
	}

	/**
	 * WHERE 句に、このカラムの = 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q eq(double value) {
		relationship.getContext().addCondition(
			relationship,
			ConditionFactory.createCondition(column, new DoubleBinder(value)));

		return root();
	}

	/**
	 * WHERE 句に、このカラムの = 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q eq(float value) {
		relationship.getContext().addCondition(
			relationship,
			ConditionFactory.createCondition(column, new FloatBinder(value)));

		return root();
	}

	/**
	 * WHERE 句に、このカラムの = 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q eq(int value) {
		relationship.getContext().addCondition(
			relationship,
			ConditionFactory.createCondition(column, new IntBinder(value)));

		return root();
	}

	/**
	 * WHERE 句に、このカラムの = 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q eq(long value) {
		relationship.getContext().addCondition(
			relationship,
			ConditionFactory.createCondition(column, new LongBinder(value)));

		return root();
	}

	/**
	 * WHERE 句に、このカラムの = 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q eq(String value) {
		relationship.getContext().addCondition(
			relationship,
			ConditionFactory.createCondition(column, value));

		return root();
	}

	/**
	 * WHERE 句に、このカラムの = 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q eq(Timestamp value) {
		relationship.getContext().addCondition(
			relationship,
			ConditionFactory.createCondition(column, new TimestampBinder(value)));

		return root();
	}

	/**
	 * WHERE 句に、このカラムの = 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q eq(UUID value) {
		relationship.getContext().addCondition(
			relationship,
			ConditionFactory.createCondition(column, new UUIDBinder(value)));

		return root();
	}

	/**
	 * WHERE 句に、このカラムの = 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q eq(Bindable value) {
		relationship.getContext().addCondition(
			relationship,
			ConditionFactory.createCondition(column, value));

		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;&gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ne(BigDecimal value) {
		compare(ComparisonOperator.NE, new BigDecimalBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;&gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ne(boolean value) {
		compare(ComparisonOperator.NE, new BooleanBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;&gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ne(double value) {
		compare(ComparisonOperator.NE, new DoubleBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;&gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ne(float value) {
		compare(ComparisonOperator.NE, new FloatBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;&gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ne(int value) {
		compare(ComparisonOperator.NE, new IntBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;&gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ne(long value) {
		compare(ComparisonOperator.NE, new LongBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;&gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ne(String value) {
		compare(ComparisonOperator.NE, new StringBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;&gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ne(Timestamp value) {
		compare(ComparisonOperator.NE, new TimestampBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;&gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ne(UUID value) {
		compare(ComparisonOperator.NE, new UUIDBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;&gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ne(Bindable value) {
		compare(ComparisonOperator.NE, value);
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q lt(BigDecimal value) {
		compare(ComparisonOperator.LT, new BigDecimalBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q lt(boolean value) {
		compare(ComparisonOperator.LT, new BooleanBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q lt(double value) {
		compare(ComparisonOperator.LT, new DoubleBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q lt(float value) {
		compare(ComparisonOperator.LT, new FloatBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q lt(int value) {
		compare(ComparisonOperator.LT, new IntBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q lt(long value) {
		compare(ComparisonOperator.LT, new LongBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q lt(String value) {
		compare(ComparisonOperator.LT, new StringBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q lt(Timestamp value) {
		compare(ComparisonOperator.LT, new TimestampBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q lt(UUID value) {
		compare(ComparisonOperator.LT, new UUIDBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q lt(Bindable value) {
		compare(ComparisonOperator.LT, value);
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q gt(BigDecimal value) {
		compare(ComparisonOperator.GT, new BigDecimalBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q gt(boolean value) {
		compare(ComparisonOperator.GT, new BooleanBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q gt(double value) {
		compare(ComparisonOperator.GT, new DoubleBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q gt(float value) {
		compare(ComparisonOperator.GT, new FloatBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q gt(int value) {
		compare(ComparisonOperator.GT, new IntBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q gt(long value) {
		compare(ComparisonOperator.GT, new LongBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q gt(String value) {
		compare(ComparisonOperator.GT, new StringBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q gt(Timestamp value) {
		compare(ComparisonOperator.GT, new TimestampBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q gt(UUID value) {
		compare(ComparisonOperator.GT, new UUIDBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt; 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q gt(Bindable value) {
		compare(ComparisonOperator.GT, value);
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q le(BigDecimal value) {
		compare(ComparisonOperator.LE, new BigDecimalBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q le(boolean value) {
		compare(ComparisonOperator.LE, new BooleanBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q le(double value) {
		compare(ComparisonOperator.LE, new DoubleBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q le(float value) {
		compare(ComparisonOperator.LE, new FloatBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q le(int value) {
		compare(ComparisonOperator.LE, new IntBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q le(long value) {
		compare(ComparisonOperator.LE, new LongBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q le(String value) {
		compare(ComparisonOperator.LE, new StringBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q le(Timestamp value) {
		compare(ComparisonOperator.LE, new TimestampBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q le(UUID value) {
		compare(ComparisonOperator.LE, new UUIDBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &lt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q le(Bindable value) {
		compare(ComparisonOperator.LE, value);
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ge(BigDecimal value) {
		compare(ComparisonOperator.GE, new BigDecimalBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ge(boolean value) {
		compare(ComparisonOperator.GE, new BooleanBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ge(double value) {
		compare(ComparisonOperator.GE, new DoubleBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ge(float value) {
		compare(ComparisonOperator.GE, new FloatBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ge(int value) {
		compare(ComparisonOperator.GE, new IntBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ge(long value) {
		compare(ComparisonOperator.GE, new LongBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ge(String value) {
		compare(ComparisonOperator.GE, new StringBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ge(Timestamp value) {
		compare(ComparisonOperator.GE, new TimestampBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ge(UUID value) {
		compare(ComparisonOperator.GE, new UUIDBinder(value));
		return root();
	}

	/**
	 * WHERE 句に、このカラムの &gt;= 条件を追加します。
	 *
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q ge(Bindable value) {
		compare(ComparisonOperator.GE, value);
		return root();
	}

	/**
	 * WHERE 句に、このカラムの条件を追加します。
	 *
	 * @see ComparisonOperator
	 * @param operator 比較演算子
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q compare(ComparisonOperator operator, Bindable value) {
		relationship.getContext().addCondition(
			relationship,
			ConditionFactory.createComparisonCondition(operator, column, value));

		return root();
	}

	/**
	 * WHERE 句に、このカラムの LIKE 条件を追加します。
	 *
	 * @see Match
	 * @param type LIKE 検索の一致タイプ
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q LIKE(Match type, String value) {
		relationship.getContext().addCondition(
			relationship,
			ConditionFactory.createLikeCondition(type, column, value));

		return root();
	}

	/**
	 * WHERE 句に、このカラムの LIKE 条件を追加します。
	 *
	 * @see Match
	 * @param type LIKE 検索の一致タイプ
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q NOT_LIKE(Match type, String value) {
		relationship.getContext().addCondition(
			relationship,
			ConditionFactory.createNotLikeCondition(type, column, value));

		return root();
	}

	/**
	 * WHERE 句に、このカラムの IN 条件を追加します。
	 *
	 * @param values 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q IN(String... values) {
		relationship.getContext().addCondition(
			relationship,
			ConditionFactory.createInCondition(column, values));

		return root();
	}

	/**
	 * WHERE 句に、このカラムの IN 条件を追加します。
	 *
	 * @param values 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q IN(Bindable... values) {
		relationship.getContext().addCondition(
			relationship,
			ConditionFactory.createInCondition(column, values));

		return root();
	}

	/**
	 * WHERE 句に、このカラムの IS NULL 条件を追加します。
	 *
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q IS_NULL() {
		relationship.getContext().addCondition(
			relationship,
			ConditionFactory.createNullCondition(NullComparisonOperator.IS_NULL, column));

		return root();
	}

	/**
	 * WHERE 句に、このカラムの IS NOT NULL 条件を追加します。
	 *
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q IS_NOT_NULL() {
		relationship.getContext().addCondition(
			relationship,
			ConditionFactory.createNullCondition(NullComparisonOperator.IS_NOT_NULL, column));

		return root();
	}

	/**
	 * WHERE 句に、このカラムの条件を追加します。
	 *
	 * @see ConditionFactory#createCondition(String, Column, Bindable)
	 * @param clause WHERE 句の元になるテンプレート
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public Q add(String clause, Bindable value) {
		relationship.getContext().addCondition(
			relationship,
			ConditionFactory.createCondition(clause, column, value));

		return root();
	}

	@SuppressWarnings("unchecked")
	private Q root() {
		return (Q) relationship.getRoot();
	}
}
