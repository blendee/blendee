package org.blendee.assist;

/**
 * INSERT 句を設定するための関数型インターフェイスです。
 * @author 千葉 哲嗣
 * @param <I> 使用する {@link SelectStatement} のルートテーブル
 */
@FunctionalInterface
public interface InsertOfferFunction<I extends InsertClauseAssist> {

	/**
	 * @param relation 使用する {@link SelectStatement} のルートテーブル
	 * @return {@link Offers}
	 */
	Offers<Offer> apply(I relation);
}
