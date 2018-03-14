package org.blendee.support;

import java.util.LinkedList;
import java.util.List;

/**
 * ORDER BY 句の検索候補を表すインターフェイスです。
 * @author 千葉 哲嗣
 */
public class OrderByOffer implements Offers<OrderByOffer> {

	private final Runnable offerFunction;

	/**
	 * @param offerFunction {@link Runnable}
	 */
	public OrderByOffer(Runnable offerFunction) {
		this.offerFunction = offerFunction;
	}

	/**
	 * ORDER BY 句に追加されます。
	 */
	public void offer() {
		offerFunction.run();
	}

	@Override
	public List<OrderByOffer> get() {
		List<OrderByOffer> offers = new LinkedList<>();
		offers.add(this);
		return offers;
	}
}
