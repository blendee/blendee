package org.blendee.support;

import java.util.LinkedList;
import java.util.List;

import org.blendee.jdbc.ChainPreparedStatementComplementer;
import org.blendee.selector.RuntimeOptimizer;
import org.blendee.sql.Column;
import org.blendee.sql.ListClause;
import org.blendee.sql.MultiColumn;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.SelectClause;

/**
 * SELECT 句のカラム表現を完成させるための補助クラスです。<br>
 * 内部使用のためのクラスですので直接使用しないでください。
 * @author 千葉 哲嗣
 */
public class ColumnExpression extends AliasableOffer {

	private final RuntimeId id;

	private final StringBuilder expression = new StringBuilder();

	private final Column[] columns;

	private final ChainPreparedStatementComplementer complementer;

	private int order = ListClause.DEFAULT_ORDER;

	/**
	 * @param column {@link Column}
	 */
	ColumnExpression(RuntimeId id, Column column) {
		this.id = id;
		this.columns = new Column[] { column };
		complementer = null;
	}

	/**
	 * @param expression テンプレート
	 * @param columns {@link Column}
	 */
	ColumnExpression(RuntimeId id, String expression, Column... columns) {
		this.id = id;
		this.expression.append(expression);
		this.columns = columns;
		complementer = null;
	}

	ColumnExpression(RuntimeId id, String expression, Column[] columns, ChainPreparedStatementComplementer complementer) {
		this.id = id;
		this.columns = columns;
		this.expression.append(expression);
		this.complementer = complementer;
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
		LinkedList<ColumnExpression> list = new LinkedList<>();
		list.add(this);
		return list;
	};

	@Override
	public Column column() {
		return new MultiColumn(expression.toString(), columns);
	}

	@Override
	public RuntimeId runtimeId() {
		return id;
	}

	void accept(RuntimeOptimizer optimizer) {
		for (Column column : columns) {
			optimizer.add(column);
		}
	}

	void accept(SelectClause selectClause) {
		if (expression.length() == 0) {
			List<String> list = new LinkedList<>();
			for (int i = 0; i < columns.length; i++) {
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
