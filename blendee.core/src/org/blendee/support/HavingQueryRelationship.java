package org.blendee.support;

import static org.blendee.support.QueryRelationshipConstants.AVG_TEMPLATE;
import static org.blendee.support.QueryRelationshipConstants.COUNT_TEMPLATE;
import static org.blendee.support.QueryRelationshipConstants.MAX_TEMPLATE;
import static org.blendee.support.QueryRelationshipConstants.MIN_TEMPLATE;
import static org.blendee.support.QueryRelationshipConstants.SUM_TEMPLATE;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.blendee.sql.Column;
import org.blendee.sql.MultiColumn;
import org.blendee.sql.PseudoColumn;

/**
 * 自動生成される ConcreteQueryRelationship の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface HavingQueryRelationship extends CriteriaQueryRelationship {

	/**
	 * GROUP BY 句用 AVG(column)
	 * @param <O> operator
	 * @param column {@link HavingQueryColumn}
	 * @return {@link LogicalOperators}
	 */
	default <O extends LogicalOperators<?>> HavingQueryColumn<O> AVG(HavingQueryColumn<O> column) {
		return any(AVG_TEMPLATE, column);
	}

	/**
	 * GROUP BY 句用 SUM(column)
	 * @param <O> operator
	 * @param column {@link HavingQueryColumn}
	 * @return {@link LogicalOperators}
	 */
	default <O extends LogicalOperators<?>> HavingQueryColumn<O> SUM(HavingQueryColumn<O> column) {
		return any(SUM_TEMPLATE, column);
	}

	/**
	 * GROUP BY 句用 MAX(column)
	 * @param <O> operator
	 * @param column {@link HavingQueryColumn}
	 * @return {@link LogicalOperators}
	 */
	default <O extends LogicalOperators<?>> HavingQueryColumn<O> MAX(HavingQueryColumn<O> column) {
		return any(MAX_TEMPLATE, column);
	}

	/**
	 * GROUP BY 句用 MIN(column)
	 * @param <O> operator
	 * @param column {@link HavingQueryColumn}
	 * @return {@link LogicalOperators}
	 */
	default <O extends LogicalOperators<?>> HavingQueryColumn<O> MIN(HavingQueryColumn<O> column) {
		return any(MIN_TEMPLATE, column);
	}

	/**
	 * GROUP BY 句用 COUNT(column)
	 * @param <O> operator
	 * @param column {@link HavingQueryColumn}
	 * @return {@link LogicalOperators}
	 */
	default <O extends LogicalOperators<?>> HavingQueryColumn<O> COUNT(HavingQueryColumn<O> column) {
		return any(COUNT_TEMPLATE, column);
	}

	/**
	 * COALESCE を追加します。
	 * @param columns 対象カラム
	 * @param values カラム以外の要素
	 * @return カラム
	 */
	default <O extends LogicalOperators<?>> HavingQueryColumn<O> COALESCE(Vargs<HavingQueryColumn<O>> columns, Object... values) {
		List<Column> list = new LinkedList<>();
		columns.stream().forEach(c -> list.add(c.column()));

		Arrays.stream(values).forEach(v -> {
			list.add(new PseudoColumn(getRelationship(), v.toString(), false));
		});

		int size = list.size();
		return new HavingQueryColumn<>(
			getRoot(),
			getContext(),
			new MultiColumn(Coalesce.createTemplate(size), list.toArray(new Column[size])));
	}

	/**
	 * COALESCE を追加します。
	 * @param column 対象カラム
	 * @param values カラム以外の要素
	 * @return カラム
	 */
	default <O extends LogicalOperators<?>> HavingQueryColumn<O> COALESCE(HavingQueryColumn<O> column, Object... values) {
		List<Column> list = new LinkedList<>();
		list.add(column.column());

		Arrays.stream(values).forEach(v -> {
			list.add(new PseudoColumn(getRelationship(), v.toString(), false));
		});

		int size = list.size();
		return new HavingQueryColumn<>(
			getRoot(),
			getContext(),
			new MultiColumn(Coalesce.createTemplate(size), list.toArray(new Column[size])));
	}

	/**
	 * COALESCE を追加します。
	 * @param columns 対象カラム
	 * @return カラム
	 */
	default <O extends LogicalOperators<?>> HavingQueryColumn<O> COALESCE(Vargs<HavingQueryColumn<O>> columns) {
		return any(Coalesce.createTemplate(columns.length()), columns);
	}

	/**
	 * HAVING 句に任意のカラムを追加します。
	 * @param <O> operator
	 * @param template カラムのテンプレート
	 * @param column 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> HavingQueryColumn<O> any(
		String template,
		HavingQueryColumn<O> column) {
		return new HavingQueryColumn<>(
			getRoot(),
			getContext(),
			new MultiColumn(template, column.column()));
	}

	/**
	 * HAVING 句に任意のカラムを追加します。
	 * @param <O> operator
	 * @param template カラムのテンプレート
	 * @param args 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> HavingQueryColumn<O> any(
		String template,
		Vargs<HavingQueryColumn<O>> args) {
		HavingQueryColumn<O>[] values = args.get();
		Column[] columns = new Column[values.length];
		for (int i = 0; i < values.length; i++) {
			columns[i] = values[i].column();
		}

		return new HavingQueryColumn<>(
			getRoot(),
			getContext(),
			new MultiColumn(template, columns));
	}
}
