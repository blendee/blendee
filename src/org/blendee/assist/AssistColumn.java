package org.blendee.assist;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.ChainPreparedStatementComplementer;
import org.blendee.sql.Binder;
import org.blendee.sql.Column;

/**
 * {@link Column} を内部に保持するものを表すインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface AssistColumn extends ChainPreparedStatementComplementer {

	/**
	 * @return {@link Column}
	 */
	Column column();

	/**
	 * このインスタンスが持つ {@link Statement} を返します。
	 * @return {@link Statement}
	 */
	Statement statement();

	/**
	 * このカラムがプレースホルダを持つ場合、その値を {@link Binder} として返します。
	 * @return プレースホルダの値
	 */
	default Binder[] values() {
		return Binder.EMPTY_ARRAY;
	}

	@Override
	default int complement(int done, BPreparedStatement statement) {
		for (Binder binder : values()) {
			binder.bind(++done, statement);
		}

		return done;
	}
}
