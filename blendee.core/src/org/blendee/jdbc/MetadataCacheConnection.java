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
	public ResourceLocator[] getTables(final String schemaName) {
		ResourceLocator[] tables = cache.getTables(new Request<String, ResourceLocator[]>() {

			@Override
			String createCacheKey() {
				return schemaName;
			}

			@Override
			ResourceLocator[] createCacheTarget() {
				return MetadataCacheConnection.super.getTables(schemaName);
			}
		});

		return tables.clone();
	}

	@Override
	public ColumnMetadata[] getColumnMetadatas(final ResourceLocator locator) {
		ColumnMetadata[] columnMetadatas = cache.getColumnMetadatas(new Request<ResourceLocator, ColumnMetadata[]>() {

			@Override
			ResourceLocator createCacheKey() {
				return locator;
			}

			@Override
			ColumnMetadata[] createCacheTarget() {
				return MetadataCacheConnection.super.getColumnMetadatas(locator);
			}
		});

		return columnMetadatas.clone();
	}

	@Override
	public PrimaryKeyMetadata getPrimaryKeyMetadata(ResourceLocator locator) {
		return cache.getPrimaryKeyMetadata(new Request<ResourceLocator, PrimaryKeyMetadata>() {

			@Override
			ResourceLocator createCacheKey() {
				return locator;
			}

			@Override
			PrimaryKeyMetadata createCacheTarget() {
				return MetadataCacheConnection.super.getPrimaryKeyMetadata(locator);
			}
		});
	}

	@Override
	public ResourceLocator[] getResourcesOfImportedKey(final ResourceLocator locator) {
		ResourceLocator[] locators = cache.getResourcesOfImportedKey(new Request<ResourceLocator, ResourceLocator[]>() {

			@Override
			ResourceLocator createCacheKey() {
				return locator;
			}

			@Override
			ResourceLocator[] createCacheTarget() {
				return MetadataCacheConnection.super.getResourcesOfImportedKey(locator);
			}
		});

		return locators.clone();
	}

	@Override
	public ResourceLocator[] getResourcesOfExportedKey(final ResourceLocator locator) {
		ResourceLocator[] locators = cache.getResourcesOfExportedKey(new Request<ResourceLocator, ResourceLocator[]>() {

			@Override
			ResourceLocator createCacheKey() {
				return locator;
			}

			@Override
			ResourceLocator[] createCacheTarget() {
				return MetadataCacheConnection.super.getResourcesOfExportedKey(locator);
			}
		});

		return locators.clone();
	}

	@Override
	public CrossReference[] getCrossReferences(
		final ResourceLocator exportedTable,
		final ResourceLocator importedTable) {
		CrossReference[] references = cache.getCrossReferences(new Request<List<ResourceLocator>, CrossReference[]>() {

			@Override
			List<ResourceLocator> createCacheKey() {
				List<ResourceLocator> key = new ArrayList<ResourceLocator>(2);
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
