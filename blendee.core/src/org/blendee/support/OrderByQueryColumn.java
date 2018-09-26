package org.blendee.support;

import java.util.LinkedList;
import java.util.List;

import org.blendee.sql.Column;
import org.blendee.sql.OrderByClause.Direction;

/**
 * ORDER BY 句に新しい要素を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 */
public class OrderByQueryColumn implements Offerable, Offers<Offerable> {

	/**
	 * ORDER BY 句に、このカラムを ASC として追加します。
	 */
	public final OrderByOffer ASC;

	/**
	 * ORDER BY 句に、このカラムを DESC として追加します。
	 */
	public final OrderByOffer DESC;

	/**
	 * ORDER BY 句に、このカラムを ASC NULLS FIRST として追加します。
	 */
	public final OrderByOffer ASC_NULLS_FIRST;

	/**
	 * ORDER BY 句に、このカラムを ASC NULLS LAST として追加します。
	 */
	public final OrderByOffer ASC_NULLS_LAST;

	/**
	 * ORDER BY 句に、このカラムを DESC NULLS FIRST として追加します。
	 */
	public final OrderByOffer DESC_NULLS_FIRST;

	/**
	 * ORDER BY 句に、このカラムを DESC NULLS LAST として追加します。
	 */
	public final OrderByOffer DESC_NULLS_LAST;

	private final OrderByOffer NONE;

	private final Column column;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param helper 条件作成に必要な情報を持った {@link QueryRelationship}
	 * @param name カラム名
	 */
	public OrderByQueryColumn(QueryRelationship helper, String name) {
		column = helper.getRelationship().getColumn(name);
		ASC = new OrderByOffer(
			order -> helper.getOrderByClause().add(order, column, Direction.ASC));
		DESC = new OrderByOffer(
			order -> helper.getOrderByClause().add(order, column, Direction.DESC));
		ASC_NULLS_FIRST = new OrderByOffer(
			order -> helper.getOrderByClause().add(order, column, Direction.ASC_NULLS_FIRST));
		ASC_NULLS_LAST = new OrderByOffer(
			order -> helper.getOrderByClause().add(order, column, Direction.ASC_NULLS_LAST));
		DESC_NULLS_FIRST = new OrderByOffer(
			order -> helper.getOrderByClause().add(order, column, Direction.DESC_NULLS_FIRST));
		DESC_NULLS_LAST = new OrderByOffer(
			order -> helper.getOrderByClause().add(order, column, Direction.DESC_NULLS_LAST));
		NONE = new OrderByOffer(
			order -> helper.getOrderByClause().add(order, column, Direction.NONE));
	}

	@Override
	public void offer(int order) {
		NONE.offer(order);
	}

	@Override
	public List<Offerable> get() {
		List<Offerable> offers = new LinkedList<>();
		offers.add(this);
		return offers;
	}

	Column column() {
		return column;
	}
}
