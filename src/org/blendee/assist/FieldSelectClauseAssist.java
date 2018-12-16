package org.blendee.assist;

/**
 * @author 千葉 哲嗣
 */
public interface FieldSelectClauseAssist {

	/**
	 * SELECT
	 * @param offers SELECT 句の要素
	 */
	default void SELECT(SelectOffer... offers) {
		behavior().SELECT(a -> a.ls(offers));
	}

	/**
	 * SELECT DISTINCT
	 * @param offers SELECT 句の要素
	 */
	default void SELECT_DISTINCT(SelectOffer... offers) {
		behavior().SELECT_DISTINCT(a -> a.ls(offers));
	}

	/**
	 * SELECT COUNT
	 */
	default void SELECT_COUNT() {
		behavior().SELECT_COUNT();
	}

	/**
	 * @return {@link SelectStatementBehavior}
	 */
	SelectStatementBehavior<?, ?, ?, ?, ?, ?> behavior();
}
