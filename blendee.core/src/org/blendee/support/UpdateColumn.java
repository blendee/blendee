package org.blendee.support;

import org.blendee.sql.Column;
import org.blendee.sql.RuntimeId;

/**
 * UPDATE 文に新しい要素を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 */
public class UpdateColumn extends SetElement implements ColumnSupplier {

	private final Column column;

	private final RuntimeId id;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param helper 条件作成に必要な情報を持った {@link TableFacadeRelationship}
	 * @param name カラム名
	 */
	public UpdateColumn(TableFacadeRelationship helper, String name) {
		super(helper);
		this.column = helper.getRelationship().getColumn(name);
		this.id = helper.getQueryId();
		addColumn(column);
	}

	@Override
	public Column column() {
		return column;
	}

	@Override
	public RuntimeId queryId() {
		return id;
	}
}
