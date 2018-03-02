package org.blendee.jdbc;

/**
 * {@link BlenStatement} を生成するのに必要となる情報をもつコンテナクラスです。
 * @author 千葉 哲嗣
 */
public class StatementSource {

	private final String sql;

	private final PreparedStatementComplementer complementer;

	/**
	 * @param sql SQL
	 * @param complementer プレースホルダにセットする値
	 */
	public StatementSource(String sql, PreparedStatementComplementer complementer) {
		this.sql = sql;
		this.complementer = complementer;
	}

	/**
	 * @return SQL
	 */
	public String getSQL() {
		return sql;
	}

	/**
	 * @return プレースホルダにセットする値
	 */
	public PreparedStatementComplementer getComplementer() {
		return complementer;
	}
}
