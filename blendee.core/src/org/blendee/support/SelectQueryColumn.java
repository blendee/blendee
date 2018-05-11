package org.blendee.support;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.blendee.support.SelectOfferFunction.SelectOffers;

/**
 * ORDER BY 句に新しい要素を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 */
public class SelectQueryColumn extends AbstractQueryColumn implements SelectOffer, Offers<ColumnExpression> {

	private ColumnExpression expression;

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
		if (expression != null) {
			offers.add(expression);
		} else {
			offers.add(column);
		}
	}

	@Override
	public List<ColumnExpression> get() {
		List<ColumnExpression> list = new LinkedList<>();
		if (expression != null) {
			list.add(expression);
		} else {
			list.add(new ColumnExpression(column));
		}

		return list;
	}

	/**
	 * カラムに別名を付けます。<br>
	 * 別名をつけてしまうと {@link Executor#aggregate(Consumer)} しか使用できなくなります。
	 * @param alias 別名
	 * @return {@link SelectOffer}
	 */
	public SelectOffer AS(String alias) {
		relationship.getRoot().quitRowMode();
		expression = new ColumnExpression(column);
		expression.appendAlias(alias);
		return this;
	}
}
