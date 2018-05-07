package org.blendee.support;

import org.blendee.selector.RuntimeOptimizer;
import org.blendee.sql.Column;
import org.blendee.sql.ListQueryClause;
import org.blendee.sql.SelectClause;

/**
 * SELECT 句のカラム表現を完成させるための補助クラスです。<br>
 * 内部使用のためのクラスですので直接使用しないでください。
 * @author 千葉 哲嗣
 */
public class ColumnExpression {

	private final StringBuilder expression = new StringBuilder();

	private final Column[] columns;

	private int order = ListQueryClause.DEFAULT_ORDER;

	/**
	 * @param columns {@link Column}
	 */
	public ColumnExpression(Column... columns) {
		this.columns = columns;
	}

	/**
	 * @param expression テンプレート
	 * @param columns {@link Column}
	 */
	public ColumnExpression(String expression, Column... columns) {
		this.expression.append(expression);
		this.columns = columns;
	}

	/**
	 * @param optimizer {@link RuntimeOptimizer}
	 */
	public void accept(RuntimeOptimizer optimizer) {
		for (Column column : columns) {
			optimizer.add(column);
		}
	}

	/**
	 * @param selectClause SELECT 句
	 */
	public void accept(SelectClause selectClause) {
		if (expression.length() == 0) {
			selectClause.add(order, columns);
		} else {
			selectClause.add(order, expression.toString(), columns);
		}
	}

	void appendAlias(String alias) {
		if (expression.length() == 0) {
			expression.append("{0} AS " + alias);
		} else {
			expression.append(" AS " + alias);
		}
	}

	void order(int order) {
		this.order = order;
	}
}
