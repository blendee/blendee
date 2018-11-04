package org.blendee.jdbc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 千葉 哲嗣
 */
public class MetadataCache implements ManagementSubject {

	private final Map<String, TablePath[]> tableNamesCache = new HashMap<>();

	private final Map<TablePath, ColumnMetadata[]> columnMetadatasCache = new HashMap<>();

	private final Map<TablePath, PrimaryKeyMetadata> primaryKeyCache = new HashMap<>();

	private final Map<TablePath, TablePath[]> importedKeyCache = new HashMap<>();

	private final Map<TablePath, TablePath[]> exportedKeyCache = new HashMap<>();

	private final Map<List<TablePath>, CrossReference[]> crossReferencesCache = new HashMap<>();

	/**
	 * キャッシュを消去します。
	 */
	public void clearCache() {
		clearCacheInternal(tableNamesCache);
		clearCacheInternal(columnMetadatasCache);
		clearCacheInternal(primaryKeyCache);
		clearCacheInternal(importedKeyCache);
		clearCacheInternal(exportedKeyCache);
		clearCacheInternal(crossReferencesCache);
	}

	TablePath[] getTables(Request<String, TablePath[]> request) {
		return execute(tableNamesCache, request);
	}

	ColumnMetadata[] getColumnMetadatas(Request<TablePath, ColumnMetadata[]> request) {
		return execute(columnMetadatasCache, request);
	}

	PrimaryKeyMetadata getPrimaryKeyMetadata(Request<TablePath, PrimaryKeyMetadata> request) {
		return execute(primaryKeyCache, request);
	}

	TablePath[] getResourcesOfImportedKey(Request<TablePath, TablePath[]> request) {
		return execute(importedKeyCache, request);
	}

	TablePath[] getResourcesOfExportedKey(Request<TablePath, TablePath[]> request) {
		return execute(exportedKeyCache, request);
	}

	CrossReference[] getCrossReferences(Request<List<TablePath>, CrossReference[]> request) {
		return execute(crossReferencesCache, request);
	}

	private <K, T> T execute(Map<K, T> cache, Request<K, T> request) {
		synchronized (cache) {
			K key = request.createCacheKey();
			T target = cache.get(key);
			if (target != null) return target;

			target = request.createCacheTarget();
			cache.put(key, target);
			return target;
		}
	}

	private static void clearCacheInternal(Map<?, ?> cache) {
		synchronized (cache) {
			cache.clear();
		}
	}

	static abstract class Request<K, T> {

		abstract K createCacheKey();

		abstract T createCacheTarget();
	}
}
