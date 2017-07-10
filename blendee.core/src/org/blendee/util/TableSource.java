package org.blendee.util;

import java.util.Objects;

import org.blendee.internal.U;
import org.blendee.jdbc.ColumnMetadata;
import org.blendee.jdbc.Metadata;
import org.blendee.jdbc.TablePath;
import org.blendee.jdbc.TableMetadata;

/**
 * {@link Metadata} を構成するために必要なテーブル情報を表すクラスです。
 * @author 千葉 哲嗣
 * @see VirtualSpace#addTable(TableSource)
 */
public class TableSource {

	private final TablePath path;

	private final TableMetadata tableMetadata;

	private final ColumnMetadata[] columnMetadatas;

	private final PrimaryKeySource pk;

	private final ForeignKeySource[] fks;

	/**
	 * このクラスのインスタンスを生成します。
	 * @param path 対象となるテーブル
	 * @param tableMetadata 対象となるテーブルのメタデータ
	 * @param columnMetadatas 追加するカラム
	 * @param pk 追加する主キー
	 * @param fks 追加する外部キー
	 */
	public TableSource(
		TablePath path,
		TableMetadata tableMetadata,
		ColumnMetadata[] columnMetadatas,
		PrimaryKeySource pk,
		ForeignKeySource[] fks) {
		this.path = Objects.requireNonNull(path);
		this.columnMetadatas = Objects.requireNonNull(columnMetadatas);

		this.tableMetadata = tableMetadata;
		this.pk = pk;

		Objects.requireNonNull(fks);
		this.fks = fks.clone();
	}

	/**
	 * コンストラクタで渡された対象テーブルを返します。
	 * @return 対象テーブル
	 */
	public TablePath getTablePath() {
		return path;
	}

	/**
	 * コンストラクタで渡されたメタデータを返します。
	 * @return 対象となるテーブルのメタデータ
	 */
	public TableMetadata getTabelMetadata() {
		return tableMetadata;
	}

	/**
	 * コンストラクタで渡された追加カラム定義情報を返します。
	 * @return 追加カラム定義情報
	 */
	public ColumnMetadata[] getColumnMetadatas() {
		return columnMetadatas.clone();
	}

	/**
	 * コンストラクタで渡された追加主キーを返します。
	 * @return 追加主キー
	 */
	public PrimaryKeySource getPrimaryKeySource() {
		return pk;
	}

	/**
	 * コンストラクタで渡された追加外部キーを返します。
	 * @return 追加外部キー
	 */
	public ForeignKeySource[] getForeignKeySources() {
		return fks.clone();
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
