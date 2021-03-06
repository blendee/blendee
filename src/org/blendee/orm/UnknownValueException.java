package org.blendee.orm;

import org.blendee.jdbc.BlendeeException;
import org.blendee.sql.Bindable;

/**
 * {@link DataObject} に、新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合にスローされる例外です。
 * @author 千葉 哲嗣
 * @see DataObject#setSQLFragment(String, String)
 * @see DataObject#setSQLFragmentAndValue(String, String, Bindable)
 */
public class UnknownValueException extends BlendeeException {

	private static final long serialVersionUID = -2842517856307860363L;

	UnknownValueException(String columnName, String sqlFragment) {
		//The current value is unknown because setting "B" to "A".
		super("The current value is unknown because setting \"sqlFragment\" to \"columnName\".");
	}
}
