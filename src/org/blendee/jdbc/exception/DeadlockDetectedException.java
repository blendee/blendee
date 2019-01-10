package org.blendee.jdbc.exception;

import java.sql.SQLException;

import org.blendee.jdbc.BSQLException;

/**
 * デッドロックが発生した場合に使用する例外です。
 * @author 千葉 哲嗣
 */
public class DeadlockDetectedException extends BSQLException {

	private static final long serialVersionUID = 4188734293398367718L;

	/**
	 * メッセージ無しのコンストラクタです。
	 */
	public DeadlockDetectedException() {}

	/**
	 * メッセージのあるコンストラクタです。
	 * @param message 独自のメッセージ
	 */
	public DeadlockDetectedException(String message) {
		super(message);
	}

	/**
	 * メッセージのあるコンストラクタです。
	 * @param message 独自のメッセージ
	 * @param e 元となる例外
	 */
	public DeadlockDetectedException(String message, SQLException e) {
		super(message, e);
	}
}
