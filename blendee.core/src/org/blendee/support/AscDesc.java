package org.blendee.support;

import java.util.LinkedList;
import java.util.List;

/**
 * ORDER BY 句のカラムに ASC DESC を追加するための補助クラスです。
 * @author 千葉 哲嗣
 */
public class AscDesc implements Offerable, Offers<Offerable> {

	/**
	 * ORDER BY 句に、このカラムを ASC として追加します。
	 */
	public final OrderByOffer ASC;

	/**
	 * ORDER BY 句に、このカラムを DESC として追加します。
	 */
	public final OrderByOffer DESC;

	/**
	 * ORDER BY 句に、このカラムを ASC NULLS FIRST として追加します。
	 */
	public final OrderByOffer ASC_NULLS_FIRST;

	/**
	 * ORDER BY 句に、このカラムを ASC NULLS LAST として追加します。
	 */
	public final OrderByOffer ASC_NULLS_LAST;

	/**
	 * ORDER BY 句に、このカラムを DESC NULLS FIRST として追加します。
	 */
	public final OrderByOffer DESC_NULLS_FIRST;

	/**
	 * ORDER BY 句に、このカラムを DESC NULLS LAST として追加します。
	 */
	public final OrderByOffer DESC_NULLS_LAST;

	private final OrderByOffer NONE;

	/**
	 * @param asc
	 * @param desc
	 */
	AscDesc(
		OrderByOffer asc,
		OrderByOffer desc,
		OrderByOffer ascNullsFirst,
		OrderByOffer ascNullsLast,
		OrderByOffer descNullsFirst,
		OrderByOffer descNullsLast,
		OrderByOffer none) {
		ASC = asc;
		DESC = desc;
		ASC_NULLS_FIRST = ascNullsFirst;
		ASC_NULLS_LAST = ascNullsLast;
		DESC_NULLS_FIRST = descNullsFirst;
		DESC_NULLS_LAST = descNullsLast;
		NONE = none;
	}

	@Override
	public void offer(int order) {
		NONE.offer(order);
	}

	@Override
	public List<Offerable> get() {
		List<Offerable> offers = new LinkedList<>();
		offers.add(this);
		return offers;
	}
}
