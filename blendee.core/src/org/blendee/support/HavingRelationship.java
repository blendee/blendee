package org.blendee.support;

import static org.blendee.support.RelationshipConstants.AVG_TEMPLATE;
import static org.blendee.support.RelationshipConstants.COUNT_TEMPLATE;
import static org.blendee.support.RelationshipConstants.MAX_TEMPLATE;
import static org.blendee.support.RelationshipConstants.MIN_TEMPLATE;
import static org.blendee.support.RelationshipConstants.SUM_TEMPLATE;

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
public interface HavingRelationship extends CriteriaRelationship {

	/**
	 * GROUP BY 句用 AVG(column)
	 * @param <O> operator
	 * @param column {@link HavingColumn}
	 * @return {@link LogicalOperators}
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> AVG(HavingColumn<O> column) {
		return any(AVG_TEMPLATE, column);
	}

	/**
	 * GROUP BY 句用 SUM(column)
	 * @param <O> operator
	 * @param column {@link HavingColumn}
	 * @return {@link LogicalOperators}
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> SUM(HavingColumn<O> column) {
		return any(SUM_TEMPLATE, column);
	}

	/**
	 * GROUP BY 句用 MAX(column)
	 * @param <O> operator
	 * @param column {@link HavingColumn}
	 * @return {@link LogicalOperators}
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> MAX(HavingColumn<O> column) {
		return any(MAX_TEMPLATE, column);
	}

	/**
	 * GROUP BY 句用 MIN(column)
	 * @param <O> operator
	 * @param column {@link HavingColumn}
	 * @return {@link LogicalOperators}
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> MIN(HavingColumn<O> column) {
		return any(MIN_TEMPLATE, column);
	}

	/**
	 * GROUP BY 句用 COUNT(column)
	 * @param <O> operator
	 * @param column {@link HavingColumn}
	 * @return {@link LogicalOperators}
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> COUNT(HavingColumn<O> column) {
		return any(COUNT_TEMPLATE, column);
	}

	/**
	 * COALESCE を追加します。
	 * @param columns 対象カラム
	 * @param values カラム以外の要素
	 * @return カラム
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> COALESCE(Vargs<HavingColumn<O>> columns, Object... values) {
		List<Column> list = new LinkedList<>();
		columns.stream().forEach(c -> list.add(c.column()));

		Arrays.stream(values).forEach(v -> {
			list.add(new PseudoColumn(getRelationship(), v.toString(), false));
		});

		int size = list.size();
		return new HavingColumn<>(
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
	default <O extends LogicalOperators<?>> HavingColumn<O> COALESCE(HavingColumn<O> column, Object... values) {
		List<Column> list = new LinkedList<>();
		list.add(column.column());

		Arrays.stream(values).forEach(v -> {
			list.add(new PseudoColumn(getRelationship(), v.toString(), false));
		});

		int size = list.size();
		return new HavingColumn<>(
			getRoot(),
			getContext(),
			new MultiColumn(Coalesce.createTemplate(size), list.toArray(new Column[size])));
	}

	/**
	 * COALESCE を追加します。
	 * @param columns 対象カラム
	 * @return カラム
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> COALESCE(Vargs<HavingColumn<O>> columns) {
		return any(Coalesce.createTemplate(columns.length()), columns);
	}

	/**
	 * HAVING 句に任意のカラムを追加します。
	 * @param <O> operator
	 * @param template カラムのテンプレート
	 * @param column 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> HavingColumn<O> any(
		String template,
		HavingColumn<O> column) {
		return new HavingColumn<>(
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
	default <O extends LogicalOperators<?>> HavingColumn<O> any(
		String template,
		Vargs<HavingColumn<O>> args) {
		HavingColumn<O>[] values = args.get();
		Column[] columns = new Column[values.length];
		for (int i = 0; i < values.length; i++) {
			columns[i] = values[i].column();
		}

		return new HavingColumn<>(
			getRoot(),
			getContext(),
			new MultiColumn(template, columns));
	}
}