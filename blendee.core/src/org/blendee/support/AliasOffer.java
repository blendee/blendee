package org.blendee.support;

import org.blendee.support.SelectOfferFunction.SelectOffers;

public class AliasOffer implements SelectOffer {

	private final ColumnExpression expression;

	AliasOffer(ColumnExpression expression) {
		this.expression = expression;
	}

	public SelectOffer AS(String alias) {
		expression.append(" AS " + alias);
		return this;
	}

	@Override
	public void accept(SelectOffers offers) {
		offers.add(expression);
	};
}
