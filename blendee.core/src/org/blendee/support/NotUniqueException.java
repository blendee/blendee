package org.blendee.support;

import org.blendee.jdbc.BlendeeException;
import org.blendee.sql.Effector;

/**
 * ユニークキーを使用した検索にもかかわらず、検索結果が複数件あった場合にスローされる例外です。
 * @author 千葉 哲嗣
 * @see Executor#willUnique()
 * @see Executor#willUnique(Effector...)
 */
public class NotUniqueException extends BlendeeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 6530027126490925906L;

	/**
	 * この例外のコンストラクタです。
	 */
	public NotUniqueException() {
		super();
	}
}
