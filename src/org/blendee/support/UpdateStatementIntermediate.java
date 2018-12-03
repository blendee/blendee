package org.blendee.support;

import java.util.function.Consumer;

/**
 * UPDATE 文中間形態を表します。
 * @author 千葉 哲嗣
 * @param <W> WhereRelationship
 */
public class UpdateStatementIntermediate<W extends WhereRelationship<?>> implements DataManipulationStatementIntermediate<W> {

	private final DataManipulationStatementBehavior<?, ?, W> behavior;

	/**
	 * @param behavior {@link DataManipulationStatementBehavior}
	 */
	public UpdateStatementIntermediate(DataManipulationStatementBehavior<?, ?, W> behavior) {
		this.behavior = behavior;
	}

	/**
	 * この UPDATE 文に WHERE 句をセットします。
	 * @param consumers {@link Consumer}
	 * @return {@link DataManipulator}
	 */
	@Override
	@SafeVarargs
	public final DataManipulator WHERE(Consumer<W>... consumers) {
		behavior.WHERE(consumers);
		return behavior.createUpdateDataManipulator();
	}

	@Override
	public DataManipulator dataManipulator() {
		return behavior.createUpdateDataManipulator();
	}

	@Override
	public String toString() {
		return sql();
	}
}
