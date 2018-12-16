package org.blendee.assist;

/**
 * @author 千葉 哲嗣
 */
public interface FieldGroupByClauseAssist {

	/**
	 * GROUP BY
	 * @param offers GROUP BY 句の要素
	 */
	default void GROUP_BY(Offerable... offers) {
		behavior().GROUP_BY(a -> a.ls(offers));
	}

	/**
	 * @return {@link SelectStatementBehavior}
	 */
	SelectStatementBehavior<?, ?, ?, ?, ?, ?> behavior();
}
