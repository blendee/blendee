package org.blendee.assist;

/**
 * {@link SelectStatement} に ORDER BY 句を設定するための関数型インターフェイスです。
 * @author 千葉 哲嗣
 * @param <A> 使用する {@link SelectStatement} のルートテーブル
 */
@FunctionalInterface
public interface OrderByOfferFunction<A extends OrderByClauseAssist> {

	/**
	 * @param assist 使用する {@link SelectStatement} のルートテーブル
	 * @return {@link Offers}
	 */
	Offers<Offer> apply(A assist);
}
