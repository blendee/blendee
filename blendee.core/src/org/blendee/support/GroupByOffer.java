package org.blendee.support;

import java.util.LinkedList;
import java.util.List;

/**
 * 順序付き GROUP BY 候補です。
 */
public class GroupByOffer implements Offerable, Offers<Offerable> {

	private final int order;

	private final GroupByColumn column;

	GroupByOffer(int order, GroupByColumn column) {
		this.order = order;
		this.column = column;
	}

	@Override
	public List<Offerable> get() {
		List<Offerable> offers = new LinkedList<>();
		offers.add(this);
		return offers;
	}

	@Override
	public void offer() {
		column.offer(order);
	}

	@Override
	public void offer(int order) {
		column.offer(order);
	}
}
