package org.blendee.support;

import java.util.LinkedList;
import java.util.List;

import org.blendee.sql.ListQueryClause;

/**
 * GROUP BY 句に新しい要素を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 */
public class GroupByQueryColumn extends AbstractQueryColumn implements GroupByOffer, Offers<GroupByOffer> {

	private int order = ListQueryClause.DEFAULT_ORDER;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param helper 条件作成に必要な情報を持った {@link QueryRelationship}
	 * @param name カラム名
	 */
	public GroupByQueryColumn(QueryRelationship helper, String name) {
		super(helper, name);
	}

	@Override
	public void offer() {
		relationship.getGroupByClause().add(order, column);
	}

	@Override
	public List<GroupByOffer> get() {
		List<GroupByOffer> offers = new LinkedList<>();
		offers.add(this);
		return offers;
	}

	/**
	 * 他のクエリと JOIN した際などの、最終的な順位を指定します。
	 * @param order 最終的な GROUP BY 句内での順序
	 */
	void order(int order) {
		this.order = order;
	}
}