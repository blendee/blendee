package org.blendee.support;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 自動生成される ConcreteQueryRelationship の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface GroupByQueryRelationship {

	/**
	 * {@link GroupByOfferFunction} 内で使用する GROUP BY 句生成用メソッドです。<br>
	 * パラメータの項目と順序を GROUP BY 句に割り当てます。
	 * @param offers GROUP BY 句に含めるテーブルおよびカラム
	 * @return offers
	 */
	default Offers<Offerable> list(Offerable... offers) {
		return () -> Arrays.asList(offers);
	}

	/**
	 * 他のクエリと JOIN した際などの、最終的な順位を指定します。
	 * @param order 最終的な GROUP BY 句内での順序
	 * @param column 対象カラム
	 * @return {@link OrderedGroupByOffer}
	 */
	default OrderedGroupByOffer order(int order, GroupByQueryColumn column) {
		return new OrderedGroupByOffer(order, column);
	}

	/**
	 * 順序付き GROUP BY 候補です。
	 */
	public static class OrderedGroupByOffer implements Offerable, Offers<Offerable> {

		private final int order;

		private final GroupByQueryColumn column;

		private OrderedGroupByOffer(int order, GroupByQueryColumn column) {
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
		public void offer(int defaultOrder) {
			column.offer(order);
		}
	}
}
