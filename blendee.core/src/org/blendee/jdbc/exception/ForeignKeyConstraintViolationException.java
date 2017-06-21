package org.blendee.jdbc.exception;

/**
 * 参照整合性制約に違反した場合の例外です。
 *
 * @author 千葉 哲嗣
 */
public class ForeignKeyConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -6050336563778804307L;

	/**
	 * メッセージ無しのコンストラクタです。
	 */
	public ForeignKeyConstraintViolationException() {}

	/**
	 * メッセージのあるコンストラクタです。
	 *
	 * @param message 独自のメッセージ
	 */
	public ForeignKeyConstraintViolationException(String message) {
		super(message);
	}
}
