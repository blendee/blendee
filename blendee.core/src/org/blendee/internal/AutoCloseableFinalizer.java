package org.blendee.internal;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//TODO 設計通り動くかをテストすること
class AutoCloseableFinalizer {

	private static final AtomicInteger counter = new AtomicInteger();

	private final Object lock = new Object();

	private final Map<Reference<Object>, AutoCloseable> map = new HashMap<>();

	private final ReferenceQueue<Object> reaped = new ReferenceQueue<Object>();

	private final Thread thread;

	public AutoCloseableFinalizer(int intervalmilliSeconds) {
		thread = new Thread(
			() -> {
				while (!execute()) {
					try {
						TimeUnit.MILLISECONDS.sleep(intervalmilliSeconds);
					} catch (InterruptedException e) {
						break;
					}
				}

				map.values().forEach(AutoCloseableFinalizer::close);
			},
			AutoCloseableFinalizer.class.getName() + "-" + counter.getAndIncrement());
	}

	public void regist(Object enclosure, AutoCloseable closeable) {
		synchronized (lock) {
			map.put(new PhantomReference<>(enclosure, reaped), closeable);
		}
	}

	public void start() {
		thread.start();
	}

	public void stop() {
		thread.interrupt();
		System.gc();
		execute();
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
			closeable.close();
		} catch (Exception e) {}
	}

	public static void main(String[] args) throws Exception {
		AutoCloseable c = () -> System.out.println("closed.");

		AutoCloseableFinalizer f = new AutoCloseableFinalizer(10);
		f.start();

		for (int i = 0; i < 10000; i++) {
			Enc e = new Enc();
			e.c = c;
			f.regist(e, c);
		}

		f.stop();
	}

	private static class Enc {

		AutoCloseable c;
	}
}
