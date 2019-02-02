package org.blendee.assist;

import java.util.Arrays;

/**
 * 自動生成される TableFacade の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface GroupByClauseAssist extends ColumnMaker {

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
	 * 他のクエリと JOIN した際などの、最終的な順位を指定します。
	 * @param order 最終的な GROUP BY 句内での順序
	 * @param offer 対象カラム
	 * @return {@link GroupByOffer}
	 */
	default GroupByOffer order(int order, Offer offer) {
		return new GroupByOffer(order, offer);
	}

	@Override
	default Statement statement() {
		return getSelectStatement();
	}

	/**
	 * 内部使用メソッド
	 * @return {@link SelectStatement}
	 */
	SelectStatement getSelectStatement();
}
