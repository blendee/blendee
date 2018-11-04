package org.blendee.support;

import org.blendee.sql.Column;
import org.blendee.sql.Relationship;

/**
 * 汎用的な {@link SelectStatement} カラムクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 */
public class TableFacadeColumn {

	private final Column column;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param relationship 条件作成に必要な情報を持った {@link TableFacadeRelationship}
	 * @param name カラム名
	 */
	public TableFacadeColumn(Relationship relationship, String name) {
		column = relationship.getColumn(name);
	}

	/**
	 * このインスタンスが表すカラムを {@link Column} として返します。
	 * @return カラムインスタンス
	 */
	public Column getColumn() {
		return column;
	}
}
