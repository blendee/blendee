package org.blendee.support;

public class AscDesc {

	/**
	 * ORDER BY 句に、このカラムを ASC として追加します。
	 */
	public final OrderByOffer ASC;

	/**
	 * ORDER BY 句に、このカラムを DESC として追加します。
	 */
	public final OrderByOffer DESC;

	/**
	 * @param asc
	 * @param desc
	 */
	AscDesc(
		OrderByOffer asc,
		OrderByOffer desc) {
		ASC = asc;
		DESC = desc;
	}

}
