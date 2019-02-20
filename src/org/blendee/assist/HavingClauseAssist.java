package org.blendee.assist;

import static org.blendee.assist.Helper.AVG_TEMPLATE;
import static org.blendee.assist.Helper.COUNT_TEMPLATE;
import static org.blendee.assist.Helper.MAX_TEMPLATE;
import static org.blendee.assist.Helper.MIN_TEMPLATE;
import static org.blendee.assist.Helper.SUM_TEMPLATE;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.blendee.sql.Column;
import org.blendee.sql.MultiColumn;
import org.blendee.sql.PseudoColumn;

/**
 * 自動生成される TableFacade の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @param <R> 実装クラス
 * @author 千葉 哲嗣
 */
public interface HavingClauseAssist<R extends HavingClauseAssist<?>> extends CriteriaClauseAssist<R> {

	/**
	 * GROUP BY 句用 AVG(column)
	 * @param <O> operator
	 * @param column {@link HavingColumn}
	 * @return {@link LogicalOperators}
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> AVG(CriteriaAssistColumn<O> column) {
		return any(AVG_TEMPLATE, column);
	}

	/**
	 * GROUP BY 句用 SUM(column)
	 * @param <O> operator
	 * @param column {@link HavingColumn}
	 * @return {@link LogicalOperators}
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> SUM(CriteriaAssistColumn<O> column) {
		return any(SUM_TEMPLATE, column);
	}

	/**
	 * GROUP BY 句用 MAX(column)
	 * @param <O> operator
	 * @param column {@link HavingColumn}
	 * @return {@link LogicalOperators}
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> MAX(CriteriaAssistColumn<O> column) {
		return any(MAX_TEMPLATE, column);
	}

	/**
	 * GROUP BY 句用 MIN(column)
	 * @param <O> operator
	 * @param column {@link HavingColumn}
	 * @return {@link LogicalOperators}
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> MIN(CriteriaAssistColumn<O> column) {
		return any(MIN_TEMPLATE, column);
	}

	/**
	 * GROUP BY 句用 COUNT(column)
	 * @param <O> operator
	 * @param column {@link HavingColumn}
	 * @return {@link LogicalOperators}
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> COUNT(CriteriaAssistColumn<O> column) {
		return any(COUNT_TEMPLATE, column);
	}

	/**
	 * COALESCE を追加します。
	 * @param columns 対象カラム
	 * @param values カラム以外の要素
	 * @param <O> {@link LogicalOperators}
	 * @return カラム
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> COALESCE(Vargs<CriteriaAssistColumn<O>> columns, Object... values) {
		List<Column> list = new LinkedList<>();
		columns.stream().forEach(c -> list.add(c.column()));

		Arrays.stream(values).forEach(v -> {
			list.add(new PseudoColumn(getRelationship(), v.toString(), false));
		});

		int size = list.size();
		SelectStatement statement = getSelectStatement();
		return new HavingColumn<>(
			statement,
			getContext(),
			new MultiColumn(
				statement.getRootRealtionship(),
				Helper.createCoalesceTemplate(size),
				list.toArray(new Column[size])),
			Helper.flatValues(columns.get()));
	}

	/**
	 * COALESCE を追加します。
	 * @param column 対象カラム
	 * @param values カラム以外の要素
	 * @param <O> {@link LogicalOperators}
	 * @return カラム
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> COALESCE(CriteriaAssistColumn<O> column, Object... values) {
		List<Column> list = new LinkedList<>();
		list.add(column.column());

		Arrays.stream(values).forEach(v -> {
			list.add(new PseudoColumn(getRelationship(), v.toString(), false));
		});

		int size = list.size();
		SelectStatement statement = getSelectStatement();
		return new HavingColumn<>(
			statement,
			getContext(),
			new MultiColumn(
				statement.getRootRealtionship(),
				Helper.createCoalesceTemplate(size),
				list.toArray(new Column[size])),
			column.values());
	}

	/**
	 * COALESCE を追加します。
	 * @param columns 対象カラム
	 * @param <O> {@link LogicalOperators}
	 * @return カラム
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> COALESCE(Vargs<CriteriaAssistColumn<O>> columns) {
		return any(Helper.createCoalesceTemplate(columns.length()), columns);
	}

	/**
	 * HAVING 句に任意のカラムを追加します。
	 * @param column 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> any(CriteriaAssistColumn<O> column) {
		return any("{0}", column);
	}

	/**
	 * HAVING 句に任意のカラムを追加します。
	 * @param <O> {@link LogicalOperators}
	 * @param template カラムのテンプレート
	 * @param column 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> any(
		String template,
		CriteriaAssistColumn<O> column) {
		SelectStatement statement = getSelectStatement();
		return new HavingColumn<>(
			statement,
			getContext(),
			new MultiColumn(
				statement.getRootRealtionship(),
				template,
				column.column()),
			column.values());
	}

	/**
	 * HAVING 句に任意のカラムを追加します。
	 * @param <O> {@link LogicalOperators}
	 * @param template カラムのテンプレート
	 * @param args 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> any(
		String template,
		Vargs<CriteriaAssistColumn<O>> args) {
		AssistColumn[] values = args.get();
		Column[] columns = new Column[values.length];
		for (int i = 0; i < values.length; i++) {
			columns[i] = values[i].column();
		}

		SelectStatement statement = getSelectStatement();
		return new HavingColumn<>(
			statement,
			getContext(),
			new MultiColumn(statement.getRootRealtionship(), template, columns),
			Helper.flatValues(args.get()));
	}
}
