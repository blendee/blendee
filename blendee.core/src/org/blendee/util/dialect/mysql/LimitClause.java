package org.blendee.util.dialect.mysql;

import org.blendee.orm.SQLAdjusterOption;
import org.blendee.sql.SQLAdjuster;

/**
 * 検索結果を制限する SQL に加工する {@link SQLAdjuster} です。
 * @author 千葉 哲嗣
 */
public class LimitClause implements SQLAdjusterOption {

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
	public String adjustSQL(String sql) {
		return clause;
	}
}
