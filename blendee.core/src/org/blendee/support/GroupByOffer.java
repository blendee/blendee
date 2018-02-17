package org.blendee.support;

/**
 * GROUP BY 句の検索候補を表すインターフェイスです。
 * @author 千葉 哲嗣
 */
@FunctionalInterface
public interface GroupByOffer {

	/**
	 * ORDER BY 句に追加されます。
	 */
	void offer();
}
