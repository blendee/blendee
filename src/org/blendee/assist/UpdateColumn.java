package org.blendee.assist;

import org.blendee.sql.Column;

/**
 * UPDATE 文に新しい要素を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 */
public class UpdateColumn extends SetElement implements ColumnSupplier {

	private final Column column;

	private final Statement statement;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param helper 条件作成に必要な情報を持った {@link TableFacadeAssist}
	 * @param name カラム名
	 */
	public UpdateColumn(TableFacadeAssist helper, String name) {
		super(helper);
		this.column = helper.getRelationship().getColumn(name);
		this.statement = helper.getDataManipulationStatement();
		addColumn(column);
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
