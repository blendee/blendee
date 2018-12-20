package org.blendee.assist;

import org.blendee.sql.Column;
import org.blendee.sql.MultiColumn;

/**
 * 自動生成される ConcreteQueryRelationship の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @param <R> 実装クラス
 * @author 千葉 哲嗣
 */
public interface WhereClauseAssist<R extends WhereClauseAssist<?>> extends CriteriaClauseAssist<R> {

	/**
	 * COALESCE を追加します。
	 * @param columns 対象カラム
	 * @param <O> {@link LogicalOperators}
	 * @return カラム
	 */
	default <O extends LogicalOperators<?>> WhereColumn<O> COALESCE(Vargs<WhereColumn<O>> columns) {
		return any(Helper.createCoalesceTemplate(columns.length()), columns);
	}

	/**
	 * WHERE 句に任意のカラムを追加します。
	 * @param <O> {@link LogicalOperators}
	 * @param template カラムのテンプレート
	 * @param column 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> WhereColumn<O> any(
		String template,
		WhereColumn<O> column) {
		return new WhereColumn<>(
			getStatement(),
			getContext(),
			new MultiColumn(template, column.column()));
	}

	/**
	 * WHERE 句に任意のカラムを追加します。
	 * @param <O> {@link LogicalOperators}
	 * @param template カラムのテンプレート
	 * @param args 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> WhereColumn<O> any(
		String template,
		Vargs<WhereColumn<O>> args) {
		WhereColumn<O>[] values = args.get();
		Column[] columns = new Column[values.length];
		for (int i = 0; i < values.length; i++) {
			columns[i] = values[i].column();
		}

		return new WhereColumn<>(
			getStatement(),
			getContext(),
			new MultiColumn(template, columns));
	}
}
