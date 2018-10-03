package org.blendee.sql;

import java.util.HashMap;
import java.util.Map;

import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.ManagementSubject;
import org.blendee.jdbc.MetadataUtilities;
import org.blendee.jdbc.TablePath;

/**
 * {@link Relationship} の生成、管理を行うファクトリクラスです。
 * @author 千葉 哲嗣
 */
public class RelationshipFactory implements ManagementSubject {

	private final Object lock = new Object();

	private final Map<TablePath, Relationship> relationshipCache = new HashMap<>();

	private final Map<TablePath, String> pathIdMap = new HashMap<>();

	/**
	 * このクラスのコンストラクタです。<br>
	 * {@link ContextManager} 管理対象です。
	 * @see ContextManager#get(Class)
	 */
	public RelationshipFactory() {}

	/**
	 * {@link TablePath} が表すテーブルをルートとするテーブルツリーを作成します。
	 * @param path ルートとなるテーブル
	 * @return ツリーのルート要素
	 */
	public Relationship getInstance(TablePath path) {
		if (path instanceof AliasTablePath) return getInstance((AliasTablePath) path);

		synchronized (lock) {
			if (pathIdMap.size() == 0) preparePathIdMap();
			Relationship relationship = relationshipCache.get(path);
			if (relationship == null) {
				relationship = createRelationship(path, null);
				relationshipCache.put(path, relationship);
			}

			return relationship;
		}
	}

	/**
	 * {@link TablePath} が表すテーブルをルートとするテーブルツリーを作成します。<br>
	 * キャッシュを行いません。
	 * @param path ルートとなるテーブル
	 * @return ツリーのルート要素
	 */
	public Relationship getInstance(AliasTablePath path) {
		return createRelationship(path, path.getAlias());
	}

	/**
	 * キャッシュをクリアします。
	 */
	public void clearCache() {
		synchronized (lock) {
			relationshipCache.clear();
			pathIdMap.clear();
		}
	}

	private void preparePathIdMap() {
		String[] schemaNames = ContextManager.get(BlendeeManager.class).getConfigure().getSchemaNames();
		int counter = 0;
		for (String name : schemaNames) {
			TablePath[] paths = MetadataUtilities.getTables(name);
			counter += paths.length;
		}

		counter = 0;
		for (String name : schemaNames) {
			TablePath[] paths = MetadataUtilities.getTables(name);
			for (TablePath path : paths) {
				pathIdMap.put(path, "t" + counter);
				counter++;
			}
		}
	}

	private Relationship createRelationship(final TablePath path, String alias) {
		String pathId;
		if (alias == null) {
			pathId = pathIdMap.get(path);
			if (pathId == null) throw new IllegalArgumentException(path + " は使用できるテーブルに含まれていません");
		} else {
			pathId = alias;
		}

		return new ConcreteRelationship(
			null,
			null,
			null,
			path,
			pathId,
			ContextManager.get(BlendeeManager.class).getConfigure().getDataTypeConverter());
	}
}
