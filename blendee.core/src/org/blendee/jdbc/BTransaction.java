package org.blendee.jdbc;

import java.sql.Connection;

import org.blendee.internal.Transactions;
import org.blendee.internal.U;

/**
 * {@link Connection} から、トランザクション操作部分のみを抽出した抽象クラスです。
 * @author 千葉 哲嗣
 * @see BlendeeManager#startTransaction()
 */
public abstract class BTransaction implements AutoCloseable, Transaction {

	private final BlendeeManager manager = ContextManager.get(BlendeeManager.class);

	private BConnection connection;

	/**
	 * 接続及び {@link BlendeeManager#synchroniseWithCurrentTransaction(Transaction)} で登録された {@link Transaction} をコミットします。
	 * @see BlendeeManager#synchroniseWithCurrentTransaction(Transaction)
	 */
	@Override
	public final void commit() {
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
	 * 接続及び {@link BlendeeManager#synchroniseWithCurrentTransaction(Transaction)} で登録された {@link Transaction} をロールバックします。
	 * @see BlendeeManager#synchroniseWithCurrentTransaction(Transaction)
	 */
	@Override
	public final void rollback() {
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
	 * @throws IllegalStateException {@link BlendeeManager#synchroniseWithCurrentTransaction(Transaction)} で登録された {@link Transaction} があり、 {@link BTransaction#commit()} または {@link BTransaction#rollback} が実行されていない場合
	 * @see BlendeeManager#synchroniseWithCurrentTransaction(Transaction)
	 */
	@Override
	public final void close() {
		//close中に例外が出るかもしれないので、Connectionの削除が先
		manager.remove(this);

		Transactions transactions = manager.getTransactions();
		try {
			closeInternal();
			if (transactions.size() > 0) throw new IllegalStateException("commit もしくは rollback が実行されていません");
		} finally {
			transactions.clear();
		}
	}

	/**
	 * このトランザクションで使用する接続を取得します。
	 * @return 接続
	 */
	public BConnection getConnection() {
		if (connection == null) return getConnectionInternal();
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

	void setConnection(BConnection connection) {
		this.connection = connection;
	}
}
