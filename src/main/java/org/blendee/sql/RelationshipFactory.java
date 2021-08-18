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
	 * 現在のコンテキストのインスタンスを返します。
	 * @return {@link RelationshipFactory}
	 */
	public static RelationshipFactory getInstance() {
		return ContextManager.get(RelationshipFactory.class);
	}

	/**
	 * このクラスのコンストラクタです。<br>
	 * {@link ContextManager} 管理対象です。
	 * @see ContextManager#get(Class)
	 */
	public RelationshipFactory() {
	}

	/**
	 * {@link TablePath} が表すテーブルをルートとするテーブルツリーを作成します。
	 * @param path ルートとなるテーブル
	 * @return ツリーのルート要素
	 */
	public Relationship getInstance(TablePath path) {
		synchronized (lock) {
			if (pathIdMap.size() == 0) preparePathIdMap();
			var relationship = relationshipCache.get(path);
			if (relationship == null) {
				var pathId = pathIdMap.get(path);
				//path + " は使用できるテーブルに含まれていません"
				if (pathId == null) throw new IllegalArgumentException(path + " is not in the usable tables.");

				relationship = createRelationship(path, pathId);
				relationshipCache.put(path, relationship);
			}

			return relationship;
		}
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
		var schemaNames = BlendeeManager.get().getConfigure().getSchemaNames();
		var counter = 0;
		for (var name : schemaNames) {
			var paths = MetadataUtilities.getTables(name);
			counter += paths.length;
		}

		counter = 0;
		for (var name : schemaNames) {
			var paths = MetadataUtilities.getTables(name);
			for (var path : paths) {
				pathIdMap.put(path, "t" + counter);
				counter++;
			}
		}
	}

	private Relationship createRelationship(final TablePath path, String pathId) {
		return new ConcreteRelationship(
			null,
			null,
			null,
			path,
			pathId,
			ContextManager.get(BlendeeManager.class).getConfigure().getDataTypeConverter());
	}
}
