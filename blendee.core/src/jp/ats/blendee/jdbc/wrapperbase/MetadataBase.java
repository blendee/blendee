package jp.ats.blendee.jdbc.wrapperbase;

import jp.ats.blendee.jdbc.ColumnMetadata;
import jp.ats.blendee.jdbc.CrossReference;
import jp.ats.blendee.jdbc.Metadata;
import jp.ats.blendee.jdbc.PrimaryKeyMetadata;
import jp.ats.blendee.jdbc.ResourceLocator;
import jp.ats.blendee.jdbc.TableMetadata;

/**
 * {@link Metadata} のラッパーを実装するベースとなる、抽象基底クラスです。
 *
 * @author 千葉 哲嗣
 */
public abstract class MetadataBase implements Metadata {

	private final Metadata base;

	/**
	 * ラップするインスタンスを受け取るコンストラクタです。
	 *
	 * @param base ベースとなるインスタンス
	 */
	protected MetadataBase(Metadata base) {
		this.base = base;
	}

	@Override
	public ResourceLocator[] getTables(String schemaName) {
		return base.getTables(schemaName);
	}

	@Override
	public TableMetadata getTableMetadata(ResourceLocator locator) {
		return base.getTableMetadata(locator);
	}

	@Override
	public ColumnMetadata[] getColumnMetadatas(ResourceLocator locator) {
		return base.getColumnMetadatas(locator);
	}

	@Override
	public PrimaryKeyMetadata getPrimaryKeyMetadata(ResourceLocator locator) {
		return base.getPrimaryKeyMetadata(locator);
	}

	@Override
	public ResourceLocator[] getResourcesOfImportedKey(ResourceLocator locator) {
		return base.getResourcesOfImportedKey(locator);
	}

	@Override
	public ResourceLocator[] getResourcesOfExportedKey(ResourceLocator locator) {
		return base.getResourcesOfExportedKey(locator);
	}

	@Override
	public CrossReference[] getCrossReferences(
		ResourceLocator exportedTable,
		ResourceLocator importedTable) {
		return base.getCrossReferences(exportedTable, importedTable);
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
