package jp.ats.blendee.util;

import java.util.Objects;

import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.ColumnMetadata;
import jp.ats.blendee.jdbc.Metadata;
import jp.ats.blendee.jdbc.ResourceLocator;
import jp.ats.blendee.jdbc.TableMetadata;

/**
 * {@link Metadata} を構成するために必要なテーブル情報を表すクラスです。
 *
 * @author 千葉 哲嗣
 * @see VirtualSpace#addTable(TableSource)
 */
public class TableSource {

	private final ResourceLocator locator;

	private final TableMetadata tableMetadata;

	private final ColumnMetadata[] columnMetadatas;

	private final PrimaryKeySource pk;

	private final ForeignKeySource[] fks;

	/**
	 * このクラスのインスタンスを生成します。
	 *
	 * @param locator 対象となるテーブル
	 * @param tableMetadata 対象となるテーブルのメタデータ
	 * @param columnMetadatas 追加するカラム
	 * @param pk 追加する主キー
	 * @param fks 追加する外部キー
	 */
	public TableSource(
		ResourceLocator locator,
		TableMetadata tableMetadata,
		ColumnMetadata[] columnMetadatas,
		PrimaryKeySource pk,
		ForeignKeySource[] fks) {
		this.locator = Objects.requireNonNull(locator);
		this.columnMetadatas = Objects.requireNonNull(columnMetadatas);

		this.tableMetadata = tableMetadata;
		this.pk = pk;

		Objects.requireNonNull(fks);
		this.fks = fks.clone();
	}

	/**
	 * コンストラクタで渡された対象テーブルを返します。
	 *
	 * @return 対象テーブル
	 */
	public ResourceLocator getResourceLocator() {
		return locator;
	}

	/**
	 * コンストラクタで渡されたメタデータを返します。
	 *
	 * @return 対象となるテーブルのメタデータ
	 */
	public TableMetadata getTabelMetadata() {
		return tableMetadata;
	}

	/**
	 * コンストラクタで渡された追加カラム定義情報を返します。
	 *
	 * @return 追加カラム定義情報
	 */
	public ColumnMetadata[] getColumnMetadatas() {
		return columnMetadatas.clone();
	}

	/**
	 * コンストラクタで渡された追加主キーを返します。
	 *
	 * @return 追加主キー
	 */
	public PrimaryKeySource getPrimaryKeySource() {
		return pk;
	}

	/**
	 * コンストラクタで渡された追加外部キーを返します。
	 *
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
