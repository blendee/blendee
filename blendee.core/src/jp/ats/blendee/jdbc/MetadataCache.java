package jp.ats.blendee.jdbc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 千葉 哲嗣
 */
public class MetadataCache implements ManagementSubject {

	private final Map<String, ResourceLocator[]> tableNamesCache = new HashMap<>();

	private final Map<ResourceLocator, ColumnMetadata[]> columnMetadatasCache = new HashMap<>();

	private final Map<ResourceLocator, PrimaryKeyMetadata> primaryKeyCache = new HashMap<>();

	private final Map<ResourceLocator, ResourceLocator[]> importedKeyCache = new HashMap<>();

	private final Map<ResourceLocator, ResourceLocator[]> exportedKeyCache = new HashMap<>();

	private final Map<List<ResourceLocator>, CrossReference[]> crossReferencesCache = new HashMap<>();

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

	ResourceLocator[] getTables(Request<String, ResourceLocator[]> request) {
		return execute(tableNamesCache, request);
	}

	ColumnMetadata[] getColumnMetadatas(Request<ResourceLocator, ColumnMetadata[]> request) {
		return execute(columnMetadatasCache, request);
	}

	PrimaryKeyMetadata getPrimaryKeyMetadata(Request<ResourceLocator, PrimaryKeyMetadata> request) {
		return execute(primaryKeyCache, request);
	}

	ResourceLocator[] getResourcesOfImportedKey(Request<ResourceLocator, ResourceLocator[]> request) {
		return execute(importedKeyCache, request);
	}

	ResourceLocator[] getResourcesOfExportedKey(Request<ResourceLocator, ResourceLocator[]> request) {
		return execute(exportedKeyCache, request);
	}

	CrossReference[] getCrossReferences(Request<List<ResourceLocator>, CrossReference[]> request) {
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
