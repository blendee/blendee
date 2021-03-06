package org.blendee.assist;

import java.util.Arrays;

/**
 * 自動生成される TableFacade の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface InsertClauseAssist {

	/**
	 * INSERT 文生成用メソッドです。<br>
	 * パラメータの項目と順序を INSERT 文に割り当てます。
	 * @param offers INSERT 文に含めるテーブルおよびカラム
	 * @return offers
	 */
	default Offers<Offer> list(Offer... offers) {
		return ls(offers);
	}

	/**
	 * {@link #list} の短縮形です。
	 * パラメータの項目と順序を INSERT 文に割り当てます。
	 * @param offers INSERT 文に含めるテーブルおよびカラム
	 * @return offers
	 */
	default Offers<Offer> ls(Offer... offers) {
		return () -> Arrays.asList(offers);
	}
}
