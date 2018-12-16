package org.blendee.assist;

/**
 * @author 千葉 哲嗣
 */
public interface FieldInsertClauseAssist {

	/**
	 * INSERT
	 * @return {@link InsertStatementIntermediate}
	 */
	default InsertStatementIntermediate INSERT() {
		return behavior().INSERT();
	}

	/**
	 * INSERT
	 * @param offers INSERT の要素
	 * @return {@link InsertStatementIntermediate}
	 */
	default InsertStatementIntermediate INSERT(Offerable... offers) {
		return behavior().INSERT(a -> a.ls(offers));
	}

	/**
	 * INSERT
	 * @param offers INSERT の要素
	 * @param select {@link SelectStatement}
	 * @return {@link DataManipulator}
	 */
	default DataManipulator INSERT(Vargs<Offerable> offers, SelectStatement select) {
		return behavior().INSERT(a -> a.ls(offers.get()), select);
	}

	/**
	 * INSERT
	 * @param select {@link SelectStatement}
	 * @return {@link DataManipulator}
	 */
	default DataManipulator INSERT(SelectStatement select) {
		return behavior().INSERT(select);
	}

	/**
	 * @return {@link SelectStatementBehavior}
	 */
	DataManipulationStatementBehavior<?, ?, ?> behavior();
}
