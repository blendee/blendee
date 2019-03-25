package org.blendee.sql;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 一度生成した SQL をキャッシュし、次回実行時にその SQL を使用することで処理を高速化するためのクラスです。
 * @author 千葉 哲嗣
 */
class AsyncRecorder extends Recorder {

	private final Lock lock = new Lock() {

		@Override
		public void lock() {
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean tryLock() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void unlock() {
		}

		@Override
		public Condition newCondition() {
			throw new UnsupportedOperationException();
		}
	};

	@Override
	protected Lock lock() {
		return lock;
	}
}
