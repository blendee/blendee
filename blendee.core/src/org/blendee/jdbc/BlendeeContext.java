package org.blendee.jdbc;

import java.lang.ref.WeakReference;

import org.blendee.jdbc.impl.SimpleContextStrategy;

/**
 * Blendee 内で使用する static にアクセスする必要のあるインスタンスを管理するクラスです。
 * @author 千葉 哲嗣
 */
public class BlendeeContext {

	private static final Object lock = new Object();

	private static ContextStrategy strategy;

	private static ThreadLocal<WeakReference<ContextStrategy>> threadLocal = ThreadLocal
		.withInitial(() -> new WeakReference<>(strategy));

	/**
	 * 管理されているインスタンスを取得します。
	 * <br>
	 * まだインスタンスが作成されていない場合、新しく作成されます。
	 * @param <T> 取得するインスタンスの型
	 * @param clazz 取得するインスタンスのクラス
	 * @return 管理されているインスタンス
	 */
	public static <T extends ManagementSubject> T get(Class<T> clazz) {
		ContextStrategy mine = threadLocal.get().get();

		if (mine == null) {
			synchronized (lock) {
				if (strategy == null) strategy = new SimpleContextStrategy();
				mine = strategy;
			}

			threadLocal.set(new WeakReference<>(mine));
		}

		return mine.getManagedInstance(clazz);
	}

	/**
	 * インスタンスの管理方法を変更します。
	 * <br>
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
			BlendeeContext.strategy = strategy;
		}
	}

	/**
	 * インスタンスの管理方法を変更した場合、ガベージコレクションの状態によってはすぐに反映されていない可能性があります。
	 * <br>
	 * {@link ContextStrategy} はスレッド毎に紐づけられているので、 このスレッドが持つ {@link ContextStrategy} を、最新のものに更新します。
	 */
	public static void updateStrategyOnThisThread() {
		synchronized (lock) {
			threadLocal.set(new WeakReference<>(strategy));
		}
	}

	/**
	 * {@link ContextStrategy} を、最新のものに更新します。
	 * <br>
	 * この処理の実装は実験的なものなので、将来変更される可能性があります。
	 */
	public static void updateStrategy() {
		//GCの副作用でスレッド上のContextStrategy弱参照をなくす
		System.gc();
	}
}
