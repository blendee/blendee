package org.blendee.orm;

import org.blendee.jdbc.Batch;

/**
 * 自スレッドが行う {@link DataAccessHelper} と {@link DataObject} を使用した更新が、全てバッチ実行となる処理を定義できるインターフェイスです。
 * @author 千葉 哲嗣
 * @see DataAccessHelper#startThreadBatch(Batch, ThreadBatchCallback)
 */
@FunctionalInterface
public interface ThreadBatchCallback {

	/**
	 * 自スレッドが行う {@link DataAccessHelper} と {@link DataObject} を使用した更新が、全てバッチ実行となる処理を実行します。
	 * @throws Exception コールバック内で例外が発生した場合
	 */
	void execute() throws Exception;
}
