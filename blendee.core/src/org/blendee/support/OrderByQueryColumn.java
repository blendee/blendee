package org.blendee.support;

/**
 * ORDER BY 句に新しい要素を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 * @param <T> 連続呼び出し用 {@link Query}
 */
public class OrderByQueryColumn<T> extends AbstractQueryColumn<T> {

	/**
	 * ORDER BY 句に、このカラムを ASC として追加します。
	 */
	public final OrderByOffer ASC;

	/**
	 * ORDER BY 句に、このカラムを DESC として追加します。
	 */
	public final OrderByOffer DESC;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param helper 条件作成に必要な情報を持った {@link QueryRelationship}
	 * @param name カラム名
	 */
	public OrderByQueryColumn(QueryRelationship helper, String name) {
		super(helper, name);
		ASC = () -> relationship.getOrderByClause().asc(column);
		DESC = () -> relationship.getOrderByClause().desc(column);
	}

	/**
	 * @param column
	 * @param asc
	 * @param desc
	 */
	public OrderByQueryColumn(
		OrderByQueryColumn<T> column,
		OrderByOffer asc,
		OrderByOffer desc) {
		super(column);
		ASC = asc;
		DESC = desc;
	}
}
