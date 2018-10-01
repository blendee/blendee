package org.blendee.support;

import java.util.function.BiConsumer;

import org.blendee.jdbc.ComposedSQL;

/**
 * UPDATE
 */
public class UpdateStatementIntermediate extends DataManipulator {

	public UpdateStatementIntermediate(ComposedSQL base) {
		super(base);
	}

	/**
	 * @param consumers {@link BiConsumer}
	 * @return {@link SelectStatement}
	 */
	public DataManipulator WHERE() {
		return new DataManipulator(null);
	}
}
