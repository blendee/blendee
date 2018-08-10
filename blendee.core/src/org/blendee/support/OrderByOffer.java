package org.blendee.support;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.blendee.sql.ListClause;

/**
 * ORDER BY 句の検索候補を表すインターフェイスです。
 * @author 千葉 哲嗣
 */
public class OrderByOffer implements Offerable, Offers<Offerable> {

	private final Consumer<Integer> offerFunction;

	private final Offerable offerable;

	private final int order;

	/**
	 * @param offerFunction {@link Runnable}
	 */
	OrderByOffer(Consumer<Integer> offerFunction) {
		this.offerFunction = offerFunction;
		order = ListClause.DEFAULT_ORDER;
		offerable = null;
	}

	OrderByOffer(Offerable offerable, int order) {
		this.offerable = offerable;
		this.order = order;
		offerFunction = null;
	}

	/**
	 * ORDER BY 句に追加されます。
	 */
	@Override
	public void offer() {
		if (offerable != null) {
			offerable.offer(order);
		} else {
			offerFunction.accept(order);
		}
	}

	@Override
	public void offer(int order) {
		offerFunction.accept(order);
	}

	@Override
	public List<Offerable> get() {
		List<Offerable> offers = new LinkedList<>();
		offers.add(this);
		return offers;
	}
}
