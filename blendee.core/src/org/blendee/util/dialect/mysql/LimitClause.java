package org.blendee.util.dialect.mysql;

import org.blendee.sql.Effector;

/**
 * 検索結果を制限する SQL に加工する {@link Effector} です。
 * @author 千葉 哲嗣
 */
public class LimitClause implements Effector {

	private final String clause;

	/**
	 * インスタンスを生成します。
	 * @param offset オフセット数
	 * @param limit リミット数
	 */
	public LimitClause(int offset, int limit) {
		clause = " LIMIT " + offset + ", " + limit;
	}

	@Override
	public String effect(String sql) {
		return sql + clause;
	}
}
