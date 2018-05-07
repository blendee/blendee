package org.blendee.support;

/**
 * ORDER BY 句のカラムに ASC DESC を追加するための補助クラスです。
 * @author 千葉 哲嗣
 */
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
