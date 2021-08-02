package org.blendee.jdbc.exception;

import java.sql.SQLException;

/**
 * NOT NULL 制約に違反した場合の例外です。
 * @author 千葉 哲嗣
 */
public class NotNullConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 3741989461655883001L;

	/**
	 * メッセージ無しのコンストラクタです。
	 */
	public NotNullConstraintViolationException() {
	}

	/**
	 * メッセージのあるコンストラクタです。
	 * @param message 独自のメッセージ
	 */
	public NotNullConstraintViolationException(String message) {
		super(message);
	}

	/**
	 * メッセージのあるコンストラクタです。
	 * @param message 独自のメッセージ
	 * @param e 元となる例外
	 */
	public NotNullConstraintViolationException(String message, SQLException e) {
		super(message, e);
	}
}
