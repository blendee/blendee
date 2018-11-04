package org.blendee.jdbc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.blendee.internal.U;

/**
 * @author 千葉 哲嗣
 */
class SQLLogger {

	private final List<BindingValue> values = new LinkedList<>();

	private final BLogger logger;

	private final Pattern stackTracePattern;

	private String sql;

	SQLLogger(BLogger logger, Pattern stackTracePattern) {
		this.logger = logger;
		this.stackTracePattern = stackTracePattern;
	}

	void setSql(String sql) {
		this.sql = sql;
	}

	void addBindingValue(String type, int index, Object value) {
		values.add(new BindingValue(type, index, value));
	}

	void logElapsed(long startNanos) {
		synchronized (getClass()) {
			float elapsed = (System.nanoTime() - startNanos) / 1000000f;
			logger.println("elapsed: " + new BigDecimal(elapsed).setScale(2, RoundingMode.DOWN) + "ms");
			logger.flush(Level.INFO);
		}
	}

	void flush() {
		synchronized (getClass()) {
			if (logger == null) return;
			logger.println("Blendee SQL Log: [" + new Date() + "]");
			logger.println("------ SQL START ------");

			if (stackTracePattern != null) {
				logger.println("call from:");
				StackTraceElement[] elements = new Throwable().getStackTrace();
				for (StackTraceElement element : elements) {
					String elementString = element.toString();
					if (stackTracePattern.matcher(elementString).find()) logger.println(" " + elementString);
				}
			}

			logger.println("sql:");
			logger.println(" " + sql);

			Collections.sort(values);
			if (values.size() > 0) logger.println("binding value:");
			for (BindingValue value : values) {
				logger.println(" " + value.createLog());
			}

			logger.println("------  SQL END  ------");
			values.clear();
			logger.flush(Level.INFO);
		}
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private static class BindingValue implements Comparable<BindingValue> {

		private final String type;

		private final int index;

		private final Object value;

		private BindingValue(String type, int index, Object value) {
			this.type = type;
			this.index = index;
			this.value = value;
		}

		@Override
		public int compareTo(BindingValue value) {
			return Integer.valueOf(index).compareTo(Integer.valueOf(value.index));
		}

		private String createLog() {
			return "index:[" + index + "] type:[" + type + "] value:[" + value + "]";
		}
	}
}
