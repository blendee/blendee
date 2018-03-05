package org.blendee.jdbc;

import java.sql.Connection;

import org.blendee.internal.Transactions;
import org.blendee.internal.U;

/**
 * {@link Connection} から、トランザクション操作部分のみを抽出した抽象クラスです。
 * @author 千葉 哲嗣
 * @see BlendeeManager#startTransaction()
 */
public abstract class Transaction implements AutoCloseable, Committable {

	private final BlendeeManager manager = ContextManager.get(BlendeeManager.class);

	private BlenConnection connection;

	/**
	 * 接続及び {@link BlendeeManager#synchroniseWithCurrentTransaction(Committable)} で登録された {@link Committable} をコミットします。
	 * @see BlendeeManager#synchroniseWithCurrentTransaction(Committable)
	 */
	@Override
	public void commit() {
		Transactions transactions = manager.getTransactions();
		try {
			commitInternal();
		} catch (Throwable t) {
			transactions.clear();
			throw new RuntimeException(t);
		}

		transactions.commit();
	}

	/**
	 * 接続及び {@link BlendeeManager#synchroniseWithCurrentTransaction(Committable)} で登録された {@link Committable} をロールバックします。
	 * @see BlendeeManager#synchroniseWithCurrentTransaction(Committable)
	 */
	@Override
	public void rollback() {
		Transactions transactions = manager.getTransactions();
		try {
			rollbackInternal();
		} catch (Throwable t) {
			transactions.clear();
			throw new RuntimeException(t);
		}

		transactions.rollback();
	}

	/**
	 * 接続を閉じます。
	 * @throws IllegalStateException {@link BlendeeManager#synchroniseWithCurrentTransaction(Committable)} で登録された {@link Committable} があり、 {@link Transaction#commit()} または {@link Transaction#rollback} が実行されていない場合
	 * @see BlendeeManager#synchroniseWithCurrentTransaction(Committable)
	 */
	@Override
	public void close() {
		//close中に例外が出るかもしれないので、Connectionの削除が先
		manager.remove(this);

		Transactions transactions = manager.getTransactions();
		try {
			closeInternal();
			if (transactions.size() > 0) throw new IllegalStateException("commit もしくは rollback が実行されていません");
		} finally {
			transactions.clear();
		}

		BlendeeManager.removeThreadLocal();
	}

	/**
	 * このトランザクションで使用する接続を取得します。
	 * @return 接続
	 */
	public BlenConnection getConnection() {
		if (connection == null) {
			connection = getConnectionInternal();
		}

		return connection;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	/**
	 * このトランザクションで使用する接続を取得します。
	 * @return 接続
	 */
	protected abstract BlenConnection getConnectionInternal();

	/**
	 * 実際のコミットを行います。
	 */
	protected abstract void commitInternal();

	/**
	 * 実際のロールバックを行います。
	 */
	protected abstract void rollbackInternal();

	/**
	 * 実際に接続を閉じます。
	 */
	protected abstract void closeInternal();

	void prepareConnection(Configure config) {
		BlenConnection connection = getConnection();

		if (config.usesMetadataCacheWithoutCheck()) connection = new MetadataCacheConnection(connection);

		if (config.enablesLogWithoutCheck()) connection = new LoggingConnection(
			connection,
			new Logger(config.getLogOutputWithoutCheck(), config.getLogStackTracePatternWithoutCheck()));

		config.initializeMetadatas(connection);
		Metadata[] metadatas = config.getMetadatasWithoutCheck();
		if (metadatas.length > 0) connection = new MetadatasConnection(connection, metadatas);

		this.connection = connection;
	}
}
