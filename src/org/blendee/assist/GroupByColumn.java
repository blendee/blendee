package org.blendee.assist;

import java.util.LinkedList;
import java.util.List;

import org.blendee.sql.Column;

/**
 * GROUP BY 句に新しい要素を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 */
public class GroupByColumn implements Offer, Offers<Offer> {

	private final TableFacadeAssist assist;

	private final Column column;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param assist 条件作成に必要な情報を持った {@link TableFacadeAssist}
	 * @param name カラム名
	 */
	public GroupByColumn(TableFacadeAssist assist, String name) {
		this.assist = assist;
		column = assist.getRelationship().getColumn(name);
	}

	@Override
	public void add(int order) {
		assist.getSelectStatement().getGroupByClause().add(order, column);
	}

	@Override
	public List<Offer> get() {
		List<Offer> offers = new LinkedList<>();
		offers.add(this);
		return offers;
	}
}