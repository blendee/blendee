package org.blendee.support;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * UPDATE
 * @param <W> WhereRelationship
 */
public class UpdateStatementIntermediate<W extends WhereRelationship> implements DataManipulationStatementIntermediate<W> {

	private final DataManipulationStatementBehavior<?, ?, W> behavior;

	private DataManipulator defaultDataManipulator;

	/**
	 * @param behavior {@link DataManipulationStatementBehavior}
	 */
	public UpdateStatementIntermediate(DataManipulationStatementBehavior<?, ?, W> behavior) {
		this.behavior = behavior;
	}

	/**
	 * @param consumers {@link BiConsumer}
	 * @return {@link SelectStatement}
	 */
	@Override
	@SafeVarargs
	public final DataManipulator WHERE(Consumer<W>... consumers) {
		behavior.WHERE(consumers);
		return behavior.createUpdateDataManipulator();
	}

	@Override
	public DataManipulator getDefaultDataManipulator() {
		if (defaultDataManipulator == null) {
			defaultDataManipulator = behavior.createUpdateDataManipulator();
		}

		return defaultDataManipulator;
	}
}
