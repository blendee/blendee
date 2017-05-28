package org.blendee.orm;

import org.blendee.selector.Selector;
import org.blendee.sql.Pager;

/**
 * 検索結果をページ単位で取得するように SQL を加工する {@link QueryOption} です。
 * <br>
 * LIMIT 句を使用しているので、データベースの種類によっては使用できません。
 *
 * @author 千葉 哲嗣
 */
public class PagerOption implements QueryOption {

	private final Pager pager;

	/**
	 * インスタンスを生成します。
	 *
	 * @param currentPage 現在のページ数
	 * @param rowsParPage 1 ページ当たりの行数
	 */
	public PagerOption(int currentPage, int rowsParPage) {
		pager = new Pager(currentPage, rowsParPage);
	}

	@Override
	public void process(Selector selector) {
		selector.setSQLAdjuster(pager);
	}
}
