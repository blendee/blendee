package org.blendee.support;

/**
 * ORDER BY 句に新しい要素を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 * @param <T> self
 */
public class OrderByQueryColumn<T extends OrderByQueryColumn<?>> extends AbstractQueryColumn {

	//TODO
	private int order = Integer.MAX_VALUE;

	/**
	 * ORDER BY 句に、このカラムを ASC として追加します。
	 */
	public final OrderByOffer ASC = new OrderByOffer(() -> relationship.getOrderByClause().asc(column));

	/**
	 * ORDER BY 句に、このカラムを DESC として追加します。
	 */
	public final OrderByOffer DESC = new OrderByOffer(() -> relationship.getOrderByClause().desc(column));

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param helper 条件作成に必要な情報を持った {@link QueryRelationship}
	 * @param name カラム名
	 */
	public OrderByQueryColumn(QueryRelationship helper, String name) {
		super(helper, name);
	}

	/**
	 * 他のクエリと JOIN した際などの、最終的な順位を指定します。
	 * @param order 最終的な ORDER BY 句内での順序
	 * @return self
	 */
	@SuppressWarnings("unchecked")
	public T order(int order) {
		return (T) this;
	}
}
