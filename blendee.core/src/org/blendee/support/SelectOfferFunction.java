package org.blendee.support;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.blendee.sql.Column;

/**
 * {@link Query} に SELECT 句を設定するための関数型インターフェイスです。
 * @author 千葉 哲嗣
 * @param <R> 使用する {@link Query} のルートテーブル
 */
@FunctionalInterface
public interface SelectOfferFunction<R extends QueryRelationship> {

	/**
	 * @param relation 使用する {@link Query} のルートテーブル
	 * @return {@link QueryRelationship#of(SelectOffer...)} で生成した {@link SelectOffers}
	 */
	SelectOffers offer(R relation);

	/**
	 * SELECT 句対象を束ねるクラスです。
	 */
	static class SelectOffers {

		private final List<ColumnExpression> expressions = new LinkedList<>();

		SelectOffers() {}

		/**
		 * 内部処理用なので直接使用しないこと。
		 * @param columns 追加カラム
		 */
		public void add(Column... columns) {
			for (Column column : columns) {
				expressions.add(new ColumnExpression(column));
			}
		}

		/**
		 * 内部処理用なので直接使用しないこと。
		 * @param containers 追加関数
		 */
		public void add(ColumnExpression... containers) {
			this.expressions.addAll(Arrays.asList(containers));
		}

		/**
		 * 内部処理用なので直接使用しないこと。
		 * @return {@link SelectOfferFunction} で設定された対象カラム
		 */
		public List<ColumnExpression> get() {
			return new LinkedList<>(expressions);
		}
	}
}
