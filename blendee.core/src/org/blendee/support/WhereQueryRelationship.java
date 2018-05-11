package org.blendee.support;

import org.blendee.sql.Column;
import org.blendee.sql.MultiColumn;
import org.blendee.sql.TemplateColumn;

/**
 * 自動生成される ConcreteQueryRelationship の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface WhereQueryRelationship extends CriteriaQueryRelationship {

	/**
	 * WHERE 句に任意のカラムを追加します。
	 * @param <O> operator
	 * @param template カラムのテンプレート
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> WhereQueryColumn<O> any(String template) {
		return new WhereQueryColumn<>(
			getRoot(),
			getContext(),
			new MultiColumn(getRelationship(), template, Column.EMPTY_ARRAY));
	}

	/**
	 * WHERE 句に任意のカラムを追加します。
	 * @param <O> operator
	 * @param template カラムのテンプレート
	 * @param column 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> WhereQueryColumn<O> any(
		String template,
		WhereQueryColumn<O> column) {
		return new WhereQueryColumn<>(
			getRoot(),
			getContext(),
			new TemplateColumn(template, column.column()));
	}

	/**
	 * WHERE 句に任意のカラムを追加します。
	 * @param <O> operator
	 * @param template カラムのテンプレート
	 * @param args 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> WhereQueryColumn<O> any(
		String template,
		Vargs<WhereQueryColumn<O>> args) {
		WhereQueryColumn<O>[] values = args.get();
		Column[] columns = new Column[values.length];
		for (int i = 0; i < values.length; i++) {
			columns[i] = values[i].column();
		}

		return new WhereQueryColumn<>(
			getRoot(),
			getContext(),
			new MultiColumn(getRelationship(), template, columns));
	}
}