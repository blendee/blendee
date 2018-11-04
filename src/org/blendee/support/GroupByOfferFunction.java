package org.blendee.support;

/**
 * {@link SelectStatement} に GROUP BY 句を設定するための関数型インターフェイスです。
 * @author 千葉 哲嗣
 * @param <R> 使用する {@link SelectStatement} のルートテーブル
 */
@FunctionalInterface
public interface GroupByOfferFunction<R extends GroupByRelationship> {

	/**
	 * @param relation 使用する {@link SelectStatement} のルートテーブル
	 * @return {@link Offers}
	 */
	Offers<Offerable> apply(R relation);
}
