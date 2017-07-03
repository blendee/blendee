package org.blendee.jdbc;

import java.util.ArrayList;
import java.util.List;

import org.blendee.jdbc.MetadataCache.Request;
import org.blendee.jdbc.wrapperbase.ConnectionBase;

/**
 * @author 千葉 哲嗣
 */
class MetadataCacheConnection extends ConnectionBase {

	private final MetadataCache cache = BlendeeContext.get(MetadataCache.class);

	MetadataCacheConnection(BConnection conn) {
		super(conn);
	}

	@Override
	public TablePath[] getTables(final String schemaName) {
		TablePath[] tables = cache.getTables(new Request<String, TablePath[]>() {

			@Override
			String createCacheKey() {
				return schemaName;
			}

			@Override
			TablePath[] createCacheTarget() {
				return MetadataCacheConnection.super.getTables(schemaName);
			}
		});

		return tables.clone();
	}

	@Override
	public ColumnMetadata[] getColumnMetadatas(final TablePath path) {
		ColumnMetadata[] columnMetadatas = cache.getColumnMetadatas(new Request<TablePath, ColumnMetadata[]>() {

			@Override
			TablePath createCacheKey() {
				return path;
			}

			@Override
			ColumnMetadata[] createCacheTarget() {
				return MetadataCacheConnection.super.getColumnMetadatas(path);
			}
		});

		return columnMetadatas.clone();
	}

	@Override
	public PrimaryKeyMetadata getPrimaryKeyMetadata(TablePath path) {
		return cache.getPrimaryKeyMetadata(new Request<TablePath, PrimaryKeyMetadata>() {

			@Override
			TablePath createCacheKey() {
				return path;
			}

			@Override
			PrimaryKeyMetadata createCacheTarget() {
				return MetadataCacheConnection.super.getPrimaryKeyMetadata(path);
			}
		});
	}

	@Override
	public TablePath[] getResourcesOfImportedKey(final TablePath path) {
		TablePath[] paths = cache.getResourcesOfImportedKey(new Request<TablePath, TablePath[]>() {

			@Override
			TablePath createCacheKey() {
				return path;
			}

			@Override
			TablePath[] createCacheTarget() {
				return MetadataCacheConnection.super.getResourcesOfImportedKey(path);
			}
		});

		return paths.clone();
	}

	@Override
	public TablePath[] getResourcesOfExportedKey(final TablePath path) {
		TablePath[] paths = cache.getResourcesOfExportedKey(new Request<TablePath, TablePath[]>() {

			@Override
			TablePath createCacheKey() {
				return path;
			}

			@Override
			TablePath[] createCacheTarget() {
				return MetadataCacheConnection.super.getResourcesOfExportedKey(path);
			}
		});

		return paths.clone();
	}

	@Override
	public CrossReference[] getCrossReferences(
		final TablePath exportedTable,
		final TablePath importedTable) {
		CrossReference[] references = cache.getCrossReferences(new Request<List<TablePath>, CrossReference[]>() {

			@Override
			List<TablePath> createCacheKey() {
				List<TablePath> key = new ArrayList<TablePath>(2);
				key.add(exportedTable);
				key.add(importedTable);
				return key;
			}

			@Override
			CrossReference[] createCacheTarget() {
				return MetadataCacheConnection.super.getCrossReferences(exportedTable, importedTable);
			}
		});

		return references.clone();
	}
}
