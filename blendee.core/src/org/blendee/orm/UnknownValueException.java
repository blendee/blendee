package org.blendee.orm;

import org.blendee.jdbc.BlendeeException;
import org.blendee.sql.Bindable;

/**
 * {@link DataObject} に、新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合にスローされる例外です。
 *
 * @author 千葉 哲嗣
 * @see DataObject#setSQLFragment(String, String)
 * @see DataObject#setSQLFragmentAndValue(String, String, Bindable)
 */
public class UnknownValueException extends BlendeeException {

	private static final long serialVersionUID = -7319512583543487531L;

	UnknownValueException(String columnName, String sqlFragment) {
		super(columnName + " に " + sqlFragment + " をセットしたので、現在の値が不明です");
	}
}
