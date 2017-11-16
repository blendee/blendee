package org.blendee.internal;

import org.blendee.jdbc.BTransaction;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.BlendeeManager;

/**
 * 終了時に {@link BTransaction} を自動的にクローズする抽象基底クラスです。
 * @author 千葉 哲嗣
 */
public abstract class TransactionShell implements Shell {

	private final BlendeeManager manager = ContextManager.get(BlendeeManager.class);

	private final BTransaction transaction;

	/**
	 * デフォルトコンストラクタです。
	 */
	protected TransactionShell() {
		if (manager.hasConnection()) {
			this.transaction = null;
		} else {
			this.transaction = manager.startTransaction();
		}
	}

	@Override
	public void prepare() {
		if (transaction != null) TransactionManager.regist(transaction);
	}

	@Override
	public void doFinally() {
		manager.getAutoCloseableFinalizer().closeAll();
		if (transaction != null) transaction.close();
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	/**
	 * サブクラスで、 {@link BTransaction} が必要な場合取得するためのメソッドです。
	 * @return 現在オープンしている {@link BTransaction}
	 */
	protected BTransaction getTransaction() {
		if (transaction == null)
			throw new IllegalStateException("トランザクションを開始した Shell ではないのでトランザクションがありません");

		return transaction;
	}
}
