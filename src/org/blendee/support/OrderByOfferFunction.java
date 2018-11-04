package org.blendee.support;

/**
 * {@link SelectStatement} に ORDER BY 句を設定するための関数型インターフェイスです。
 * @author 千葉 哲嗣
 * @param <R> 使用する {@link SelectStatement} のルートテーブル
 */
@FunctionalInterface
public interface OrderByOfferFunction<R extends OrderByRelationship> {

	/**
	 * @param relation 使用する {@link SelectStatement} のルートテーブル
	 * @return {@link Offers}
	 */
	Offers<Offerable> apply(R relation);
}
