package org.blendee.support;

import java.util.LinkedList;
import java.util.List;

import org.blendee.sql.Column;

/**
 * INSERT 文に新しい要素を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 */
public class InsertColumn implements Offerable, Offers<Offerable> {

	private final TableFacadeRelationship relationship;

	private final Column column;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param helper 条件作成に必要な情報を持った {@link TableFacadeRelationship}
	 * @param name カラム名
	 */
	public InsertColumn(TableFacadeRelationship helper, String name) {
		relationship = helper;
		column = helper.getRelationship().getColumn(name);
	}

	@Override
	public void offer(int order) {
		relationship.getDataManipulationStatement().addInsertColumns(column);
	}

	@Override
	public List<Offerable> get() {
		List<Offerable> offers = new LinkedList<>();
		offers.add(this);
		return offers;
	}
}
