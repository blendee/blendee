package org.blendee.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 一度生成した SQL をキャッシュし、次回実行時にその SQL を使用することで処理を高速化するためのクラスです。
 * @author 千葉 哲嗣
 */
public class Recorder extends AbstractRecorder {

	private final Lock lock = new ReentrantLock();

	@Override
	protected Lock lock() {
		return lock;
	}
}
