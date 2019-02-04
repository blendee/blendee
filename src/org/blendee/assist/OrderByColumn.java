package org.blendee.assist;

import java.util.LinkedList;
import java.util.List;

import org.blendee.sql.Column;
import org.blendee.sql.OrderByClause.Direction;

/**
 * ORDER BY 句に新しい要素を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 */
public class OrderByColumn implements Offer, Offers<Offer>, AssistColumn {

	/**
	 * ORDER BY 句に、このカラムを ASC として追加します。
	 */
	public final ListClauseOffer ASC;

	/**
	 * ORDER BY 句に、このカラムを DESC として追加します。
	 */
	public final ListClauseOffer DESC;

	/**
	 * ORDER BY 句に、このカラムを ASC NULLS FIRST として追加します。
	 */
	public final ListClauseOffer ASC_NULLS_FIRST;

	/**
	 * ORDER BY 句に、このカラムを ASC NULLS LAST として追加します。
	 */
	public final ListClauseOffer ASC_NULLS_LAST;

	/**
	 * ORDER BY 句に、このカラムを DESC NULLS FIRST として追加します。
	 */
	public final ListClauseOffer DESC_NULLS_FIRST;

	/**
	 * ORDER BY 句に、このカラムを DESC NULLS LAST として追加します。
	 */
	public final ListClauseOffer DESC_NULLS_LAST;

	private final ListClauseOffer NONE;

	private final Column column;

	private final Statement statement;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param assist 条件作成に必要な情報を持った {@link TableFacadeAssist}
	 * @param name カラム名
	 */
	public OrderByColumn(TableFacadeAssist assist, String name) {
		column = assist.getRelationship().getColumn(name);
		ASC = new ListClauseOffer(
			order -> assist.getSelectStatement().getOrderByClause().add(order, column, Direction.ASC));
		DESC = new ListClauseOffer(
			order -> assist.getSelectStatement().getOrderByClause().add(order, column, Direction.DESC));
		ASC_NULLS_FIRST = new ListClauseOffer(
			order -> assist.getSelectStatement().getOrderByClause().add(order, column, Direction.ASC_NULLS_FIRST));
		ASC_NULLS_LAST = new ListClauseOffer(
			order -> assist.getSelectStatement().getOrderByClause().add(order, column, Direction.ASC_NULLS_LAST));
		DESC_NULLS_FIRST = new ListClauseOffer(
			order -> assist.getSelectStatement().getOrderByClause().add(order, column, Direction.DESC_NULLS_FIRST));
		DESC_NULLS_LAST = new ListClauseOffer(
			order -> assist.getSelectStatement().getOrderByClause().add(order, column, Direction.DESC_NULLS_LAST));
		NONE = new ListClauseOffer(
			order -> assist.getSelectStatement().getOrderByClause().add(order, column, Direction.NONE));

		statement = assist.getSelectStatement();
	}

	@Override
	public void add(int order) {
		NONE.add(order);
	}

	@Override
	public List<Offer> get() {
		List<Offer> offers = new LinkedList<>();
		offers.add(this);
		return offers;
	}

	@Override
	public Column column() {
		return column;
	}

	@Override
	public Statement statement() {
		return statement;
	}
}
