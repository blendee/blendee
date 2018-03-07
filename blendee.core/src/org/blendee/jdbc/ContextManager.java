package org.blendee.jdbc;

import java.util.UUID;

import org.blendee.jdbc.impl.SimpleContextStrategy;

/**
 * Blendee 内で使用する static にアクセスする必要のあるインスタンスを管理するクラスです。
 * @author 千葉 哲嗣
 */
public class ContextManager {

	private static final String defaultContextName = ContextManager.class.getName() + ".defaultContext." + UUID.randomUUID();

	private static final Object lock = new Object();

	private static ContextStrategy strategy;

	private static final ThreadLocal<String> contextName = ThreadLocal.withInitial(() -> defaultContextName);

	/**
	 * 指定した名前のコンテキストに切り替えます。
	 * @param name 新しいコンテキスト名
	 */
	public static void setContext(String name) {
		contextName.set(name);
	}

	/**
	 * クラス内に保持する {@link ThreadLocal} の値を解放します。<br>
	 * {@link #setContext(String)} を行った場合は、必ず同じスレッドでこのメソッドを実行してください。<br>
	 * そうしないとメモリリークの原因となる可能性があります。
	 */
	public static void releaseContext() {
		contextName.remove();
	}

	/**
	 * 現在のコンテキスト名を取得します。
	 * @return 現在のコンテキスト名
	 */
	public static String getCurrentContextName() {
		return contextName.get();
	}

	/**
	 * 現在のコンテキストで管理されているインスタンスを取得します。<br>
	 * まだインスタンスが作成されていない場合、新しく作成されます。
	 * @param <T> 取得するインスタンスの型
	 * @param clazz 取得するインスタンスのクラス
	 * @return 管理されているインスタンス
	 */
	public static <T extends ManagementSubject> T get(Class<T> clazz) {
		synchronized (lock) {
			if (strategy == null) strategy = new SimpleContextStrategy();
		}

		return strategy.getManagedInstance(getCurrentContextName(), clazz);
	}

	/**
	 * インスタンスの管理方法を変更します。<br>
	 * {@link SimpleContextStrategy} が使用されます。
	 */
	public static void newStrategy() {
		synchronized (lock) {
			strategy = new SimpleContextStrategy();
		}
	}

	/**
	 * インスタンスの管理方法を変更します。
	 * @param strategy {@link ContextStrategy}
	 */
	public static void newStrategy(ContextStrategy strategy) {
		synchronized (lock) {
			ContextManager.strategy = strategy;
		}
	}
}
