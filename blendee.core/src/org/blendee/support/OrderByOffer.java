package org.blendee.support;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.blendee.sql.ListQueryClause;

/**
 * ORDER BY 句の検索候補を表すインターフェイスです。
 * @author 千葉 哲嗣
 */
public class OrderByOffer implements Offers<OrderByOffer> {

	private final Consumer<Integer> offerFunction;

	private final int order;

	/**
	 * @param offerFunction {@link Runnable}
	 */
	OrderByOffer(Consumer<Integer> offerFunction) {
		this.offerFunction = offerFunction;
		order = ListQueryClause.DEFAULT_ORDER;
	}

	OrderByOffer(OrderByOffer offer, int order) {
		this.offerFunction = offer.offerFunction;
		this.order = order;
	}

	/**
	 * ORDER BY 句に追加されます。
	 */
	public void offer() {
		offerFunction.accept(order);
	}

	@Override
	public List<OrderByOffer> get() {
		List<OrderByOffer> offers = new LinkedList<>();
		offers.add(this);
		return offers;
	}
}
