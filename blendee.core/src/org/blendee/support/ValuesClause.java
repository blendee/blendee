package org.blendee.support;

import java.util.function.BiConsumer;

/**
 * VALUES 句
 */
public class ValuesClause {

	/**
	 * @param consumers {@link BiConsumer}
	 * @return {@link SelectStatement}
	 */
	public DataManipulator VALUES() {
		return new DataManipulator();
	}
}
