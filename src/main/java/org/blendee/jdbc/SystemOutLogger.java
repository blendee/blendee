package org.blendee.jdbc;

import java.util.logging.Level;

/**
 * {@link BLogger} の簡易実装です。<br>
 * ログを標準出力に出力します。<br>
 * ログレベルは INFO です。
 * @author 千葉 哲嗣
 */
public class SystemOutLogger implements BLogger {

	@Override
	public boolean isLoggable(Level level) {
		return Level.INFO.intValue() >= level.intValue();
	}

	@Override
	public void println(String message) {
		System.out.println(message);
	}

	@Override
	public void flush(Level level) {
	}

}
