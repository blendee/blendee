package org.blendee.support;

import java.util.function.Consumer;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.sql.Binder;

/**
 * データ操作文の中間形態を表すものの基底インターフェイスです。
 * @author 千葉 哲嗣
 * @param <W> WHERE
 */
public interface DataManipulationStatementIntermediate<W extends WhereRelationship<?>> extends DataManipulator {

	/**
	 * このデータ操作文に WHERE 句をセットします。
	 * @param consumers {@link Consumer}
	 * @return {@link DataManipulator}
	 */
	@SuppressWarnings("unchecked")
	DataManipulator WHERE(Consumer<W>... consumers);

	@Override
	default String sql() {
		return dataManipulator().sql();
	}

	@Override
	default int complement(int done, BPreparedStatement statement) {
		return dataManipulator().complement(done, statement);
	}

	@Override
	default DataManipulator reproduce(Object... placeHolderValues) {
		return dataManipulator().reproduce(placeHolderValues);
	}

	@Override
	default DataManipulator reproduce() {
		return dataManipulator().reproduce();
	}

	@Override
	default Binder[] currentBinders() {
		return dataManipulator().currentBinders();
	}

	@Override
	default int execute() {
		return dataManipulator().execute();
	}

	/**
	 * @return 現時点での {@link DataManipulator}
	 */
	DataManipulator dataManipulator();
}
