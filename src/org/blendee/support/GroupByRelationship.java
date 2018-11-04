package org.blendee.support;

import java.util.Arrays;

/**
 * 自動生成される ConcreteQueryRelationship の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface GroupByRelationship {

	/**
	 * {@link GroupByOfferFunction} 内で使用する GROUP BY 句生成用メソッドです。<br>
	 * パラメータの項目と順序を GROUP BY 句に割り当てます。
	 * @param offers GROUP BY 句に含めるテーブルおよびカラム
	 * @return offers
	 */
	default Offers<Offerable> list(Offerable... offers) {
		return ls(offers);
	}

	/**
	 * {@link #list} の短縮形です。
	 * パラメータの項目と順序を GROUP BY 句に割り当てます。
	 * @param offers GROUP BY 句に含めるテーブルおよびカラム
	 * @return offers
	 */
	default Offers<Offerable> ls(Offerable... offers) {
		return () -> Arrays.asList(offers);
	}

	/**
	 * 他のクエリと JOIN した際などの、最終的な順位を指定します。
	 * @param order 最終的な GROUP BY 句内での順序
	 * @param column 対象カラム
	 * @return {@link GroupByOffer}
	 */
	default GroupByOffer order(int order, GroupByColumn column) {
		return new GroupByOffer(order, column);
	}
}
