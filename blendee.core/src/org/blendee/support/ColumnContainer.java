package org.blendee.support;

import org.blendee.selector.RuntimeOptimizer;
import org.blendee.sql.Column;
import org.blendee.sql.SelectClause;

public class ColumnContainer {

	private final String clause;

	private final Column column;

	public ColumnContainer(Column column) {
		this.column = column;
		clause = null;
	}

	public ColumnContainer(String clause, Column column) {
		this.clause = clause;
		this.column = column;
	}

	public void accept(RuntimeOptimizer optimizer) {
		optimizer.add(column);
	}

	public void accept(SelectClause selectClause) {
		selectClause.add(clause, column);
	}
}
