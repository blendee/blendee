package org.blendee.jdbc;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link BLogger} のデフォルト実装です。<br>
 * {@link Logger} を内部で使用します。
 * @author 千葉 哲嗣
 */
public class DefaultLogger implements BLogger {

	private final ThreadLocal<List<String>> buffer = ThreadLocal.withInitial(() -> new LinkedList<>());

	private final Logger logger = java.util.logging.Logger.getLogger(DefaultLogger.class.getName());

	private static final String lineSeparator = System.lineSeparator();

	@Override
	public boolean isLoggable(Level level) {
		return logger.isLoggable(level);
	}

	@Override
	public void println(String message) {
		buffer.get().add(message);
	}

	@Override
	public void flush(Level level) {
		var list = buffer.get();
		logger.log(level, String.join(lineSeparator, list));
		list.clear();
	}
}
