package org.blendee.jdbc;

/**
 * {@link BPreparedStatement} のプレースホルダに値を設定するものを定義したインターフェイスです。
 * @author 千葉 哲嗣
 * @see BConnection#getStatement(String, PreparedStatementComplementer)
 */
@FunctionalInterface
public interface ChainPreparedStatementComplementer extends PreparedStatementComplementer {

	/**
	 * プレースホルダに値を設定します。
	 * @param done 既に先頭から statement にセットした数
	 * @param statement 対象となるステートメント
	 * @return statement にセットした数
	 */
	int complement(int done, BPreparedStatement statement);

	@Override
	default void complement(BPreparedStatement statement) {
		complement(0, statement);
	}
}
