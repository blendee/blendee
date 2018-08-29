package org.blendee.jdbc.wrapperbase;

import org.blendee.jdbc.ColumnMetadata;
import org.blendee.jdbc.CrossReference;
import org.blendee.jdbc.Metadata;
import org.blendee.jdbc.PrimaryKeyMetadata;
import org.blendee.jdbc.StoredIdentifier;
import org.blendee.jdbc.TableMetadata;
import org.blendee.jdbc.TablePath;

/**
 * {@link Metadata} のラッパーを実装するベースとなる、抽象基底クラスです。
 * @author 千葉 哲嗣
 */
public abstract class MetadataBase implements Metadata {

	private final Metadata base;

	/**
	 * ラップするインスタンスを受け取るコンストラクタです。
	 * @param base ベースとなるインスタンス
	 */
	protected MetadataBase(Metadata base) {
		this.base = base;
	}

	@Override
	public TablePath[] getTables(String schemaName) {
		return base.getTables(schemaName);
	}

	@Override
	public TableMetadata getTableMetadata(TablePath path) {
		return base.getTableMetadata(path);
	}

	@Override
	public ColumnMetadata[] getColumnMetadatas(TablePath path) {
		return base.getColumnMetadatas(path);
	}

	@Override
	public PrimaryKeyMetadata getPrimaryKeyMetadata(TablePath path) {
		return base.getPrimaryKeyMetadata(path);
	}

	@Override
	public TablePath[] getResourcesOfImportedKey(TablePath path) {
		return base.getResourcesOfImportedKey(path);
	}

	@Override
	public TablePath[] getResourcesOfExportedKey(TablePath path) {
		return base.getResourcesOfExportedKey(path);
	}

	@Override
	public CrossReference[] getCrossReferences(
		TablePath exportedTable,
		TablePath importedTable) {
		return base.getCrossReferences(exportedTable, importedTable);
	}

	@Override
	public StoredIdentifier getStoredIdentifier() {
		return base.getStoredIdentifier();
	}

	@Override
	public String toString() {
		return base.toString();
	}

	@Override
	public boolean equals(Object o) {
		return base.equals(o);
	}

	@Override
	public int hashCode() {
		return base.hashCode();
	}
}
