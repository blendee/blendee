package org.blendee.jdbc;

import java.sql.Connection;

import org.blendee.internal.U;

/**
 * {@link Connection} から、トランザクション操作部分のみを抽出した抽象クラスです。
 * @author 千葉 哲嗣
 * @see BlendeeManager#startTransaction()
 */
public abstract class Transaction implements AutoCloseable {

	private BlenConnection connection;

	/**
	 * コミットします。
	 */
	public void commit() {
		try {
			commitInternal();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	/**
	 * ロールバックします。
	 */
	public void rollback() {
		try {
			rollbackInternal();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	/**
	 * 接続を閉じます。
	 */
	@Override
	public void close() {
		try {
			closeInternal();
		} finally {
			BlendeeManager.removeThreadLocal();
		}
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
