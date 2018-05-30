package org.blendee.support;

import org.blendee.sql.ListQueryClause;

/**
 * 句の候補を表すインターフェイスです。
 * @author 千葉 哲嗣
 */
@FunctionalInterface
public interface Offerable {

	/**
	 * 句に追加されます。
	 * @param order 句内の順序
	 */
	void offer(int order);

	/**
	 * 句に追加されます。
	 */
	default void offer() {
		offer(ListQueryClause.DEFAULT_ORDER);
	}
}
