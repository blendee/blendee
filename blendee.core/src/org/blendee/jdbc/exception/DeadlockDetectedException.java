package org.blendee.jdbc.exception;

import org.blendee.jdbc.BlendeeException;

/**
 * デッドロックが発生した場合に使用する例外です。
 *
 * @author 千葉 哲嗣
 */
public class DeadlockDetectedException extends BlendeeException {

	private static final long serialVersionUID = -6663058203853766873L;

	/**
	 * メッセージ無しのコンストラクタです。
	 */
	public DeadlockDetectedException() {}

	/**
	 * メッセージのあるコンストラクタです。
	 *
	 * @param message 独自のメッセージ
	 */
	public DeadlockDetectedException(String message) {
		super(message);
	}
}
