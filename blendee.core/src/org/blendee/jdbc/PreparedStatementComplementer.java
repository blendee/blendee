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
	 * <br>
	 * 全てのプレースホルダを埋めないことも可能なので、そのために自身が設定した値の数を返し、Blendee が次のインスタンスに委譲します。
	 * @param statement 対象となるステートメント
	 * @return 自身が設定した値の数
	 */
	int complement(BPreparedStatement statement);
}
