package org.blendee.jdbc.exception;

/**
 * NOT NULL 制約に違反した場合の例外です。
 *
 * @author 千葉 哲嗣
 */
public class NotNullConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -1383750256881016411L;

	/**
	 * メッセージ無しのコンストラクタです。
	 */
	public NotNullConstraintViolationException() {}

	/**
	 * メッセージのあるコンストラクタです。
	 *
	 * @param message 独自のメッセージ
	 */
	public NotNullConstraintViolationException(String message) {
		super(message);
	}
}
