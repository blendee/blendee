package org.blendee.jdbc;

import java.util.logging.Level;

/**
 * 何も行わない {@link BLogger} です。
 * @author 千葉 哲嗣
 */
public class VoidLogger implements BLogger {

	@Override
	public boolean isLoggable(Level level) {
		return true;
	}

	@Override
	public void println(String message) {}

	@Override
	public void flush(Level level) {}
}
