package org.blendee.assist;

import org.blendee.jdbc.BlendeeException;

/**
 * ユニークキーを使用した検索にもかかわらず、検索結果が複数件あった場合にスローされる例外です。
 * @author 千葉 哲嗣
 * @see Query#willUnique()
 */
public class NotUniqueException extends BlendeeException {

	private static final long serialVersionUID = -2908580016495229526L;

	/**
	 * この例外のコンストラクタです。
	 */
	public NotUniqueException() {
		super();
	}
}
