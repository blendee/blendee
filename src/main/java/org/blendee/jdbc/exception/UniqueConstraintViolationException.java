package org.blendee.jdbc.exception;

import java.sql.SQLException;

/**
 * 一意制約に違反した場合の例外です。
 * @author 千葉 哲嗣
 */
public class UniqueConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -3119592810027357590L;

	/**
	 * メッセージ無しのコンストラクタです。
	 */
	public UniqueConstraintViolationException() {
	}

	/**
	 * メッセージのあるコンストラクタです。
	 * @param message 独自のメッセージ
	 */
	public UniqueConstraintViolationException(String message) {
		super(message);
	}

	/**
	 * メッセージのあるコンストラクタです。
	 * @param message 独自のメッセージ
	 * @param e 元となる例外
	 */
	public UniqueConstraintViolationException(String message, SQLException e) {
		super(message, e);
	}
}
