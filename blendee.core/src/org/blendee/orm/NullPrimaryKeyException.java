package org.blendee.orm;

import org.blendee.jdbc.BlendeeException;
import org.blendee.sql.Relationship;

/**
 * 対象となるテーブルに主キーが定義されていない場合にスローされる例外です。
 * @author 千葉 哲嗣
 */
public class NullPrimaryKeyException extends BlendeeException {

	private static final long serialVersionUID = -2160138773158624947L;

	NullPrimaryKeyException(Relationship relationship) {
		super(
			relationship.getTablePath()
				+ " ("
				+ relationship.getID()
				+ ") は PK に値を持たないので、使用できません");
	}
}
