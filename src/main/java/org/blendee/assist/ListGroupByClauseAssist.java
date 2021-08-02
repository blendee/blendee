package org.blendee.assist;

/**
 * @author 千葉 哲嗣
 */
public interface ListGroupByClauseAssist {

	/**
	 * GROUP BY
	 * @param offers GROUP BY 句の要素
	 */
	default void GROUP_BY(Offer... offers) {
		behavior().GROUP_BY(a -> a.ls(offers));
	}

	/**
	 * @return {@link SelectStatementBehavior}
	 */
	SelectStatementBehavior<?, ?, ? extends GroupByClauseAssist, ?, ?, ?, ?, ?, ?> behavior();
}
