package org.blendee.jdbc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

/**
 * Blendee がログを出力するためのインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface BLogger {

	/**
	 * ログ出力可能なレベルかを判定します。
	 * @param level 調査対象
	 * @return 可能な場合、 true
	 */
	boolean isLoggable(Level level);

	/**
	 * message をログとして出力します。<br>
	 * {@link #flush(Level)} を行うまでログには出力されません。
	 * @param message メッセージ
	 */
	void println(String message);

	/**
	 * 未出力のログを、指定されたログレベルで出力します。
	 * @param level 出力レベル
	 */
	void flush(Level level);

	/**
	 * ログを一行出力します。
	 * @param level 出力レベル
	 * @param message メッセージ
	 */
	default void log(Level level, String message) {
		println(message);
		flush(level);
	}

	/**
	 * {@link Throwable} のスタックトレースをログに出力します。
	 * @param level 出力レベル
	 * @param t Throwable
	 */
	default void log(Level level, Throwable t) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream;
		try {
			stream = new PrintStream(out, false, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException uee) {
			throw new Error(uee);
		}

		t.printStackTrace(stream);
		stream.flush();

		println(new String(out.toByteArray(), StandardCharsets.UTF_8));

		flush(level);
	}
}
