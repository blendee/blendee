package org.blendee.util.dialect.mysql;

import org.blendee.sql.SQLAdjuster;
import org.blendee.util.dialect.postgresql.LimitClause;

/**
 * 検索結果をページ単位で取得するように SQL を加工する {@link SQLAdjuster} です。<br>
 * @author 千葉 哲嗣
 */
public class Pager extends LimitClause {

	/**
	 * インスタンスを生成します。
	 * @param currentPage 現在のページ数
	 * @param rowsParPage 1 ページ当たりの行数
	 */
	public Pager(int currentPage, int rowsParPage) {
		super(offset(currentPage, rowsParPage), rowsParPage);
	}

	private static final int offset(int currentPage, int rowsParPage) {
		return ((currentPage < 1 ? 1 : currentPage) - 1) * rowsParPage;
	}
}
