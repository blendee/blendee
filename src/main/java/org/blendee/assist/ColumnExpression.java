package org.blendee.assist;

import java.util.LinkedList;
import java.util.List;

import org.blendee.jdbc.ChainPreparedStatementComplementer;
import org.blendee.orm.SimpleSelectContext;
import org.blendee.sql.Column;
import org.blendee.sql.ListClause;
import org.blendee.sql.MultiColumn;
import org.blendee.sql.SelectClause;

/**
 * SELECT 句のカラム表現を完成させるための補助クラスです。<br>
 * 内部使用のためのクラスですので直接使用しないでください。
 * @author 千葉 哲嗣
 */
public class ColumnExpression implements AliasableOffer {

	private final Statement statement;

	private final StringBuilder expression = new StringBuilder();

	private final Column[] columns;

	private final ChainPreparedStatementComplementer complementer;

	private int order = ListClause.DEFAULT_ORDER;

	/**
	 * @param column {@link Column}
	 */
	ColumnExpression(Statement statement, Column column) {
		this.statement = statement;
		this.columns = new Column[] { column };
		complementer = null;
	}

	/**
	 * @param expression テンプレート
	 * @param columns {@link Column}
	 */
	ColumnExpression(Statement statement, String expression, Column... columns) {
		this.statement = statement;
		this.expression.append(expression);
		this.columns = columns;
		complementer = null;
	}

	ColumnExpression(Statement statement, String expression, Column[] columns, ChainPreparedStatementComplementer complementer) {
		this.statement = statement;
		this.columns = columns;
		this.expression.append(expression);
		this.complementer = complementer;
	}

	ColumnExpression(AssistColumn column) {
		statement = column.statement();
		columns = new Column[] { column.column() };
		this.expression.append("{0}");
		this.complementer = column;
	}

	/**
	 * AS エイリアス となります。
	 * @param alias エイリアス
	 * @return {@link SelectOffer}
	 */
	@Override
	public SelectOffer AS(String alias) {
		if (expression.length() == 0) {
			expression.append("{0} AS " + alias);
		} else {
			expression.append(" AS " + alias);
		}

		return this;
	}

	@Override
	public List<ColumnExpression> get() {
		var list = new LinkedList<ColumnExpression>();
		list.add(this);
		return list;
	};

	@Override
	public Column column() {
		return new MultiColumn(statement.getRootRealtionship(), expression.toString(), columns);
	}

	@Override
	public Statement statement() {
		return statement;
	}

	void accept(SimpleSelectContext optimizer) {
		for (var column : columns) {
			optimizer.add(column);
		}
	}

	void accept(SelectClause selectClause) {
		if (expression.length() == 0) {
			var list = new LinkedList<String>();
			for (var i = 0; i < columns.length; i++) {
				list.add("{" + i + "}");
			}

			//あえてテンプレートを作り、エイリアスを付けないようにする
			selectClause.add(order, String.join(", ", list), columns, complementer);
		} else {
			selectClause.add(order, expression.toString(), columns, complementer);
		}
	}

	void order(int order) {
		this.order = order;
	}
}
