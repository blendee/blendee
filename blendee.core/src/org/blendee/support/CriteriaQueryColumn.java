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
public abstract class CriteriaQueryColumn<O extends LogicalOperators> {

	final QueryRelationship relationship;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param relationship 条件作成に必要な情報を持った {@link QueryRelationship}
	 */
	public CriteriaQueryColumn(QueryRelationship relationship) {
		this.relationship = relationship;
	}

	abstract Column column();

	abstract O logocalOperators();

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O eq(BigDecimal value) {
		relationship.getContext()
			.addCriteria(relationship, CriteriaFactory.create(column(), new BigDecimalBinder(value)));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O eq(boolean value) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.create(column(), new BooleanBinder(value)));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O eq(double value) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.create(column(), new DoubleBinder(value)));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O eq(float value) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.create(column(), new FloatBinder(value)));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O eq(int value) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.create(column(), new IntBinder(value)));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O eq(long value) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.create(column(), new LongBinder(value)));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O eq(String value) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.create(column(), value));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O eq(Timestamp value) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.create(column(), new TimestampBinder(value)));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O eq(UUID value) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.create(column(), new UUIDBinder(value)));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの = 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O eq(Bindable value) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.create(column(), value));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ne(BigDecimal value) {
		compare(ComparisonOperator.NE, new BigDecimalBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ne(boolean value) {
		compare(ComparisonOperator.NE, new BooleanBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ne(double value) {
		compare(ComparisonOperator.NE, new DoubleBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ne(float value) {
		compare(ComparisonOperator.NE, new FloatBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ne(int value) {
		compare(ComparisonOperator.NE, new IntBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ne(long value) {
		compare(ComparisonOperator.NE, new LongBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ne(String value) {
		compare(ComparisonOperator.NE, new StringBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ne(Timestamp value) {
		compare(ComparisonOperator.NE, new TimestampBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ne(UUID value) {
		compare(ComparisonOperator.NE, new UUIDBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;&gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ne(Bindable value) {
		compare(ComparisonOperator.NE, value);
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O lt(BigDecimal value) {
		compare(ComparisonOperator.LT, new BigDecimalBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O lt(boolean value) {
		compare(ComparisonOperator.LT, new BooleanBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O lt(double value) {
		compare(ComparisonOperator.LT, new DoubleBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O lt(float value) {
		compare(ComparisonOperator.LT, new FloatBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O lt(int value) {
		compare(ComparisonOperator.LT, new IntBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O lt(long value) {
		compare(ComparisonOperator.LT, new LongBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O lt(String value) {
		compare(ComparisonOperator.LT, new StringBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O lt(Timestamp value) {
		compare(ComparisonOperator.LT, new TimestampBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O lt(UUID value) {
		compare(ComparisonOperator.LT, new UUIDBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O lt(Bindable value) {
		compare(ComparisonOperator.LT, value);
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O gt(BigDecimal value) {
		compare(ComparisonOperator.GT, new BigDecimalBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O gt(boolean value) {
		compare(ComparisonOperator.GT, new BooleanBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O gt(double value) {
		compare(ComparisonOperator.GT, new DoubleBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O gt(float value) {
		compare(ComparisonOperator.GT, new FloatBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O gt(int value) {
		compare(ComparisonOperator.GT, new IntBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O gt(long value) {
		compare(ComparisonOperator.GT, new LongBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O gt(String value) {
		compare(ComparisonOperator.GT, new StringBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O gt(Timestamp value) {
		compare(ComparisonOperator.GT, new TimestampBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O gt(UUID value) {
		compare(ComparisonOperator.GT, new UUIDBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt; 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O gt(Bindable value) {
		compare(ComparisonOperator.GT, value);
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O le(BigDecimal value) {
		compare(ComparisonOperator.LE, new BigDecimalBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O le(boolean value) {
		compare(ComparisonOperator.LE, new BooleanBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O le(double value) {
		compare(ComparisonOperator.LE, new DoubleBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O le(float value) {
		compare(ComparisonOperator.LE, new FloatBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O le(int value) {
		compare(ComparisonOperator.LE, new IntBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O le(long value) {
		compare(ComparisonOperator.LE, new LongBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O le(String value) {
		compare(ComparisonOperator.LE, new StringBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O le(Timestamp value) {
		compare(ComparisonOperator.LE, new TimestampBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O le(UUID value) {
		compare(ComparisonOperator.LE, new UUIDBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &lt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O le(Bindable value) {
		compare(ComparisonOperator.LE, value);
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ge(BigDecimal value) {
		compare(ComparisonOperator.GE, new BigDecimalBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ge(boolean value) {
		compare(ComparisonOperator.GE, new BooleanBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ge(double value) {
		compare(ComparisonOperator.GE, new DoubleBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ge(float value) {
		compare(ComparisonOperator.GE, new FloatBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ge(int value) {
		compare(ComparisonOperator.GE, new IntBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ge(long value) {
		compare(ComparisonOperator.GE, new LongBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ge(String value) {
		compare(ComparisonOperator.GE, new StringBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ge(Timestamp value) {
		compare(ComparisonOperator.GE, new TimestampBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ge(UUID value) {
		compare(ComparisonOperator.GE, new UUIDBinder(value));
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの &gt;= 条件を追加します。
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O ge(Bindable value) {
		compare(ComparisonOperator.GE, value);
		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの条件を追加します。
	 * @see ComparisonOperator
	 * @param operator 比較演算子
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O compare(ComparisonOperator operator, Bindable value) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.create(operator, column(), value));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの LIKE 条件を追加します。
	 * @see Match
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O LIKE(String value) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.createLikeCriteria(Match.OPTIONAL, column(), value));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの LIKE 条件を追加します。
	 * @see Match
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O NOT_LIKE(String value) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.createNotLikeCriteria(Match.OPTIONAL, column(), value));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの LIKE 条件を追加します。
	 * @see Match
	 * @param type LIKE 検索の一致タイプ
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O LIKE(Match type, String value) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.createLikeCriteria(type, column(), value));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの LIKE 条件を追加します。
	 * @see Match
	 * @param type LIKE 検索の一致タイプ
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O NOT_LIKE(Match type, String value) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.createNotLikeCriteria(type, column(), value));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの IN 条件を追加します。
	 * @param values 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O IN(String... values) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.createInCriteria(column(), values));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの IN 条件を追加します。
	 * @param values 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O IN(Number... values) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.createInCriteria(column(), values));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの IN 条件を追加します。
	 * @param values 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O IN(Bindable... values) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.createInCriteria(column(), values));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの NOT IN 条件を追加します。
	 * @param values 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O NOT_IN(String... values) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.createInCriteria(column(), values));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの NOT IN 条件を追加します。
	 * @param values 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O NOT_IN(Number... values) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.createInCriteria(column(), values));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの NOT IN 条件を追加します。
	 * @param values 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O NOT_IN(Bindable... values) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.createInCriteria(column(), values));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの BETWEEN 条件を追加します。
	 * @param v1 from
	 * @param v2 to
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O BETWEEN(Number v1, Number v2) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.createBetweenCriteria(column(), v1, v2));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの BETWEEN 条件を追加します。
	 * @param v1 from
	 * @param v2 to
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O BETWEEN(String v1, String v2) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.createBetweenCriteria(column(), v1, v2));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの BETWEEN 条件を追加します。
	 * @param v1 from
	 * @param v2 to
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O BETWEEN(Binder v1, Binder v2) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.createBetweenCriteria(column(), v1, v2));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの NOT BETWEEN 条件を追加します。
	 * @param v1 from
	 * @param v2 to
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O NOT_BETWEEN(Number v1, Number v2) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.createNotBetweenCriteria(column(), v1, v2));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの NOT BETWEEN 条件を追加します。
	 * @param v1 from
	 * @param v2 to
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O NOT_BETWEEN(String v1, String v2) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.createNotBetweenCriteria(column(), v1, v2));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの NOT BETWEEN 条件を追加します。
	 * @param v1 from
	 * @param v2 to
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O NOT_BETWEEN(Binder v1, Binder v2) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.createNotBetweenCriteria(column(), v1, v2));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの IS NULL 条件を追加します。
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O IS_NULL() {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.create(NullComparisonOperator.IS_NULL, column()));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの IS NOT NULL 条件を追加します。
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O IS_NOT_NULL() {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.create(NullComparisonOperator.IS_NOT_NULL, column()));

		return logocalOperators();
	}

	/**
	 * 条件句に、このカラムの条件を追加します。
	 * @see CriteriaFactory#createCriteria(String, Column, Bindable)
	 * @param clause 条件句の元になるテンプレート
	 * @param value 検索条件の値
	 * @return 連続呼び出し用 {@link Query}
	 */
	public O add(String clause, Bindable value) {
		relationship.getContext().addCriteria(
			relationship,
			CriteriaFactory.createCriteria(clause, column(), value));

		return logocalOperators();
	}
}
