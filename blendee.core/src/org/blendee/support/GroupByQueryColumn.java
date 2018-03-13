package org.blendee.support;

import java.util.LinkedList;
import java.util.List;

/**
 * GROUP BY 句に新しい要素を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 * @param <T> 連続呼び出し用 {@link Query}
 */
public class GroupByQueryColumn<T> extends AbstractQueryColumn<T> implements GroupByOffer, Offers<GroupByOffer> {

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
		relationship.getGroupByClause().add(column);
	}

	@Override
	public List<GroupByOffer> get() {
		List<GroupByOffer> offers = new LinkedList<>();
		offers.add(this);
		return offers;
	}
}
