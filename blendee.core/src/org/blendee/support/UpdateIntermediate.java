package org.blendee.support;

import java.util.function.BiConsumer;

/**
 * VALUES 句
 */
public class UpdateIntermediate extends DataManipulator {

	/**
	 * @param consumers {@link BiConsumer}
	 * @return {@link SelectStatement}
	 */
	public DataManipulator WHERE() {
		return new DataManipulator();
	}
}
