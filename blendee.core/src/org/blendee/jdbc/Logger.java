package org.blendee.jdbc;

import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.blendee.internal.U;

/**
 * @author 千葉 哲嗣
 */
class Logger {

	private final List<BindingValue> values = new LinkedList<>();

	private final PrintStream printStream;

	private final Pattern stackTracePattern;

	private String sql;

	Logger(PrintStream printStream, Pattern stackTracePattern) {
		this.printStream = printStream;
		this.stackTracePattern = stackTracePattern;
	}

	void setSql(String sql) {
		this.sql = sql;
	}

	void addBindingValue(String type, int index, Object value) {
		values.add(new BindingValue(type, index, value));
	}

	void logElapsed(long start) {
		synchronized (getClass()) {
			printStream.println("[elapsed: " + (System.currentTimeMillis() - start) + " ms]");
			printStream.flush();
		}
	}

	void flush() {
		synchronized (getClass()) {
			if (printStream == null) return;
			printStream.println("------ SQL START ------");

			if (stackTracePattern != null) {
				printStream.println("call from:");
				StackTraceElement[] elements = new Throwable().getStackTrace();
				for (StackTraceElement element : elements) {
					String elementString = element.toString();
					if (stackTracePattern.matcher(elementString).find()) printStream.println(" " + elementString);
				}
			}

			printStream.println("sql:");
			printStream.println(" " + sql);

			Collections.sort(values);
			if (values.size() > 0) printStream.println("binding value:");
			for (BindingValue value : values) {
				printStream.println(" " + value.createLog());
			}

			printStream.println("------  SQL END  ------");
			values.clear();
			printStream.flush();
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
