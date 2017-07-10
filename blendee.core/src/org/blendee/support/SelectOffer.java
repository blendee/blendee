package org.blendee.support;

import org.blendee.support.SelectOfferFunction.SelectOffers;

/**
 * SELECT 句の検索候補を表すインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface SelectOffer {

	/**
	 * 自身が保持するカラムを {@link SelectOffers} に追加します。
	 * @param offers ビジター
	 */
	void accept(SelectOffers offers);
}
