package org.blendee.jdbc.exception;

import org.blendee.jdbc.BlendeeException;

/**
 * 整合性制約に違反した場合の例外です。
 *
 * @author 千葉 哲嗣
 */
public class ConstraintViolationException extends BlendeeException {

	private static final long serialVersionUID = 1590142695425209205L;

	/**
	 * メッセージ無しのコンストラクタです。
	 */
	public ConstraintViolationException() {}

	/**
	 * メッセージのあるコンストラクタです。
	 *
	 * @param message 独自のメッセージ
	 */
	public ConstraintViolationException(String message) {
		super(message);
	}
}
