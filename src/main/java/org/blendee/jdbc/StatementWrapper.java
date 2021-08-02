package org.blendee.jdbc;

/**
 * {@link BPreparedStatement} をラップし、機能追加するための仕組みを定義したインターフェイスです。
 * @author 千葉 哲嗣
 * @see BConnection#setStatementWrapper(StatementWrapper)
 */
public interface StatementWrapper {

	/**
	 * {@link BPreparedStatement} が生成されたときに呼び出されます。
	 * @param statement 元の {@link BPreparedStatement}
	 * @return ラップされた {@link BPreparedStatement}
	 */
	BStatement wrap(BStatement statement);

	/**
	 * {@link BPreparedStatement} が生成されたときに呼び出されます。
	 * @param statement 元の {@link BPreparedStatement}
	 * @return ラップされた {@link BPreparedStatement}
	 */
	BPreparedStatement wrap(BPreparedStatement statement);

	/**
	 * {@link Batch} が生成されたときに呼び出されます。
	 * @param batch 元の {@link Batch}
	 * @return ラップされた {@link Batch}
	 */
	Batch wrap(Batch batch);
}
