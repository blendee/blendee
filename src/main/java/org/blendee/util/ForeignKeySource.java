package org.blendee.util;

import org.blendee.internal.U;
import org.blendee.jdbc.ColumnMetadata;
import org.blendee.jdbc.TableMetadata;
import org.blendee.jdbc.TablePath;

/**
 * {@link TableSource} を構成するために必要な外部キー情報を表すクラスです。
 * @author 千葉 哲嗣
 * @see TableSource#TableSource(TablePath, TableMetadata, ColumnMetadata[], PrimaryKeySource, ForeignKeySource[])
 */
public class ForeignKeySource {

	/**
	 * 空配列
	 */
	public static final ForeignKeySource[] EMPTY_ARRAY = {};

	private final String name;

	private final String[] fkColumns;

	private final String[] pkColumns;

	private final TablePath imported;

	/**
	 * このクラスのインスタンスを生成します。
	 * @param name 外部キー名
	 * @param fkColumns FK 構成カラム
	 * @param pkColumns PK 構成カラム
	 * @param imported 参照先
	 */
	public ForeignKeySource(String name, String[] fkColumns, String[] pkColumns, TablePath imported) {
		this.name = name;
		this.fkColumns = fkColumns.clone();

		if (pkColumns == null) {
			this.pkColumns = U.STRING_EMPTY_ARRAY;
		} else {
			this.pkColumns = pkColumns.clone();
		}

		this.imported = imported;
	}

	/**
	 * 外部キー名を返します。
	 * @return 外部キー名
	 */
	public String getName() {
		return name;
	}

	/**
	 * FK 構成カラムを返します
	 * @return 構成カラム
	 */
	public String[] getFKColumns() {
		return fkColumns.clone();
	}

	/**
	 * PK 構成カラムを返します
	 * @return 構成カラム
	 */
	public String[] getPKColumns() {
		return pkColumns.clone();
	}

	/**
	 * 参照先を返します。
	 * @return 参照先
	 */
	public TablePath getImportedTable() {
		return imported;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
