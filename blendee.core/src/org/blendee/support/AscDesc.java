package org.blendee.support;

/**
 * ORDER BY 句のカラムに ASC DESC を追加するための補助クラスです。
 * @author 千葉 哲嗣
 */
public class AscDesc implements Offerable {

	/**
	 * ORDER BY 句に、このカラムを ASC として追加します。
	 */
	public final OrderByOffer ASC;

	/**
	 * ORDER BY 句に、このカラムを DESC として追加します。
	 */
	public final OrderByOffer DESC;

	private final OrderByOffer NONE;

	/**
	 * @param asc
	 * @param desc
	 */
	AscDesc(
		OrderByOffer asc,
		OrderByOffer desc,
		OrderByOffer none) {
		ASC = asc;
		DESC = desc;
		NONE = none;
	}

	@Override
	public void offer(int order) {
		NONE.offer(order);
	}
}
