package jp.ats.blendee.sql;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import jp.ats.blendee.internal.CollectionMap;
import jp.ats.blendee.jdbc.BContext;
import jp.ats.blendee.jdbc.BlendeeManager;
import jp.ats.blendee.jdbc.ManagementSubject;
import jp.ats.blendee.jdbc.MetadataUtilities;
import jp.ats.blendee.jdbc.ResourceLocator;

/**
 * {@link Relationship} の生成、管理を行うファクトリクラスです。
 *
 * @author 千葉 哲嗣
 */
public class RelationshipFactory implements ManagementSubject {

	private final Object lock = new Object();

	private final Map<ResourceLocator, Relationship> relationshipCache = new HashMap<>();

	private final Map<ResourceLocator, String> locationIdMap = new HashMap<>();

	/**
	 * このクラスのコンストラクタです。
	 * <br>
	 * {@link BContext} 管理対象です。
	 *
	 * @see BContext#get(Class)
	 */
	public RelationshipFactory() {}

	/**
	 * {@link ResourceLocator} が表すテーブルをルートとするテーブルツリーを作成します。
	 *
	 * @param locator ルートとなるテーブル
	 * @return ツリーのルート要素
	 */
	public Relationship getInstance(ResourceLocator locator) {
		synchronized (lock) {
			if (locationIdMap.size() == 0) prepareLocationIdMap();
			Relationship relationship = relationshipCache.get(locator);
			if (relationship == null) {
				relationship = createRelationship(locator);
				relationshipCache.put(locator, relationship);
			}

			return relationship;
		}
	}

	/**
	 * target を、 root のツリーに含まれる {@link Relationship} に変換します。
	 *
	 * @param root 含まれるべきツリーのルート
	 * @param target 対象
	 * @return 変換された {@link Relationship}
	 * @throws IllegalStateException 結合できないテーブルを使用している場合
	 * @throws IllegalStateException ツリー内に同一テーブルが複数あるため、あいまいな指定がされている場合
	 */
	public static Relationship convert(Relationship root, ResourceLocator target) {
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
			locationIdMap.clear();
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

	private void prepareLocationIdMap() {
		String[] schemaNames = BContext.get(BlendeeManager.class).getConfigure().getSchemaNames();
		int counter = 0;
		for (String name : schemaNames) {
			ResourceLocator[] locators = MetadataUtilities.getTables(name);
			counter += locators.length;
		}

		DecimalFormat format = createDigitFormat(counter);
		counter = 0;
		for (String name : schemaNames) {
			ResourceLocator[] locators = MetadataUtilities.getTables(name);
			for (ResourceLocator locator : locators) {
				locationIdMap.put(locator, "t" + format.format(counter));
				counter++;
			}
		}
	}

	private Relationship createRelationship(final ResourceLocator locator) {
		final String locationId = locationIdMap.get(locator);
		if (locationId == null) throw new IllegalArgumentException(locator + " は使用できるテーブルに含まれていません");
		return new Relationship(
			null,
			null,
			null,
			locator,
			locationId,
			BContext.get(BlendeeManager.class).getConfigure().getDataTypeConverter(),
			new CollectionMap<ResourceLocator, Relationship>());
	}
}
