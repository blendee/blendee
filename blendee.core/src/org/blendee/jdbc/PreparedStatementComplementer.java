package org.blendee.jdbc;

/**
 * {@link BlenPreparedStatement} のプレースホルダに値を設定するものを定義したインターフェイスです。
 * @author 千葉 哲嗣
 * @see BlenConnection#getStatement(String, PreparedStatementComplementer)
 */
@FunctionalInterface
public interface PreparedStatementComplementer {

	/**
	 * プレースホルダに値を設定します。<br>
	 * @param statement 対象となるステートメント
	 */
	void complement(BlenPreparedStatement statement);
}
