package org.blendee.support;

import org.blendee.support.SelectOfferFunction.SelectOffers;

/**
 * エイリアスをカラムに追加するための補助クラスです。
 * @author 千葉 哲嗣
 */
public class AliasOffer implements SelectOffer {

	private final ColumnExpression expression;

	AliasOffer(ColumnExpression expression) {
		this.expression = expression;
	}

	/**
	 * AS エイリアス となります。
	 * @param alias
	 * @return {@link SelectOffer}
	 */
	public SelectOffer AS(String alias) {
		expression.appendAlias(alias);
		return this;
	}

	@Override
	public void accept(SelectOffers offers) {
		offers.add(expression);
	};
}
