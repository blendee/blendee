package org.blendee.jdbc;

/**
 * {@link BatchStatement} をラップし、機能追加するための仕組みを定義したインターフェイスです。
 *
 * @author 千葉 哲嗣
 * @see BConnection#setBatchStatementWrapper(BatchStatementWrapper)
 */
@FunctionalInterface
public interface BatchStatementWrapper {

	/**
	 * {@link BatchStatement} が生成されたときに呼び出されます。
	 *
	 * @param statement 元の {@link BatchStatement}
	 * @return ラップされた {@link BatchStatement}
	 */
	BatchStatement wrap(BatchStatement statement);
}
