package org.blendee.support;

/**
 * UPDATE 句を設定するための関数型インターフェイスです。
 * @author 千葉 哲嗣
 * @param <R> 使用する {@link SelectStatement} のルートテーブル
 */
@FunctionalInterface
public interface UpdateOfferFunction<R extends TableFacadeRelationship> {

	/**
	 * @param relation 使用する {@link SelectStatement} のルートテーブル
	 * @return {@link Offers}
	 */
	Offers<Offerable> apply(R relation);
}
