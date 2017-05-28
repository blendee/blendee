package org.blendee.jdbc;

/**
 * 子レコードが存在する場合に使用する例外です。
 *
 * @author 千葉 哲嗣
 */
public class ChildKeyExistsException extends BlendeeException {

	private static final long serialVersionUID = -7301068525503834549L;

	/**
	 * メッセージ無しのコンストラクタです。
	 */
	public ChildKeyExistsException() {}

	/**
	 * メッセージのあるコンストラクタです。
	 *
	 * @param message 独自のメッセージ
	 */
	public ChildKeyExistsException(String message) {
		super(message);
	}
}
