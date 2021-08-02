package org.blendee.jdbc.exception;

import java.sql.SQLException;

/**
 * 参照整合性制約に違反した場合の例外です。
 * @author 千葉 哲嗣
 */
public class ForeignKeyConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 3967292758789776783L;

	/**
	 * メッセージ無しのコンストラクタです。
	 */
	public ForeignKeyConstraintViolationException() {
	}

	/**
	 * メッセージのあるコンストラクタです。
	 * @param message 独自のメッセージ
	 */
	public ForeignKeyConstraintViolationException(String message) {
		super(message);
	}

	/**
	 * メッセージのあるコンストラクタです。
	 * @param message 独自のメッセージ
	 * @param e 元となる例外
	 */
	public ForeignKeyConstraintViolationException(String message, SQLException e) {
		super(message, e);
	}
}
