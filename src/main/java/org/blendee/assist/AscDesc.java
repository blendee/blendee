package org.blendee.assist;

import java.util.LinkedList;
import java.util.List;

/**
 * ORDER BY 句のカラムに ASC DESC を追加するための補助クラスです。
 * @author 千葉 哲嗣
 */
public class AscDesc implements Offer, Offers<Offer> {

	/**
	 * ORDER BY 句に、このカラムを ASC として追加します。
	 */
	public final ListClauseOffer ASC;

	/**
	 * ORDER BY 句に、このカラムを DESC として追加します。
	 */
	public final ListClauseOffer DESC;

	/**
	 * ORDER BY 句に、このカラムを ASC NULLS FIRST として追加します。
	 */
	public final ListClauseOffer ASC_NULLS_FIRST;

	/**
	 * ORDER BY 句に、このカラムを ASC NULLS LAST として追加します。
	 */
	public final ListClauseOffer ASC_NULLS_LAST;

	/**
	 * ORDER BY 句に、このカラムを DESC NULLS FIRST として追加します。
	 */
	public final ListClauseOffer DESC_NULLS_FIRST;

	/**
	 * ORDER BY 句に、このカラムを DESC NULLS LAST として追加します。
	 */
	public final ListClauseOffer DESC_NULLS_LAST;

	private final ListClauseOffer NONE;

	/**
	 * @param asc
	 * @param desc
	 */
	AscDesc(
		ListClauseOffer asc,
		ListClauseOffer desc,
		ListClauseOffer ascNullsFirst,
		ListClauseOffer ascNullsLast,
		ListClauseOffer descNullsFirst,
		ListClauseOffer descNullsLast,
		ListClauseOffer none) {
		ASC = asc;
		DESC = desc;
		ASC_NULLS_FIRST = ascNullsFirst;
		ASC_NULLS_LAST = ascNullsLast;
		DESC_NULLS_FIRST = descNullsFirst;
		DESC_NULLS_LAST = descNullsLast;
		NONE = none;
	}

	@Override
	public void add(int order) {
		NONE.add(order);
	}

	@Override
	public List<Offer> get() {
		var offers = new LinkedList<Offer>();
		offers.add(this);
		return offers;
	}
}
