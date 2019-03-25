package org.blendee.jdbc.exception;

import java.sql.SQLException;

import org.blendee.jdbc.BSQLException;

/**
 * 整合性制約に違反した場合の例外です。
 * @author 千葉 哲嗣
 */
public class ConstraintViolationException extends BSQLException {

	private static final long serialVersionUID = -827919451423858157L;

	/**
	 * メッセージ無しのコンストラクタです。
	 */
	public ConstraintViolationException() {
	}

	/**
	 * メッセージのあるコンストラクタです。
	 * @param message 独自のメッセージ
	 */
	public ConstraintViolationException(String message) {
		super(message);
	}

	/**
	 * メッセージのあるコンストラクタです。
	 * @param message 独自のメッセージ
	 * @param e 元となる例外
	 */
	public ConstraintViolationException(String message, SQLException e) {
		super(message, e);
	}
}
