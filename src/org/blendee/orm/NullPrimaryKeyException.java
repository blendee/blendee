package org.blendee.orm;

import org.blendee.jdbc.BlendeeException;
import org.blendee.sql.Relationship;

/**
 * 対象となるテーブルに主キーが定義されていない場合にスローされる例外です。
 * @author 千葉 哲嗣
 */
public class NullPrimaryKeyException extends BlendeeException {

	private static final long serialVersionUID = 6679884962495612526L;

	NullPrimaryKeyException(Relationship relationship) {
		//relationship.getTablePath()
		//+ " ("
		//+ relationship.getId()
		//+ ") は PK に値を持たないので、使用できません"
		super(
			relationship.getTablePath()
				+ " ("
				+ relationship.getId()
				+ ") can not be used. (no value in PK)");
	}
}
