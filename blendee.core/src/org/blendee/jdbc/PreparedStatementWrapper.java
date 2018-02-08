package org.blendee.jdbc;

/**
 * {@link BlenPreparedStatement} をラップし、機能追加するための仕組みを定義したインターフェイスです。
 * @author 千葉 哲嗣
 * @see BlenConnection#setPreparedStatementWrapper(PreparedStatementWrapper)
 */
@FunctionalInterface
public interface PreparedStatementWrapper {

	/**
	 * {@link BlenPreparedStatement} が生成されたときに呼び出されます。
	 * @param statement 元の {@link BlenPreparedStatement}
	 * @return ラップされた {@link BlenPreparedStatement}
	 */
	BlenPreparedStatement wrap(BlenPreparedStatement statement);
}
