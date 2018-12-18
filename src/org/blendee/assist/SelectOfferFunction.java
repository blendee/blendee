package org.blendee.assist;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.blendee.sql.Column;

/**
 * {@link SelectStatement} に SELECT 句を設定するための関数型インターフェイスです。
 * @author 千葉 哲嗣
 * @param <S> 使用する {@link SelectStatement} のルートテーブル
 */
@FunctionalInterface
public interface SelectOfferFunction<S extends SelectClauseAssist> {

	/**
	 * @param relation 使用する {@link SelectStatement} のルートテーブル
	 * @return {@link SelectClauseAssist#list(SelectOffer...)} で生成した {@link SelectOffers}
	 */
	Offers<ColumnExpression> apply(S relation);

	/**
	 * SELECT 句対象を束ねるクラスです。
	 */
	static class SelectOffers implements SelectOffer {

		private final List<ColumnExpression> expressions = new LinkedList<>();

		private final SelectStatement statement;

		SelectOffers(SelectStatement statement) {
			this.statement = statement;
		}

		/**
		 * 内部処理用なので直接使用しないこと。
		 * @param columns 追加カラム
		 */
		public void add(Column... columns) {
			for (Column column : columns) {
				expressions.add(new ColumnExpression(statement, column));
			}
		}

		/**
		 * 内部処理用なので直接使用しないこと。
		 * @param containers 追加関数
		 */
		public void add(ColumnExpression... containers) {
			expressions.addAll(Arrays.asList(containers));
		}

		/**
		 * 内部処理用なので直接使用しないこと。
		 * @return {@link SelectOfferFunction} で設定された対象カラム
		 */
		@Override
		public List<ColumnExpression> get() {
			return new LinkedList<>(expressions);
		}
	}
}
