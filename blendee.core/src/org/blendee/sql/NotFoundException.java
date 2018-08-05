package org.blendee.sql;

import org.blendee.jdbc.BlendeeException;

/**
 * {@link Relationship} に要求された参照先やカラムがない場合にスローされる例外です。
 * @author 千葉 哲嗣
 * @see Relationship#find(String)
 * @see Relationship#find(String[])
 * @see Relationship#getColumn(String)
 */
public class NotFoundException extends BlendeeException {

	private static final long serialVersionUID = -3432819920672893366L;

	/**
	 * @param message エラーメッセージ
	 */
	public NotFoundException(String message) {
		super(message);
	}
}
