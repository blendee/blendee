package org.blendee.support;

import org.blendee.sql.Column;
import org.blendee.sql.MultiColumn;

/**
 * 自動生成される ConcreteQueryRelationship の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface WhereRelationship extends CriteriaRelationship {

	/**
	 * COALESCE を追加します。
	 * @param columns 対象カラム
	 * @return カラム
	 */
	default <O extends LogicalOperators<?>> WhereColumn<O> COALESCE(Vargs<WhereColumn<O>> columns) {
		return any(Coalesce.createTemplate(columns.length()), columns);
	}

	/**
	 * WHERE 句に任意のカラムを追加します。
	 * @param <O> operator
	 * @param template カラムのテンプレート
	 * @param column 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> WhereColumn<O> any(
		String template,
		WhereColumn<O> column) {
		return new WhereColumn<>(
			getSelectStatement(),
			getContext(),
			new MultiColumn(template, column.column()));
	}

	/**
	 * WHERE 句に任意のカラムを追加します。
	 * @param <O> operator
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
			getSelectStatement(),
			getContext(),
			new MultiColumn(template, columns));
	}
}
