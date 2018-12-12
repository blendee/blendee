package org.blendee.assist;

import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.sql.Reproducible;

/**
 * SQL によるデータ操作を実行するためのインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface DataManipulator extends ComposedSQL, Reproducible<DataManipulator> {

	/**
	 * データ操作を実行します。
	 * @return データ操作件数
	 */
	int execute();

	/**
	 * データ操作をバッチ実行します。
	 * @param statement {@link BatchStatement}
	 */
	default void execute(BatchStatement statement) {
		statement.addBatch(this);
	}
}
