package org.blendee.assist;

import org.blendee.sql.Binder;
import org.blendee.sql.Column;

/**
 * {@link Column} を内部に保持するものを表すインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface AssistColumn {

	/**
	 * @return {@link Column}
	 */
	Column column();

	/**
	 * このインスタンスが持つ {@link Statement} を返します。
	 * @return {@link Statement}
	 */
	Statement statement();

	default Binder[] values() {
		return Binder.EMPTY_ARRAY;
	}
}
