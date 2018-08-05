package org.blendee.sql;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.blendee.internal.CollectionMap;
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

	private final Map<TablePath, String> pathIDMap = new HashMap<>();

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
			if (pathIDMap.size() == 0) preparePathIDMap();
			Relationship relationship = relationshipCache.get(path);
			if (relationship == null) {
				relationship = createRelationship(path);
				relationshipCache.put(path, relationship);
			}

			return relationship;
		}
	}

	/**
	 * target を、 root のツリーに含まれる {@link Relationship} に変換します。
	 * @param root 含まれるべきツリーのルート
	 * @param target 対象
	 * @return 変換された {@link Relationship}
	 * @throws IllegalStateException 結合できないテーブルを使用している場合
	 * @throws IllegalStateException ツリー内に同一テーブルが複数あるため、あいまいな指定がされている場合
	 */
	public static Relationship convert(Relationship root, TablePath target) {
		//問題となるカラムが含まれる Relationship を元になる SQL 文の Relationship ツリーから取得
		Relationship[] converted = root.convert(target);
		if (converted.length == 1) {
			//Relationship が一つの場合は、それが正解なので return
			return converted[0];
		} else if (converted.length == 0) {
			//Relationship が 0 の場合は、結合できないテーブルを使用しているのでエラー
			throw new IllegalStateException(
				target + " は、 " + root + " Relationship のツリー内に含まれていません。");
		} else {
			//Relationship が複数件の場合は、ツリー内に同一テーブルが複数あるため、あいまいな
			//指定がされているということでエラー
			//解決方法は、元になる SQL 文の Relationship ツリーから取得した Relationship を使用すること
			throw new IllegalStateException(
				target + " は、" + root + "  Relationship のツリー内に複数存在するため、特定できません。");
		}
	}

	/**
	 * キャッシュをクリアします。
	 */
	public void clearCache() {
		synchronized (lock) {
			relationshipCache.clear();
			pathIDMap.clear();
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

	private void preparePathIDMap() {
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
				pathIDMap.put(path, "t" + format.format(counter));
				counter++;
			}
		}
	}

	private Relationship createRelationship(final TablePath path) {
		final String pathID = pathIDMap.get(path);
		if (pathID == null) throw new IllegalArgumentException(path + " は使用できるテーブルに含まれていません");

		List<TablePath> relationshipPath = new LinkedList<>();

		return new ConcreteRelationship(
			null,
			null,
			null,
			path,
			pathID,
			relationshipPath,
			ContextManager.get(BlendeeManager.class).getConfigure().getDataTypeConverter(),
			new CollectionMap<>());
	}
}
