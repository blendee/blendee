package org.blendee.support;

/**
 * {@link QueryBuilder} に ORDER BY 句を設定するための関数型インターフェイスです。
 * @author 千葉 哲嗣
 * @param <R> 使用する {@link QueryBuilder} のルートテーブル
 */
@FunctionalInterface
public interface OrderByOfferFunction<R extends OrderByRelationship> {

	/**
	 * @param relation 使用する {@link QueryBuilder} のルートテーブル
	 * @return {@link Offers}
	 */
	Offers<Offerable> apply(R relation);
}
