package org.blendee.jdbc;

/**
 * 親キーが存在しない場合に使用する例外です。
 *
 * @author 千葉 哲嗣
 */
public class ParentKeyNotFoundException extends BlendeeException {

	private static final long serialVersionUID = -6127113478053818013L;

	/**
	 * メッセージ無しのコンストラクタです。
	 */
	public ParentKeyNotFoundException() {}

	/**
	 * メッセージのあるコンストラクタです。
	 *
	 * @param message 独自のメッセージ
	 */
	public ParentKeyNotFoundException(String message) {
		super(message);
	}
}
