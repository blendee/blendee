package org.blendee.assist;

import org.blendee.sql.Column;
import org.blendee.sql.MultiColumn;

/**
 * 自動生成される TableFacade の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @param <R> 実装クラス
 * @author 千葉 哲嗣
 */
public interface OnLeftClauseAssist<R extends OnLeftClauseAssist<?>> extends CriteriaClauseAssist<R> {

	/**
	 * COALESCE を追加します。
	 * @param <O> operator
	 * @param columns 対象カラム
	 * @return カラム
	 */
	default <O extends LogicalOperators<?>> OnLeftColumn<O> COALESCE(Vargs<CriteriaAssistColumn<O>> columns) {
		return any(Helper.createCoalesceTemplate(columns.length()), columns);
	}

	/**
	 * ON 句に任意のカラムを追加します。
	 * @param column 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> OnLeftColumn<O> any(CriteriaAssistColumn<O> column) {
		return any("{0}", column);
	}

	/**
	 * ON 句に任意のカラムを追加します。
	 * @param <O> operator
	 * @param template カラムのテンプレート
	 * @param column 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> OnLeftColumn<O> any(
		String template,
		CriteriaAssistColumn<O> column) {
		SelectStatement statement = getSelectStatement();
		return new OnLeftColumn<>(
			statement,
			getContext(),
			new MultiColumn(
				statement.getRootRealtionship(),
				template,
				column.column()),
			column.values());
	}

	/**
	 * ON 句に任意のカラムを追加します。
	 * @param <O> {@link LogicalOperators}
	 * @param template カラムのテンプレート
	 * @param column1 使用するカラム
	 * @param column2 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> OnLeftColumn<O> any(
		String template,
		CriteriaAssistColumn<O> column1,
		CriteriaAssistColumn<O> column2) {
		return any(template, Vargs.of(column1, column2));
	}

	/**
	 * ON 句に任意のカラムを追加します。
	 * @param <O> {@link LogicalOperators}
	 * @param template カラムのテンプレート
	 * @param column1 使用するカラム
	 * @param column2 使用するカラム
	 * @param column3 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> OnLeftColumn<O> any(
		String template,
		CriteriaAssistColumn<O> column1,
		CriteriaAssistColumn<O> column2,
		CriteriaAssistColumn<O> column3) {
		return any(template, Vargs.of(column1, column2, column3));
	}

	/**
	 * ON 句に任意のカラムを追加します。
	 * @param <O> {@link LogicalOperators}
	 * @param template カラムのテンプレート
	 * @param column1 使用するカラム
	 * @param column2 使用するカラム
	 * @param column3 使用するカラム
	 * @param column4 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> OnLeftColumn<O> any(
		String template,
		CriteriaAssistColumn<O> column1,
		CriteriaAssistColumn<O> column2,
		CriteriaAssistColumn<O> column3,
		CriteriaAssistColumn<O> column4) {
		return any(template, Vargs.of(column1, column2, column3, column4));
	}

	/**
	 * ON 句に任意のカラムを追加します。
	 * @param <O> operator
	 * @param template カラムのテンプレート
	 * @param args 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> OnLeftColumn<O> any(
		String template,
		Vargs<CriteriaAssistColumn<O>> args) {
		AssistColumn[] values = args.get();
		Column[] columns = new Column[values.length];
		for (int i = 0; i < values.length; i++) {
			columns[i] = values[i].column();
		}

		SelectStatement statement = getSelectStatement();
		return new OnLeftColumn<>(
			statement,
			getContext(),
			new MultiColumn(
				statement.getRootRealtionship(),
				template,
				columns),
			Helper.flatValues(args.get()));
	}
}
