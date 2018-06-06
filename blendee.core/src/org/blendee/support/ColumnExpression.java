package org.blendee.support;

import java.util.LinkedList;
import java.util.List;

import org.blendee.selector.RuntimeOptimizer;
import org.blendee.sql.Column;
import org.blendee.sql.ListQueryClause;
import org.blendee.sql.MultiColumn;
import org.blendee.sql.SelectClause;
import org.blendee.support.SelectOfferFunction.SelectOffers;

/**
 * SELECT 句のカラム表現を完成させるための補助クラスです。<br>
 * 内部使用のためのクラスですので直接使用しないでください。
 * @author 千葉 哲嗣
 */
public class ColumnExpression extends AliasableOffer {

	private final StringBuilder expression = new StringBuilder();

	private final Column[] columns;

	private int order = ListQueryClause.DEFAULT_ORDER;

	/**
	 * @param column {@link Column}
	 */
	public ColumnExpression(Column column) {
		this.columns = new Column[] { column };
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
	 * AS エイリアス となります。
	 * @param alias エイリアス
	 * @return {@link SelectOffer}
	 */
	@Override
	public SelectOffer AS(String alias) {
		appendAlias(alias);
		return this;
	}

	@Override
	public void accept(SelectOffers offers) {
		offers.add(this);
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

	void accept(RuntimeOptimizer optimizer) {
		for (Column column : columns) {
			optimizer.add(column);
		}
	}

	void accept(SelectClause selectClause) {
		if (expression.length() == 0) {
			selectClause.add(order, columns);
		} else {
			selectClause.add(order, expression.toString(), columns);
		}
	}

	void order(int order) {
		this.order = order;
	}

	void appendAlias(String alias) {
		if (expression.length() == 0) {
			expression.append("{0} AS " + alias);
		} else {
			expression.append(" AS " + alias);
		}
	}
}
