package org.blendee.jdbc;

import java.sql.SQLException;

/**
 * Blendee で使用する {@link SQLException} のラッパークラスです。
 * @author 千葉 哲嗣
 */
public class BSQLException extends BlendeeException {

	private static final long serialVersionUID = 2626088590083460774L;

	/**
	 * {@link SQLException} をラップするコンストラクタです。
	 * @param e 元となる例外
	 * @see Throwable#getCause()
	 */
	public BSQLException(SQLException e) {
		super(e);
	}

	/**
	 * メッセージ無しのコンストラクタです。
	 */
	public BSQLException() {
		super();
	}

	/**
	 * メッセージのあるコンストラクタです。
	 * @param message 独自のメッセージ
	 */
	public BSQLException(String message) {
		super(message);
	}

	/**
	 * メッセージのあるコンストラクタです。
	 * @param message 独自のメッセージ
	 * @param e 元となる例外
	 */
	public BSQLException(String message, SQLException e) {
		super(message, e);
	}

	@Override
	public SQLException getCause() {
		return (SQLException) super.getCause();
	}

	/**
	 * @return {@link SQLException#getSQLState()}
	 */
	public String getSQLState() {
		return getCause().getSQLState();
	}
}
