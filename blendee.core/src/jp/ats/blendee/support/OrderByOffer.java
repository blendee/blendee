package jp.ats.blendee.support;

/**
 * ORDER BY 句の検索候補を表すインターフェイスです。
 *
 * @author 千葉 哲嗣
 */
@FunctionalInterface
public interface OrderByOffer {

	/**
	 * ORDER BY 句に追加されます。
	 */
	void offer();
}
