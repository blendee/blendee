package org.blendee.support;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.blendee.sql.Column;

/**
 * {@link Query} に SELECT 句を設定するための関数型インターフェイスです。
 *
 * @author 千葉 哲嗣
 * @param <R> 使用する {@link Query} のルートテーブル
 */
@FunctionalInterface
public interface SelectOfferFunction<R extends QueryRelationship> {

	/**
	 * @param relation 使用する {@link Query} のルートテーブル
	 * @return {@link QueryRelationship#as(SelectOffer...)} で生成した {@link SelectOffers}
	 */
	SelectOffers offer(R relation);

	/**
	 * SELECT 句対象を束ねるクラスです。
	 */
	public static class SelectOffers {

		private final List<Column> columns = new LinkedList<>();

		SelectOffers() {}

		/**
		 * 内部処理用なので直接使用しないこと。
		 *
		 * @param column 追加カラム
		 */
		public void add(Column... column) {
			columns.addAll(Arrays.asList(column));
		}

		/**
		 * 内部処理用なので直接使用しないこと。
		 *
		 * @return {@link SelectOfferFunction} で設定された対象カラム
		 */
		public List<Column> get() {
			return new LinkedList<>(columns);
		}
	}
}
