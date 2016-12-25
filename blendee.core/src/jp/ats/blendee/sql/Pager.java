package jp.ats.blendee.sql;

/**
 * 検索結果をページ単位で取得するように SQL を加工する {@link SQLAdjuster} です。
 * <br>
 * LIMIT 句を使用しているので、データベースの種類によっては使用できません。
 *
 * @author 千葉 哲嗣
 */
public class Pager implements SQLAdjuster {

	private final String limit;

	/**
	 * インスタンスを生成します。
	 *
	 * @param currentPage 現在のページ数
	 * @param rowsParPage 1 ページ当たりの行数
	 */
	public Pager(int currentPage, int rowsParPage) {
		int offset = ((currentPage < 1 ? 1 : currentPage) - 1) * rowsParPage;
		limit = " LIMIT " + rowsParPage + " OFFSET " + offset;
	}

	@Override
	public String adjustSQL(String sql) {
		return sql + limit;
	}
}
