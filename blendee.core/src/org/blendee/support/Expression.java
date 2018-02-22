package org.blendee.support;

import org.blendee.selector.RuntimeOptimizer;
import org.blendee.sql.Column;
import org.blendee.sql.SelectClause;

public class Expression {

	private final StringBuilder expression = new StringBuilder();

	private final Column[] columns;

	public Expression(Column... columns) {
		this.columns = columns;
	}

	public Expression(String expression, Column... columns) {
		this.expression.append(expression);
		this.columns = columns;
	}

	public void accept(RuntimeOptimizer optimizer) {
		for (Column column : columns) {
			optimizer.add(column);
		}
	}

	public void accept(SelectClause selectClause) {
		selectClause.add(expression.toString(), columns);
	}

	void append(String expression) {
		this.expression.append(expression);
	}
}
