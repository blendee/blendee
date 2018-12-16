package org.blendee.assist;

/**
 * @author 千葉 哲嗣
 */
public interface FieldOrderByClauseAssist {

	/**
	 * ORDER BY
	 * @param offers ORDER BY 句の要素
	 */
	default void ORDER_BY(Offerable... offers) {
		behavior().ORDER_BY(a -> a.ls(offers));
	}

	/**
	 * @return {@link SelectStatementBehavior}
	 */
	SelectStatementBehavior<?, ?, ?, ?, ?, ?> behavior();
}
