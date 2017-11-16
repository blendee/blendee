package org.blendee.jdbc;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * finalize() の代わりに、参照されなくなった JDBC のインスタンスを自動的にクローズするクラスです。
 * @author 千葉 哲嗣
 */
public class AutoCloseableFinalizer {

	private static final AtomicInteger threadCounter = new AtomicInteger();

	private final Object lock = new Object();

	private final Map<Reference<Object>, AutoCloseable> map = new HashMap<>();

	private final ReferenceQueue<Object> reaped = new ReferenceQueue<Object>();

	private final Runnable runnable;

	private Thread thread;

	/**
	 * コンストラクタです。
	 * @param intervalmilliSeconds 参照されなくなったかをチェックする間隔（ミリ秒）
	 */
	public AutoCloseableFinalizer(int intervalmilliSeconds) {
		runnable = () -> {
			while (!execute()) {
				try {
					TimeUnit.MILLISECONDS.sleep(intervalmilliSeconds);
				} catch (InterruptedException e) {
					break;
				}
			}
		};
	}

	/**
	 * 自動クローズ対象を登録します。<br>
	 * closable は、実際の JDBC のリソース、例えば {@link ResultSet} 等で、 closeableEnclosure は、その closeable を内部に持つ Blendee のラッパーインスタンスである必要があります。
	 * @param closeableEnclosure Blendee ラッパーインスタンス
	 * @param closeable JDBC リソース
	 */
	public void regist(Object closeableEnclosure, AutoCloseable closeable) {
		synchronized (lock) {
			map.put(new PhantomReference<>(closeableEnclosure, reaped), closeable);
		}
	}

	/**
	 * チェックスレッドが開始しているかを返します。
	 * @return チェックスレッドが開始しているか
	 */
	public boolean started() {
		synchronized (lock) {
			return thread != null && thread.isAlive();
		}
	}

	/**
	 * チェックスレッドを開始します。<br>
	 * スレッドが既に開始されている場合、何も起こりません。
	 */
	public void start() {
		synchronized (lock) {
			if (started()) return;

			thread = new Thread(runnable, AutoCloseableFinalizer.class.getName() + "-" + threadCounter.getAndIncrement());
			thread.start();
		}
	}

	/**
	 * チェックスレッドを停止します。<br>
	 * 停止時に、できる限りクローズ可能なリソースをクローズしますが、必ずすべてクローズされるわけではありません。<br>
	 * すべてクローズしたい場合は、 {@link #closeAll()} を使用してください。
	 */
	public void stop() {
		synchronized (lock) {
			if (thread == null) return;

			thread.interrupt();
			thread = null;
		}

		System.gc();
		execute();
	}

	/**
	 * このインスタンスに登録されているクローズ対象をすべてクローズします。
	 */
	public void closeAll() {
		synchronized (lock) {
			map.values().forEach(c -> close(c));
			map.clear();
		}
	}

	private boolean execute() {
		Reference<?> ref;
		while ((ref = reaped.poll()) != null) {
			ref.clear();
			synchronized (lock) {
				close(map.remove(ref));
			}
		}

		return Thread.interrupted();
	}

	private static void close(AutoCloseable closeable) {
		try {
			if (closeable != null)
				closeable.close();
		} catch (Exception e) {}
	}
}
