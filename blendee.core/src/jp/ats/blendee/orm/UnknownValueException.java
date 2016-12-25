package jp.ats.blendee.orm;

import jp.ats.blendee.jdbc.BlendeeException;
import jp.ats.blendee.sql.Bindable;

/**
 * {@link UpdatableDataObject} に、新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合にスローされる例外です。
 *
 * @author 千葉 哲嗣
 * @see UpdatableDataObject#setSQLFragment(String, String)
 * @see UpdatableDataObject#setSQLFragmentAndValue(String, String, Bindable)
 */
public class UnknownValueException extends BlendeeException {

	private static final long serialVersionUID = -7319512583543487531L;

	UnknownValueException(String columnName, String sqlFragment) {
		super(columnName + " に " + sqlFragment + " をセットしたので、現在の値が不明です");
	}
}
