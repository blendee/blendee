package org.blendee.support;

import org.blendee.sql.OrderByClause.Direction;

/**
 * ORDER BY 句に新しい要素を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 */
public class OrderByQueryColumn extends AbstractQueryColumn {

	/**
	 * ORDER BY 句に、このカラムを ASC として追加します。
	 */
	public final OrderByOffer ASC = new OrderByOffer(
		order -> relationship.getOrderByClause().add(order, column, Direction.ASC));

	/**
	 * ORDER BY 句に、このカラムを DESC として追加します。
	 */
	public final OrderByOffer DESC = new OrderByOffer(
		order -> relationship.getOrderByClause().add(order, column, Direction.DESC));

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param helper 条件作成に必要な情報を持った {@link QueryRelationship}
	 * @param name カラム名
	 */
	public OrderByQueryColumn(QueryRelationship helper, String name) {
		super(helper, name);
	}
}
