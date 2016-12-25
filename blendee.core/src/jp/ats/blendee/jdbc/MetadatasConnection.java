package jp.ats.blendee.jdbc;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.impl.SimplePrimaryKeyMetadata;
import jp.ats.blendee.jdbc.wrapperbase.ConnectionBase;

/**
 * @author 千葉 哲嗣
 */
class MetadatasConnection extends ConnectionBase {

	private final Metadata[] metadatas;

	MetadatasConnection(BConnection base, Metadata[] metadatas) {
		super(base);
		this.metadatas = new Metadata[metadatas.length + 1];

		System.arraycopy(metadatas, 0, this.metadatas, 0, metadatas.length);

		this.metadatas[metadatas.length] = base;
	}

	/**
	 * 前方にある {@link Metadata} の持つ {@link TableMetadata} が優先して使用されます。
	 *
	 * @param locator 対象となるテーブル
	 * @return テーブル定義情報
	 */
	@Override
	public TableMetadata getTableMetadata(ResourceLocator locator) {
		for (Metadata metadata : metadatas) {
			TableMetadata table = metadata.getTableMetadata(locator);
			if (table != null) return table;
		}

		return null;
	}

	/**
	 * 前方にある {@link Metadata} の持つ {@link ColumnMetadata} が優先して使用されます。
	 */
	@Override
	public ColumnMetadata[] getColumnMetadatas(ResourceLocator locator) {
		Map<String, ColumnMetadata> map = new LinkedHashMap<>();
		for (Metadata metadata : metadatas) {
			ColumnMetadata[] metadatas = metadata.getColumnMetadatas(locator);
			for (ColumnMetadata column : metadatas) {
				String name = column.getName();
				if (!map.containsKey(name)) map.put(name, column);
			}
		}

		return map.values().toArray(new ColumnMetadata[map.size()]);
	}

	/**
	 * 前方にある {@link Metadata} の持つ主キー情報が優先して使用されます。
	 */
	@Override
	public PrimaryKeyMetadata getPrimaryKeyMetadata(ResourceLocator locator) {
		for (Metadata metadata : metadatas) {
			PrimaryKeyMetadata pk = metadata.getPrimaryKeyMetadata(locator);
			if (pk != null) return pk;
		}

		return new SimplePrimaryKeyMetadata(null, U.STRING_EMPTY_ARRAY, false);
	}

	/**
	 * 全ての {@link Metadata} の持つ情報が統合されます。
	 */
	@Override
	public ResourceLocator[] getResourcesOfImportedKey(ResourceLocator locator) {
		List<ResourceLocator> list = new LinkedList<>();
		for (Metadata metadata : metadatas)
			list.addAll(Arrays.asList(metadata.getResourcesOfImportedKey(locator)));

		return list.toArray(new ResourceLocator[list.size()]);
	}

	/**
	 * 全ての {@link Metadata} の持つ情報が統合されます。
	 */
	@Override
	public ResourceLocator[] getResourcesOfExportedKey(ResourceLocator locator) {
		List<ResourceLocator> list = new LinkedList<>();
		for (Metadata metadata : metadatas)
			list.addAll(Arrays.asList(metadata.getResourcesOfExportedKey(locator)));

		return list.toArray(new ResourceLocator[list.size()]);
	}

	/**
	 * 全ての {@link Metadata} の持つ情報が統合されます。
	 */
	@Override
	public CrossReference[] getCrossReferences(ResourceLocator exported, ResourceLocator imported) {
		List<CrossReference> list = new LinkedList<>();
		for (Metadata metadata : metadatas)
			list.addAll(Arrays.asList(metadata.getCrossReferences(exported, imported)));

		return list.toArray(new CrossReference[list.size()]);
	}
}
