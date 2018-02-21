package org.blendee.support;

import org.blendee.support.SelectOfferFunction.SelectOffers;

/**
 * ORDER BY 句に新しい要素を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 * @param <T> 連続呼び出し用 {@link Query}
 */
public class SelectQueryColumn<T> extends AbstractQueryColumn<T> implements SelectOffer {

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param helper 条件作成に必要な情報を持った {@link QueryRelationship}
	 * @param name カラム名
	 */
	public SelectQueryColumn(QueryRelationship helper, String name) {
		super(helper, name);
	}

	@Override
	public void accept(SelectOffers offers) {
		offers.add(column);
	}
}
