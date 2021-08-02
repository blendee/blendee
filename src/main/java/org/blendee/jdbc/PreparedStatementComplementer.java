package org.blendee.jdbc;

/**
 * {@link BPreparedStatement} のプレースホルダに値を設定するものを定義したインターフェイスです。
 * @author 千葉 哲嗣
 * @see BConnection#getStatement(String, PreparedStatementComplementer)
 */
@FunctionalInterface
public interface PreparedStatementComplementer {

	/**
	 * プレースホルダに値を設定します。
	 * @param statement 対象となるステートメント
	 */
	void complement(BPreparedStatement statement);
}
