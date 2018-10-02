package org.blendee.support;

import org.blendee.sql.Column;

/**
 * UPDATE 文に新しい要素を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 */
public class UpdateColumn extends SetElement {

	private final Column column;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param helper 条件作成に必要な情報を持った {@link TableFacadeRelationship}
	 * @param name カラム名
	 */
	public UpdateColumn(TableFacadeRelationship helper, String name) {
		super(helper);
		this.column = helper.getRelationship().getColumn(name);
		addColumn(column);
	}

	Column column() {
		return column;
	}
}
