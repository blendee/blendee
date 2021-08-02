package org.blendee.jdbc;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.blendee.internal.U;
import org.blendee.jdbc.impl.SimplePrimaryKeyMetadata;
import org.blendee.jdbc.wrapperbase.MetadataBase;

/**
 * @author 千葉 哲嗣
 */
public class Metadatas extends MetadataBase {

	private final Metadata[] metadatas;

	private final Metadata base;

	/**
	 * @param metadatas {@link Metadata}
	 */
	public Metadatas(Metadata... metadatas) {
		base = metadatas[0];

		Objects.requireNonNull(base);

		this.metadatas = metadatas.clone();
	}

	@Override
	protected Metadata base() {
		return base;
	}

	@Override
	public TablePath[] getTables(String schemaName) {
		Set<TablePath> set = new LinkedHashSet<>();
		for (Metadata metadata : metadatas) {
			Arrays.stream(metadata.getTables(schemaName)).forEach(t -> {
				if (!set.contains(t)) set.add(t);
			});
		}

		return set.toArray(new TablePath[set.size()]);
	}

	/**
	 * 前方にある {@link Metadata} の持つ {@link TableMetadata} が優先して使用されます。
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
	 * 前方にある {@link Metadata} の持つキー情報が優先して使用されます。
	 */
	@Override
	public TablePath[] getResourcesOfImportedKey(TablePath path) {
		Set<TablePath> set = new LinkedHashSet<>();
		for (Metadata metadata : metadatas) {
			Arrays.stream(metadata.getResourcesOfImportedKey(path)).forEach(p -> {
				if (!set.contains(p))
					set.add(p);
			});
		}

		return set.toArray(new TablePath[set.size()]);
	}

	/**
	 * 前方にある {@link Metadata} の持つキー情報が優先して使用されます。
	 */
	@Override
	public TablePath[] getResourcesOfExportedKey(TablePath path) {
		Set<TablePath> set = new LinkedHashSet<>();
		for (Metadata metadata : metadatas) {
			Arrays.stream(metadata.getResourcesOfExportedKey(path)).forEach(p -> {
				if (!set.contains(p))
					set.add(p);
			});
		}

		return set.toArray(new TablePath[set.size()]);
	}

	/**
	 * 全ての {@link Metadata} の持つ情報が統合されます。
	 */
	@Override
	public CrossReference[] getCrossReferences(TablePath exported, TablePath imported) {
		Map<String, CrossReference> map = new LinkedHashMap<>();
		for (Metadata metadata : metadatas) {
			Arrays.stream(metadata.getCrossReferences(exported, imported)).forEach(c -> {
				String key = c.getForeignKeyName();
				if (!map.containsKey(key)) map.put(key, c);
			});
		}

		return map.values().toArray(new CrossReference[map.size()]);
	}
}
