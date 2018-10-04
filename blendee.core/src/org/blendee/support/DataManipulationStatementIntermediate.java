package org.blendee.support;

import java.util.function.Consumer;

import org.blendee.jdbc.BPreparedStatement;

/**
 * データ操作文の中間形態を表すものの基底インターフェイスです。
 * @author 千葉 哲嗣
 * @param <W> WHERE
 */
public interface DataManipulationStatementIntermediate<W extends WhereRelationship> extends DataManipulator {

	/**
	 * @param consumers
	 * @return {@link DataManipulator}
	 */
	@SuppressWarnings("unchecked")
	DataManipulator WHERE(Consumer<W>... consumers);

	@Override
	default String sql() {
		return getDefaultDataManipulator().sql();
	}

	@Override
	default int complement(int done, BPreparedStatement statement) {
		return getDefaultDataManipulator().complement(done, statement);
	}

	@Override
	default DataManipulator reproduce(Object... placeHolderValues) {
		return getDefaultDataManipulator().reproduce(placeHolderValues);
	}

	@Override
	default int execute() {
		return getDefaultDataManipulator().execute();
	}

	/**
	 * @return WHERE がない {@link DataManipulator}
	 */
	DataManipulator getDefaultDataManipulator();
}
