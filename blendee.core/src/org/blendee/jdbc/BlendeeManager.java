package org.blendee.jdbc;

import org.blendee.internal.LoggingManager;
import org.blendee.internal.Transactions;

/**
 * Blendee の設定を管理し、トランザクションの生成、接続の管理等をおこなうハブクラスです。
 * @author 千葉 哲嗣
 */
public class BlendeeManager implements ManagementSubject {

	private final ThreadLocal<ThreadLocalValues> threadLocalValues = ThreadLocal.withInitial(
		() -> new ThreadLocalValues());

	private final Object lock = new Object();

	private Configure config;

	private AutoCloseableFinalizer autoCloseableFinalizer;

	/**
	 * 新しい {@link Initializer} を使用して Blendee の設定を初期化します
	 * @param initializer 新しい設定を持つ {@link Initializer}
	 */
	public void initialize(Initializer initializer) {
		synchronized (lock) {
			if (config != null) throw new IllegalStateException("既に initialize が実行されています。");

			config = initializer.createConfigure();

			int interval = config.getAutoCloseIntervalMillis();

			//100以下は100に増やしておく
			autoCloseableFinalizer = new AutoCloseableFinalizer(interval < 100 ? 100 : interval);

			//intervalが0以下の場合、スレッドを起動しない
			if (interval > 0) autoCloseableFinalizer.start();
		}

		LoggingManager.getLogger().info("Blendee initialized. " + initializer);
	}

	/**
	 * 既に初期化済かを返します。
	 * @return 既に初期化済かどうか
	 */
	public boolean initialized() {
		synchronized (lock) {
			return config != null;
		}
	}

	/**
	 * 現在の設定を返します。
	 * @return 現在の設定を持つ {@link Configure}
	 * @throws IllegalStateException まだ {@link BlendeeManager#initialize(Initializer)} が行われていない場合
	 */
	public Configure getConfigure() {
		synchronized (lock) {
			if (config == null) throw new IllegalStateException("設定が完了していません");
			return config;
		}
	}

	/**
	 * 現在の {@link AutoCloseableFinalizer} を返します。
	 * @return {@link AutoCloseableFinalizer}
	 */
	public AutoCloseableFinalizer getAutoCloseableFinalizer() {
		synchronized (lock) {
			if (autoCloseableFinalizer == null) throw new IllegalStateException("設定が完了していません");
			return autoCloseableFinalizer;
		}
	}

	/**
	 * 現在実行中のスレッドが接続を持っているか検査します。
	 * @return スレッドが接続を持っているかどうか
	 */
	public boolean hasConnection() {
		return threadLocalValues.get().transaction != null;
	}

	/**
	 * 新しいトランザクションを開始します。<br>
	 * トランザクションを開始することで現在のスレッドに接続がセットされ、 {@link BlendeeManager#getConnection()} で取得することができます。
	 * @return 新しいトランザクション
	 * @throws IllegalStateException 既にこのスレッド用にトランザクションが開始されている場合
	 */
	public BTransaction startTransaction() {
		if (hasConnection()) throw new IllegalStateException("既にこのスレッド用にトランザクションが開始されています");

		Configure config = getConfigure();
		config.check();

		config.initialize();

		TransactionFactory factory = config.getTransactionFactoryWithoutCheck();
		BTransaction transaction;
		if (config.usesLazyTransaction()) {
			transaction = new LazyTransaction(factory);
		} else {
			transaction = factory.createTransaction();
		}

		transaction.prepareConnection(config);

		ThreadLocalValues values = threadLocalValues.get();
		values.transaction = transaction;

		return transaction;
	}

	/**
	 * 現在のスレッドが持つ接続を返します。
	 * @return 接続
	 */
	public BConnection getConnection() {
		BTransaction transaction = threadLocalValues.get().transaction;
		if (transaction == null) throw new IllegalStateException("このスレッドのトランザクションが開始されていません");

		BConnection connection = transaction.getConnection();
		if (connection == null) throw new IllegalStateException("このスレッドの接続が作成されていません");

		return connection;
	}

	/**
	 * 現在のトランザクションに、パラメータで渡されたトランザクションを連動させます。
	 * @param transaction 連動させたいトランザクション
	 */
	public void synchroniseWithCurrentTransaction(Transaction transaction) {
		threadLocalValues.get().transactions.regist(transaction);
	}

	/**
	 * {@link Configure#usesMetadataCache() } を使用した場合にできるキャッシュを空にします。
	 */
	public void clearMetadataCache() {
		ContextManager.get(MetadataCache.class).clearCache();
	}

	Transactions getTransactions() {
		return threadLocalValues.get().transactions;
	}

	boolean isCurrent(Configure config) {
		synchronized (lock) {
			return this.config == config;
		}
	}

	void remove(BTransaction transaction) {
		threadLocalValues.get().remove(transaction);
	}

	private static class ThreadLocalValues {

		private final Transactions transactions = new Transactions();

		private BTransaction transaction;

		/**
		 * このクラスで管理するトランザクションの場合のみ削除する
		 */
		private void remove(BTransaction transaction) {
			if (this.transaction != transaction) return;
			this.transaction = null;
		}
	}
}
