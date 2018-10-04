package org.blendee.jdbc;

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
}
