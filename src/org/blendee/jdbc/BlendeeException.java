package org.blendee.jdbc;

import java.sql.SQLException;

/**
 * Blendee で使用する例外クラスの基底クラスです。
 * @author 千葉 哲嗣
 */
public class BlendeeException extends RuntimeException {

	private static final long serialVersionUID = -661715179145326306L;

	/**
	 * {@link SQLException} をラップするコンストラクタです。
	 * @param e 元となる例外
	 * @see Throwable#getCause()
	 */
	public BlendeeException(SQLException e) {
		super(e);
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

	@Override
	public SQLException getCause() {
		return (SQLException) super.getCause();
	}
}
