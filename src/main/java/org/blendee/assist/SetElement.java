package org.blendee.assist;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.blendee.sql.Bindable;
import org.blendee.sql.Binder;
import org.blendee.sql.Column;
import org.blendee.sql.UpdateDMLBuilder;
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
 * UPDATE 文に新しい要素を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 */
public class SetElement {

	private static final String singleTemplate = "?";

	private final LinkedList<Column> columns = new LinkedList<>();

	private final TableFacadeAssist assist;

	private final List<Binder> binders = new LinkedList<>();

	private String template;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param assist 条件作成に必要な情報を持った {@link TableFacadeAssist}
	 */
	public SetElement(TableFacadeAssist assist) {
		this.assist = assist;
	}

	/**
	 * UPDATE SET 句に、このカラムへの代入を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public SetProof set(BigDecimal value) {
		return set(new BigDecimalBinder(value));
	}

	/**
	 * UPDATE SET 句に、このカラムへの代入を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public SetProof set(boolean value) {
		return set(new BooleanBinder(value));
	}

	/**
	 * UPDATE SET 句に、このカラムへの代入を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public SetProof set(double value) {
		return set(new DoubleBinder(value));
	}

	/**
	 * UPDATE SET 句に、このカラムへの代入を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public SetProof set(float value) {
		return set(new FloatBinder(value));
	}

	/**
	 * UPDATE SET 句に、このカラムへの代入を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public SetProof set(int value) {
		return set(new IntBinder(value));
	}

	/**
	 * UPDATE SET 句に、このカラムへの代入を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public SetProof set(long value) {
		return set(new LongBinder(value));
	}

	/**
	 * UPDATE SET 句に、このカラムへの代入を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public SetProof set(String value) {
		return set(new StringBinder(value));
	}

	/**
	 * UPDATE SET 句に、このカラムへの代入を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public SetProof set(Timestamp value) {
		return set(new TimestampBinder(value));
	}

	/**
	 * UPDATE SET 句に、このカラムへの代入を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public SetProof set(UUID value) {
		return set(new UUIDBinder(value));
	}

	/**
	 * UPDATE SET 句に、このカラムへの代入を追加します。
	 * @param value 代入値
	 * @return {@link SetProof}
	 */
	public SetProof set(Bindable value) {
		binders.clear();
		binders.add(value.toBinder());
		template = singleTemplate;

		assist.getDataManipulationStatement().addSetElement(this);

		return SetProof.singleton;
	}

	/**
	 * UPDATE SET 句に、このカラムへの代入を追加します。
	 * @param values テンプレートにセットする値
	 * @return {@link SetProof}
	 */
	public SetProof set(Object... values) {
		binders.clear();
		var extractor = new BinderExtractor();
		Arrays.stream(values).forEach(v -> {
			binders.add(extractor.extract(v));
		});

		template = Stream.generate(() -> singleTemplate).limit(values.length).collect(Collectors.joining(", "));

		assist.getDataManipulationStatement().addSetElement(this);

		return SetProof.singleton;
	}

	/**
	 * UPDATE SET 句に、このカラムへの代入を追加します。
	 * @param template テンプレート
	 * @param values テンプレートにセットする値
	 * @return {@link SetProof}
	 */
	public SetProof set(String template, Vargs<Object> values) {
		binders.clear();
		var extractor = new BinderExtractor();
		Arrays.stream(values.get()).forEach(v -> {
			binders.add(extractor.extract(v));
		});

		this.template = template;

		assist.getDataManipulationStatement().addSetElement(this);

		return SetProof.singleton;
	}

	/**
	 * UPDATE SET 句に、このカラムへの代入を追加します。
	 * @param template テンプレート
	 * @param columns テンプレートにセットするカラム
	 * @param values テンプレートにセットする値
	 * @return {@link SetProof}
	 */
	public SetProof set(String template, Vargs<? extends UpdateColumn> columns, Vargs<Object> values) {
		binders.clear();
		var extractor = new BinderExtractor();
		Arrays.stream(values.get()).forEach(v -> {
			binders.add(extractor.extract(v));
		});

		var columnNames = columns.stream().map(c -> c.column().getName()).collect(Collectors.toList());
		this.template = MessageFormat.format(template, columnNames.toArray());

		assist.getDataManipulationStatement().addSetElement(this);

		return SetProof.singleton;
	}

	/**
	 * UPDATE SET 句に、 SQL の断片を追加します。
	 * @param sqlFragment SQL の断片
	 * @return {@link SetProof}
	 */
	public SetProof setAny(String sqlFragment) {
		binders.clear();
		this.template = sqlFragment;

		assist.getDataManipulationStatement().addSetElement(this);

		return SetProof.singleton;
	}

	/**
	 * UPDATE SET 句に、このカラムへの代入を追加します。
	 * @param subquery 代入値のためのサブクエリ
	 * @return {@link SetProof}
	 */
	public SetProof set(SelectStatement subquery) {
		binders.clear();

		subquery.forSubquery(true);

		var builder = subquery.toSQLQueryBuilder();
		template = "(" + builder.sql() + ")";
		binders.addAll(Arrays.asList(builder.currentBinders()));

		assist.getDataManipulationStatement().addSetElement(this);

		return SetProof.singleton;
	}

	/**
	 * 内部使用
	 * @param column {@link Column}
	 */
	public void addColumn(Column column) {
		columns.add(column);
	}

	void appendTo(UpdateDMLBuilder builder) {
		String columnPart;
		if (columns.size() > 1) {
			columnPart = "(" + columns.stream().map(c -> c.getName()).collect(Collectors.joining(", ")) + ")";
		} else {
			columnPart = columns.peekFirst().getName();
		}

		builder.addBindableSQLFragment(columnPart, template, binders.toArray(new Binder[binders.size()]));
	}
}
