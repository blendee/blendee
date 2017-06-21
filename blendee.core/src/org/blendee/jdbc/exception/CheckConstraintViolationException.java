package org.blendee.jdbc.exception;

/**
 * NOT NULL 制約に違反した場合の例外です。
 *
 * @author 千葉 哲嗣
 */
public class CheckConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -4497486417800155138L;

	/**
	 * メッセージ無しのコンストラクタです。
	 */
	public CheckConstraintViolationException() {}

	/**
	 * メッセージのあるコンストラクタです。
	 *
	 * @param message 独自のメッセージ
	 */
	public CheckConstraintViolationException(String message) {
		super(message);
	}
}
