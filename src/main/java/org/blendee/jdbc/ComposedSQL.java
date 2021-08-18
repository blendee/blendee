package org.blendee.jdbc;

/**
 * プレースホルダを持つ SQL 文と、プレースホルダにセットする値を持つものを表すインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface ComposedSQL extends ChainPreparedStatementComplementer {

	/**
	 * このインスタンスが持つ SQL 文を返します。
	 * @return SQL 文
	 */
	String sql();

	/**
	 * {@link PreparedStatementComplementer} を入れ替えた新しい {@link ComposedSQL} を生成します。
	 * @param complementer 入れ替える {@link PreparedStatementComplementer}
	 * @return 同じ SQL を持つ、別のインスタンス
	 */
	default ComposedSQL reproduce(PreparedStatementComplementer complementer) {
		var sql = sql();
		return new ComposedSQL() {

			@Override
			public String sql() {
				return sql;
			}

			@Override
			public int complement(int done, BPreparedStatement statement) {
				complementer.complement(statement);
				return Integer.MIN_VALUE;
			}
		};
	}

	/**
	 * {@link ChainPreparedStatementComplementer} を入れ替えた新しい {@link ComposedSQL} を生成します。
	 * @param complementer 入れ替える {@link ChainPreparedStatementComplementer}
	 * @return 同じ SQL を持つ、別のインスタンス
	 */
	default ComposedSQL reproduce(ChainPreparedStatementComplementer complementer) {
		var sql = sql();
		return new ComposedSQL() {

			@Override
			public String sql() {
				return sql;
			}

			@Override
			public int complement(int done, BPreparedStatement statement) {
				return complementer.complement(done, statement);
			}
		};
	}
}
