package jp.ats.blendee.util;

import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.ColumnMetadata;
import jp.ats.blendee.jdbc.ResourceLocator;
import jp.ats.blendee.jdbc.TableMetadata;

/**
 * {@link TableSource} を構成するために必要な外部キー情報を表すクラスです。
 *
 * @author 千葉 哲嗣
 * @see TableSource#TableSource(ResourceLocator, TableMetadata, ColumnMetadata[], PrimaryKeySource, ForeignKeySource[])
 */
public class ForeignKeySource {

	/**
	 * 空配列
	 */
	public static final ForeignKeySource[] EMPTY_ARRAY = {};

	private final String name;

	private final String[] columns;

	private final ResourceLocator imported;

	/**
	 * このクラスのインスタンスを生成します。
	 *
	 * @param name 外部キー名
	 * @param columns 構成カラム
	 * @param imported 参照先
	 */
	public ForeignKeySource(String name, String[] columns, ResourceLocator imported) {
		this.name = name;
		this.columns = columns.clone();
		this.imported = imported;
	}

	/**
	 * コンストラクタで渡された外部キー名を返します。
	 *
	 * @return 外部キー名
	 */
	public String getName() {
		return name;
	}

	/**
	 * コンストラクタで渡された構成カラムを返します
	 *
	 * @return 構成カラム
	 */
	public String[] getColumns() {
		return columns.clone();
	}

	/**
	 * コンストラクタで渡された参照先を返します。
	 *
	 * @return 参照先
	 */
	public ResourceLocator getImportedTable() {
		return imported;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
