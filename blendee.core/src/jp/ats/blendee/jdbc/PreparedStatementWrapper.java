package jp.ats.blendee.jdbc;

/**
 * {@link BPreparedStatement} をラップし、機能追加するための仕組みを定義したインターフェイスです。
 *
 * @author 千葉 哲嗣
 * @see BConnection#setPreparedStatementWrapper(PreparedStatementWrapper)
 */
@FunctionalInterface
public interface PreparedStatementWrapper {

	/**
	 * {@link BPreparedStatement} が生成されたときに呼び出されます。
	 *
	 * @param statement 元の {@link BPreparedStatement}
	 * @return ラップされた {@link BPreparedStatement}
	 */
	BPreparedStatement wrap(BPreparedStatement statement);
}
