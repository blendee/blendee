package org.blendee.jdbc;

import java.sql.Connection;

import org.blendee.internal.U;

/**
 * {@link Connection} から、トランザクション操作部分のみを抽出した抽象クラスです。
 * @author 千葉 哲嗣
 * @see BlendeeManager#startTransaction()
 */
public abstract class Transaction implements AutoCloseable {

	private Configure config;

	private BConnection connection;

	/**
	 * コミットします。
	 */
	public void commit() {
		//自動コミットの場合何もしない
		if (getConfigure().usesAutoCommit()) return;

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
		//自動コミットの場合何もしない
		if (getConfigure().usesAutoCommit()) return;

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
			BlendeeManager.get().removeThreadLocal();
		}
	}

	/**
	 * このトランザクションで使用する接続を取得します。
	 * @return 接続
	 */
	public BConnection getConnection() {
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
	protected abstract BConnection getConnectionInternal();

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

	/**
	 * 現在の設定を返します。
	 * @return {@link Configure}
	 */
	protected Configure getConfigure() {
		if (config == null) {
			config = ContextManager.get(BlendeeManager.class).getConfigure();
		}

		return config;
	}

	void prepareConnection() {
		Configure config = getConfigure();

		BConnection connection = getConnection();

		if (config.getLoggerWithoutCheck().isLoggable(LoggingConnection.level)) connection = new LoggingConnection(
			connection,
			new SQLLogger(config.getLoggerWithoutCheck(), config.getLogStackTracePatternWithoutCheck()));

		this.connection = connection;
	}
}
