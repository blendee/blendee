package org.blendee.assist;

import java.util.Arrays;

import org.blendee.sql.Column;
import org.blendee.sql.GroupByClause;

/**
 * 自動生成される TableFacade の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface GroupByClauseAssist extends ClauseAssist {

	/**
	 * {@link GroupByOfferFunction} 内で使用する GROUP BY 句生成用メソッドです。<br>
	 * パラメータの項目と順序を GROUP BY 句に割り当てます。
	 * @param offers GROUP BY 句に含めるテーブルおよびカラム
	 * @return offers
	 */
	default Offers<Offer> list(Offer... offers) {
		return ls(offers);
	}

	/**
	 * {@link #list} の短縮形です。
	 * パラメータの項目と順序を GROUP BY 句に割り当てます。
	 * @param offers GROUP BY 句に含めるテーブルおよびカラム
	 * @return offers
	 */
	default Offers<Offer> ls(Offer... offers) {
		return () -> Arrays.asList(offers);
	}

	/**
	 * @param column 任意のカラム
	 * @return 対象カラム
	 */
	default Offer any(AssistColumn column) {
		String template = "{0}";
		Column[] columns = new Column[] { column.column() };

		GroupByClause clause = getGroupByClause();
		return new ListClauseOffer(order -> clause.add(order, template, columns, column));
	}

	/**
	 * 他のクエリと JOIN した際などの、最終的な順位を指定します。
	 * @param order 最終的な GROUP BY 句内での順序
	 * @param offer 対象カラム
	 * @return {@link ListClauseOffer}
	 */
	default Offer order(int order, Offer offer) {
		return new ListClauseOffer(offer, order);
	}

	@Override
	default Statement statement() {
		return getSelectStatement();
	}

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return 現在の ORDER BY 句
	 */
	GroupByClause getGroupByClause();

	/**
	 * 内部使用メソッド
	 * @return {@link SelectStatement}
	 */
	SelectStatement getSelectStatement();
}
