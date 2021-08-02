package org.blendee.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.blendee.jdbc.MetadataCache.Request;
import org.blendee.jdbc.wrapperbase.MetadataBase;

/**
 * @author 千葉 哲嗣
 */
class CacheMetadata extends MetadataBase {

	private final MetadataCache cache = ContextManager.get(MetadataCache.class);

	private final Metadata base;

	CacheMetadata(Metadata metadata) {
		base = Objects.requireNonNull(metadata);
	}

	@Override
	protected Metadata base() {
		return base;
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
				return CacheMetadata.super.getTables(schemaName);
			}
		});

		return tables.clone();
	}

	@Override
	public TableMetadata getTableMetadata(TablePath path) {
		return cache.getTableMetadata(new Request<TablePath, TableMetadata>() {

			@Override
			TablePath createCacheKey() {
				return path;
			}

			@Override
			TableMetadata createCacheTarget() {
				return CacheMetadata.super.getTableMetadata(path);
			}
		});
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
				return CacheMetadata.super.getColumnMetadatas(path);
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
				return CacheMetadata.super.getPrimaryKeyMetadata(path);
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
				return CacheMetadata.super.getResourcesOfImportedKey(path);
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
				return CacheMetadata.super.getResourcesOfExportedKey(path);
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
				return CacheMetadata.super.getCrossReferences(exportedTable, importedTable);
			}
		});

		return references.clone();
	}
}
