package org.blendee.support;

/**
 * {@link Query} に GROUP BY 句を設定するための関数型インターフェイスです。
 * @author 千葉 哲嗣
 * @param <R> 使用する {@link Query} のルートテーブル
 */
@FunctionalInterface
public interface GroupByOfferFunction<R extends QueryRelationship> {

	/**
	 * @param relation 使用する {@link Query} のルートテーブル
	 * @return {@link Offers}
	 */
	Offers<GroupByOffer> apply(R relation);
}
