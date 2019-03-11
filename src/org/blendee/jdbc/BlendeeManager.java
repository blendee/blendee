package org.blendee.jdbc;

/**
 * Blendee の設定を管理し、トランザクションの生成、接続の管理等をおこなうハブクラスです。
 * @author 千葉 哲嗣
 */
public class BlendeeManager implements ManagementSubject {

	private final ThreadLocal<Transaction> transactionThreadLocal = new ThreadLocal<>();

	private final Object lock = new Object();

	private Configure config;

	private Metadata metadata;

	private AutoCloseableFinalizer autoCloseableFinalizer;

	/**
	 * 新しい {@link Initializer} を使用して Blendee の設定を初期化します
	 * @param initializer 新しい設定を持つ {@link Initializer}
	 */
	public void initialize(Initializer initializer) {
		synchronized (lock) {
			//既に initialize が実行されています
			if (config != null) throw new IllegalStateException(BlendeeManager.class.getSimpleName() + " is already initialized");

			config = initializer.createConfigure();

			int interval = config.getAutoCloseIntervalMillis();

			//intervalが0以下の場合、スレッドを起動しない
			if (interval > 0) {
				//100以下は100に増やしておく
				autoCloseableFinalizer = new AutoCloseableFinalizer(interval < 100 ? 100 : interval);
				autoCloseableFinalizer.start();
			}
		}
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
			//設定が完了していません
			if (config == null) throw new IllegalStateException(BlendeeManager.class.getSimpleName() + " is not initialized.");
			return config;
		}
	}

	/**
	 * 現在の {@link AutoCloseableFinalizer} を返します。
	 * @return {@link AutoCloseableFinalizer}
	 */
	public AutoCloseableFinalizer getAutoCloseableFinalizer() {
		synchronized (lock) {
			return autoCloseableFinalizer;
		}
	}

	/**
	 * トランザクションが開始されているか検査します。
	 * @return トランザクションが開始されているかどうか
	 */
	public boolean startsTransaction() {
		return transactionThreadLocal.get() != null;
	}

	/**
	 * 新しいトランザクションを開始します。<br>
	 * トランザクションを開始することで現在のスレッドに接続がセットされ、 {@link BlendeeManager#getConnection()} で取得することができます。
	 * @return 新しいトランザクション
	 * @throws IllegalStateException 既にこのスレッド用にトランザクションが開始されている場合
	 */
	public Transaction startTransaction() {
		//既にこのスレッド用にトランザクションが開始されています
		if (startsTransaction()) throw new IllegalStateException(Transaction.class.getSimpleName() + " is already started for current thread.");

		config.initialize();

		Configure config = getConfigure();
		config.check();

		TransactionFactory factory = config.getTransactionFactoryWithoutCheck();
		Transaction transaction;
		if (config.usesLazyTransaction()) {
			transaction = new LazyTransaction(factory);
		} else {
			transaction = factory.createTransaction();
		}

		transaction.prepareConnection();

		transactionThreadLocal.set(transaction);

		return transaction;
	}

	/**
	 * 現在のスレッドが持つトランザクションを返します。
	 * @return トランザクション
	 */
	public Transaction getCurrentTransaction() {
		Transaction transaction = transactionThreadLocal.get();
		//このスレッドのトランザクションが開始されていません
		if (transaction == null) throw new IllegalStateException(Transaction.class.getSimpleName() + " is not started for current thread.");

		return transaction;
	}

	/**
	 * {@link Metadata} を返します。
	 * @return {@link Metadata}
	 */
	public Metadata getMetadata() {
		synchronized (lock) {
			if (metadata == null) {
				if (config.usesMetadataCache()) {
					metadata = new CacheMetadata(config.getMetadataFactory().createMetadata());
				} else {
					metadata = config.getMetadataFactory().createMetadata();
				}
			}

			return metadata;
		}
	}

	/**
	 * 現在のコンテキストの {@link BlendeeManager} を返します。
	 * @return {@link BlendeeManager}
	 */
	public static BlendeeManager get() {
		return ContextManager.get(BlendeeManager.class);
	}

	/**
	 * 設定済みの {@link BLogger} を返します。
	 * @return {@link BLogger}
	 */
	public static BLogger getLogger() {
		return get().getConfigure().getLogger();
	}

	/**
	 * 現在のスレッドが持つ接続を返します。
	 * @return 接続
	 */
	public static BConnection getConnection() {
		Transaction transaction = get().transactionThreadLocal.get();
		//このスレッドのトランザクションが開始されていません
		if (transaction == null) throw new IllegalStateException(Transaction.class.getSimpleName() + " is not started for current thread.");

		BConnection connection = transaction.getConnection();
		//このスレッドの接続が作成されていません
		if (connection == null) throw new IllegalStateException(BConnection.class.getSimpleName() + " is not created for current thread.");

		return connection;
	}

	/**
	 * {@link Configure#usesMetadataCache() } を使用した場合にできるキャッシュを空にします。
	 */
	public void clearMetadataCache() {
		ContextManager.get(MetadataCache.class).clearCache();
	}

	boolean isCurrent(Configure config) {
		synchronized (lock) {
			return this.config == config;
		}
	}

	void removeThreadLocal() {
		transactionThreadLocal.remove();
	}
}
