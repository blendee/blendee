package org.blendee.assist;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 順序付き GROUP BY 候補です。
 */
public class GroupByOffer implements Offer, Offers<Offer> {

	private final int order;

	private final Offer offer;

	GroupByOffer(int order, Offer offer) {
		this.order = order;
		this.offer = Objects.requireNonNull(offer);
	}

	@Override
	public List<Offer> get() {
		List<Offer> offers = new LinkedList<>();
		offers.add(this);
		return offers;
	}

	@Override
	public void add() {
		offer.add(order);
	}

	@Override
	public void add(int order) {
		offer.add(order);
	}
}
