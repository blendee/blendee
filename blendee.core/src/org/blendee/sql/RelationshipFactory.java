package org.blendee.sql;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
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
		synchronized (lock) {
			if (pathIdMap.size() == 0) preparePathIdMap();
			Relationship relationship = relationshipCache.get(path);
			if (relationship == null) {
				relationship = createRelationship(path);
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

	static DecimalFormat createDigitFormat(int max) {
		int digit = String.valueOf(max).length();
		StringBuilder zeros = new StringBuilder();
		for (int i = 0; i < digit; i++) {
			zeros.append('0');
		}

		return new DecimalFormat(zeros.toString());
	}

	private void preparePathIdMap() {
		String[] schemaNames = ContextManager.get(BlendeeManager.class).getConfigure().getSchemaNames();
		int counter = 0;
		for (String name : schemaNames) {
			TablePath[] paths = MetadataUtilities.getTables(name);
			counter += paths.length;
		}

		DecimalFormat format = createDigitFormat(counter);
		counter = 0;
		for (String name : schemaNames) {
			TablePath[] paths = MetadataUtilities.getTables(name);
			for (TablePath path : paths) {
				pathIdMap.put(path, "t" + format.format(counter));
				counter++;
			}
		}
	}

	private Relationship createRelationship(final TablePath path) {
		final String pathId = pathIdMap.get(path);
		if (pathId == null) throw new IllegalArgumentException(path + " は使用できるテーブルに含まれていません");

		return new ConcreteRelationship(
			null,
			null,
			null,
			path,
			pathId,
			new LinkedList<>(),
			ContextManager.get(BlendeeManager.class).getConfigure().getDataTypeConverter());
	}
}
