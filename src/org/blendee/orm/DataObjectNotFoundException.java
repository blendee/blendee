package org.blendee.orm;

import org.blendee.jdbc.BlendeeException;

/**
 * {@link PrimaryKey} を使用した検索にもかかわらず、対象のデータが存在しなかった場合にスローされる例外です。
 * @author 千葉 哲嗣
 */
public class DataObjectNotFoundException extends BlendeeException {

	private static final long serialVersionUID = 6254543215086264829L;

	DataObjectNotFoundException(String message) {
		super(message);
	}
}
