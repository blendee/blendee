package org.blendee.assist;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.blendee.sql.Bindable;
import org.blendee.sql.Binder;
import org.blendee.sql.Column;
import org.blendee.sql.InsertDMLBuilder;
import org.blendee.sql.SQLQueryBuilder;
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
 * INSERT 文の VALUES 句の値を個別にセットするためのサポートクラスです。
 * @author 千葉 哲嗣
 */
public class Values {

	private final InsertDMLBuilder builder;

	private final LinkedList<Column> columns;

	Values(InsertDMLBuilder builder, List<Column> columns) {
		this.builder = builder;
		this.columns = new LinkedList<>(columns);
	}

	/**
	 * INSERT VALUES 句に値を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public Values value(BigDecimal value) {
		return value(new BigDecimalBinder(value));
	}

	/**
	 * INSERT VALUES 句に値を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public Values value(boolean value) {
		return value(new BooleanBinder(value));
	}

	/**
	 * INSERT VALUES 句に値を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public Values value(double value) {
		return value(new DoubleBinder(value));
	}

	/**
	 * INSERT VALUES 句に値を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public Values value(float value) {
		return value(new FloatBinder(value));
	}

	/**
	 * INSERT VALUES 句に値を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public Values value(int value) {
		return value(new IntBinder(value));
	}

	/**
	 * INSERT VALUES 句に値を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public Values value(long value) {
		return value(new LongBinder(value));
	}

	/**
	 * INSERT VALUES 句に値を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public Values value(String value) {
		return value(new StringBinder(value));
	}

	/**
	 * INSERT VALUES 句に値を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public Values value(Timestamp value) {
		return value(new TimestampBinder(value));
	}

	/**
	 * INSERT VALUES 句に値を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public Values value(UUID value) {
		return value(new UUIDBinder(value));
	}

	/**
	 * INSERT VALUES 句に値を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public Values value(Bindable value) {
		builder.add(columnName(), value);

		return this;
	}

	/**
	 * INSERT VALUES 句に値を追加します。
	 * @param template テンプレート
	 * @param values テンプレートにセットする値
	 * @return {@link SetProof}
	 */
	public Values any(String template, Object... values) {
		List<Binder> binders = new LinkedList<>();
		BinderExtractor extractor = new BinderExtractor();
		Arrays.stream(values).forEach(v -> {
			binders.add(extractor.extract(v));
		});

		builder.addBindableSQLFragment(
			columnName(),
			template,
			binders.toArray(new Bindable[binders.size()]));

		return this;
	}

	/**
	 * INSERT VALUES 句に値を追加します。
	 * @param sqlFragment SQL の断片
	 * @return {@link SetProof}
	 */
	public Values any(String sqlFragment) {
		builder.addSQLFragment(columnName(), sqlFragment);

		return this;
	}

	/**
	 * UPDATE SET 句に、このカラムへの代入を追加します。
	 * @param subquery 代入値のためのサブクエリ
	 * @return {@link SetProof}
	 */
	public Values value(SelectStatement subquery) {
		subquery.forSubquery(true);

		SQLQueryBuilder queryBuilder = subquery.toSQLQueryBuilder();

		Arrays.asList(queryBuilder.currentBinders());

		builder.addBindableSQLFragment(
			columnName(),
			"(" + queryBuilder.sql() + ")",
			queryBuilder.currentBinders());

		return this;
	}

	private String columnName() {
		//カラム数と値の数が違います
		if (columns.size() == 0) throw new IllegalStateException("The number of columns and the number of values ​​are different.");
		return columns.pop().getName();
	}
}
