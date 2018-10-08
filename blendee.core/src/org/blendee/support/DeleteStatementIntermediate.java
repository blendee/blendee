package org.blendee.support;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * DELETE
 * @param <W> WhereRelationship
 */
public class DeleteStatementIntermediate<W extends WhereRelationship> implements DataManipulationStatementIntermediate<W> {

	private final DataManipulationStatementBehavior<?, ?, W> behavior;

	/**
	 * @param behavior {@link DataManipulationStatementBehavior}
	 */
	public DeleteStatementIntermediate(DataManipulationStatementBehavior<?, ?, W> behavior) {
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
		return behavior.createDeleteDataManipulator();
	}

	@Override
	public DataManipulator dataManipulator() {
		return behavior.createDeleteDataManipulator();
	}
}
