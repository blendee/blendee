package org.blendee.assist;

import org.blendee.sql.ListClause;

/**
 * 句の候補を表すインターフェイスです。
 * @author 千葉 哲嗣
 */
@FunctionalInterface
public interface Offer {

	/**
	 * 句に追加されます。
	 * @param order 句内の順序
	 */
	void add(int order);

	/**
	 * 句に追加されます。
	 */
	default void add() {
		add(ListClause.DEFAULT_ORDER);
	}
}
