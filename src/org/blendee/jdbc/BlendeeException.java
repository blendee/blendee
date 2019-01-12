package org.blendee.jdbc;

import java.sql.SQLException;

/**
 * Blendee で使用する例外クラスの基底クラスです。
 * @author 千葉 哲嗣
 */
public class BlendeeException extends RuntimeException {

	private static final long serialVersionUID = -6166517265998968814L;

	/**
	 * {@link SQLException} をラップするコンストラクタです。
	 * @param t 元となる例外
	 * @see Throwable#getCause()
	 */
	public BlendeeException(Throwable t) {
		super(t);
	}

	/**
	 * メッセージ無しのコンストラクタです。
	 */
	public BlendeeException() {
		super();
	}

	/**
	 * メッセージのあるコンストラクタです。
	 * @param message 独自のメッセージ
	 */
	public BlendeeException(String message) {
		super(message);
	}

	/**
	 * メッセージのあるコンストラクタです。
	 * @param message 独自のメッセージ
	 * @param t 元となる例外
	 */
	public BlendeeException(String message, Throwable t) {
		super(message, t);
	}

	@Override
	public Throwable getCause() {
		return super.getCause();
	}
}
