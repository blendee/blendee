package org.blendee.jdbc.exception;

/**
 * 一意制約に違反した場合の例外です。
 * @author 千葉 哲嗣
 */
public class UniqueConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -3817421185133992940L;

	/**
	 * メッセージ無しのコンストラクタです。
	 */
	public UniqueConstraintViolationException() {}

	/**
	 * メッセージのあるコンストラクタです。
	 * @param message 独自のメッセージ
	 */
	public UniqueConstraintViolationException(String message) {
		super(message);
	}
}
