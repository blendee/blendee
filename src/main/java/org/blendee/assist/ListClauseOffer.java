package org.blendee.assist;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.blendee.sql.ListClause;

/**
 * ORDER BY 句の検索候補を表すインターフェイスです。
 * @author 千葉 哲嗣
 */
public class ListClauseOffer implements Offer, Offers<Offer> {

	private final Consumer<Integer> offerFunction;

	private final Offer offerable;

	private final int order;

	/**
	 * @param offerFunction {@link Runnable}
	 */
	ListClauseOffer(Consumer<Integer> offerFunction) {
		this.offerFunction = Objects.requireNonNull(offerFunction);
		order = ListClause.DEFAULT_ORDER;
		offerable = null;
	}

	ListClauseOffer(Offer offerable, int order) {
		this.offerable = Objects.requireNonNull(offerable);
		this.order = order;
		offerFunction = null;
	}

	/**
	 * ORDER BY 句に追加されます。
	 */
	@Override
	public void add() {
		if (offerable != null) {
			offerable.add(order);
		} else {
			offerFunction.accept(order);
		}
	}

	@Override
	public void add(int order) {
		offerFunction.accept(order);
	}

	@Override
	public List<Offer> get() {
		var offers = new LinkedList<Offer>();
		offers.add(this);
		return offers;
	}
}
