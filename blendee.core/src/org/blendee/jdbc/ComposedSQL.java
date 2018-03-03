package org.blendee.jdbc;

/**
 * プレースホルダを持つ SQL 文と、プレースホルダにセットする値を持つものを表すインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface ComposedSQL extends PreparedStatementComplementer {

	/**
	 * このインスタンスが持つ SQL 文を返します。
	 * @return SQL 文
	 */
	String sql();
}
