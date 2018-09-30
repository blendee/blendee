package org.blendee.support;

import java.util.function.BiConsumer;

/**
 * UPDATE
 */
public class UpdateStatementIntermediate extends DataManipulator {

	/**
	 * @param consumers {@link BiConsumer}
	 * @return {@link SelectStatement}
	 */
	public DataManipulator WHERE() {
		return new DataManipulator();
	}
}
