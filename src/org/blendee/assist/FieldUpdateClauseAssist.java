package org.blendee.assist;

/**
 * @author 千葉 哲嗣
 * @param <W> {@link WhereClauseAssist}
 */
public interface FieldUpdateClauseAssist<W extends WhereClauseAssist<?>> {

	/**
	 * UPDATE SET
	 * @return {@link UpdateStatementIntermediate}
	 */
	default UpdateStatementIntermediate<W> UPDATE() {
		return behavior().UPDATE();
	}

	/**
	 * UPDATE SET
	 * @param proofs UPDATE SET 句の要素
	 * @return {@link UpdateStatementIntermediate}
	 */
	default UpdateStatementIntermediate<W> UPDATE(SetProof... proofs) {
		return behavior().UPDATE(a -> a.ls(proofs));
	}

	/**
	 * @return {@link SelectStatementBehavior}
	 */
	DataManipulationStatementBehavior<?, ?, W> behavior();
}
