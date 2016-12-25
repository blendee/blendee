package jp.ats.blendee.jdbc;

/**
 * 一意制約違反が発生した場合に使用する例外です。
 *
 * @author 千葉 哲嗣
 */
public class DuplicateKeyException extends BlendeeException {

	private static final long serialVersionUID = 3844479229674960035L;

	/**
	 * メッセージ無しのコンストラクタです。
	 */
	public DuplicateKeyException() {}

	/**
	 * メッセージのあるコンストラクタです。
	 *
	 * @param message 独自のメッセージ
	 */
	public DuplicateKeyException(String message) {
		super(message);
	}
}
