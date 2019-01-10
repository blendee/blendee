package org.blendee.jdbc.exception;

import java.sql.SQLException;

/**
 * NOT NULL 制約に違反した場合の例外です。
 * @author 千葉 哲嗣
 */
public class CheckConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -9058524209118621546L;

	/**
	 * メッセージ無しのコンストラクタです。
	 */
	public CheckConstraintViolationException() {}

	/**
	 * メッセージのあるコンストラクタです。
	 * @param message 独自のメッセージ
	 */
	public CheckConstraintViolationException(String message) {
		super(message);
	}

	/**
	 * メッセージのあるコンストラクタです。
	 * @param message 独自のメッセージ
	 * @param e 元となる例外
	 */
	public CheckConstraintViolationException(String message, SQLException e) {
		super(message, e);
	}
}
