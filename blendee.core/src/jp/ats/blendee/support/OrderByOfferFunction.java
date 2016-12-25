package jp.ats.blendee.support;

/**
 * {@link Query} に ORDER BY 句を設定するための関数型インターフェイスです。
 *
 * @author 千葉 哲嗣
 *
 * @param <R> 使用する {@link Query} のルートテーブル
 */
@FunctionalInterface
public interface OrderByOfferFunction<R extends QueryRelationship> {

	/**
	 * @param relation 使用する {@link Query} のルートテーブル
	 */
	void offer(R relation);
}
