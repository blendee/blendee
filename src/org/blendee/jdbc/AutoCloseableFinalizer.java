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
//Java9でjava.lang.ref.Cleaner使用に書き換えること
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
	public void register(Object closeableEnclosure, AutoCloseable closeable) {
		synchronized (lock) {
			map.put(new PhantomReference<>(closeableEnclosure, reaped), closeable);
			lock.notify();
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
			//デーモンスレッドとし、他スレッドが終了するとこのスレッドも終了する
			thread.setDaemon(true);
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

			System.gc();
			lock.notify();
		}
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

	//チェックスレッド用メソッド
	private boolean execute() {
		Reference<?> ref;
		while ((ref = reaped.poll()) != null) {
			ref.clear();

			//一件ずつsyncするのは無駄だが、極力メインスレッド（register側）を止めないためにこうする
			synchronized (lock) {
				close(map.remove(ref));
				try {
					//監視対象がなくなったら、追加されるまでwait
					if (map.size() == 0) lock.wait();
				} catch (InterruptedException e) {
					return true;
				}
			}
		}

		return Thread.interrupted();
	}

	private static void close(AutoCloseable closeable) {
		try {
			if (closeable != null)
				closeable.close();
		} catch (Exception e) {
			//多重クローズで例外が出るかもしれないのでここでは無視
		}
	}
}
