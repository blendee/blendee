package org.blendee.support;

import org.blendee.selector.RuntimeOptimizer;
import org.blendee.sql.Column;
import org.blendee.sql.SelectClause;

public class ColumnExpression {

	private final StringBuilder expression = new StringBuilder();

	private final Column[] columns;

	public ColumnExpression(Column... columns) {
		this.columns = columns;
	}

	public ColumnExpression(String expression, Column... columns) {
		this.expression.append(expression);
		this.columns = columns;
	}

	public void accept(RuntimeOptimizer optimizer) {
		for (Column column : columns) {
			optimizer.add(column);
		}
	}

	public void accept(SelectClause selectClause) {
		if (expression.length() == 0) {
			selectClause.add(columns);
		} else {
			selectClause.add(expression.toString(), columns);
		}
	}

	void appendAlias(String alias) {
		if (expression.length() == 0) {
			expression.append("{0} AS " + alias);
		} else {
			expression.append(" AS " + alias);
		}
	}
}
