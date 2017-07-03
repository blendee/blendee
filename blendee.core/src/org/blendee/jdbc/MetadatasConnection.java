package org.blendee.jdbc;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.blendee.internal.U;
import org.blendee.jdbc.impl.SimplePrimaryKeyMetadata;
import org.blendee.jdbc.wrapperbase.ConnectionBase;

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
	 * @param path 対象となるテーブル
	 * @return テーブル定義情報
	 */
	@Override
	public TableMetadata getTableMetadata(TablePath path) {
		for (Metadata metadata : metadatas) {
			TableMetadata table = metadata.getTableMetadata(path);
			if (table != null) return table;
		}

		return null;
	}

	/**
	 * 前方にある {@link Metadata} の持つ {@link ColumnMetadata} が優先して使用されます。
	 */
	@Override
	public ColumnMetadata[] getColumnMetadatas(TablePath path) {
		Map<String, ColumnMetadata> map = new LinkedHashMap<>();
		for (Metadata metadata : metadatas) {
			ColumnMetadata[] metadatas = metadata.getColumnMetadatas(path);
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
	public PrimaryKeyMetadata getPrimaryKeyMetadata(TablePath path) {
		for (Metadata metadata : metadatas) {
			PrimaryKeyMetadata pk = metadata.getPrimaryKeyMetadata(path);
			if (pk != null) return pk;
		}

		return new SimplePrimaryKeyMetadata(null, U.STRING_EMPTY_ARRAY, false);
	}

	/**
	 * 全ての {@link Metadata} の持つ情報が統合されます。
	 */
	@Override
	public TablePath[] getResourcesOfImportedKey(TablePath path) {
		List<TablePath> list = new LinkedList<>();
		for (Metadata metadata : metadatas)
			list.addAll(Arrays.asList(metadata.getResourcesOfImportedKey(path)));

		return list.toArray(new TablePath[list.size()]);
	}

	/**
	 * 全ての {@link Metadata} の持つ情報が統合されます。
	 */
	@Override
	public TablePath[] getResourcesOfExportedKey(TablePath path) {
		List<TablePath> list = new LinkedList<>();
		for (Metadata metadata : metadatas)
			list.addAll(Arrays.asList(metadata.getResourcesOfExportedKey(path)));

		return list.toArray(new TablePath[list.size()]);
	}

	/**
	 * 全ての {@link Metadata} の持つ情報が統合されます。
	 */
	@Override
	public CrossReference[] getCrossReferences(TablePath exported, TablePath imported) {
		List<CrossReference> list = new LinkedList<>();
		for (Metadata metadata : metadatas)
			list.addAll(Arrays.asList(metadata.getCrossReferences(exported, imported)));

		return list.toArray(new CrossReference[list.size()]);
	}
}
