package org.blendee.jdbc;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * メタデータに関するユーティリティクラスです。
 *
 * @author 千葉 哲嗣
 */
public class MetadataUtilities {

	private MetadataUtilities() {}

	/**
	 * パラメータのスキーマに存在する全てのテーブルを返します。
	 *
	 * @param schemaName 対象となるスキーマ
	 * @return スキーマに存在する全てのテーブル
	 */
	public static ResourceLocator[] getTables(final String schemaName) {
		return BlendeeContext.get(BlendeeManager.class).getConnection().getTables(schemaName);
	}

	/**
	 * パラメータのテーブルに存在する全てのカラムを返します。
	 *
	 * @param locator 対象となるテーブル
	 * @return テーブルに存在する全てのカラム
	 */
	public static ColumnMetadata[] getColumnMetadatas(final ResourceLocator locator) {
		return BlendeeContext.get(BlendeeManager.class).getConnection().getColumnMetadatas(locator);
	}

	/**
	 * パラメータのテーブルに存在する全てのカラム名を返します。
	 *
	 * @param locator 対象となるテーブル
	 * @return テーブルに存在する全てのカラム
	 */
	public static String[] getColumnNames(final ResourceLocator locator) {
		ColumnMetadata[] metadatas = getColumnMetadatas(locator);
		String[] names = new String[metadatas.length];
		for (int i = 0; i < metadatas.length; i++) {
			names[i] = metadatas[i].getName();
		}

		return names;
	}

	/**
	 * パラメータのテーブルの主キーを構成するカラムを返します。
	 *
	 * @param locator 対象となるテーブル
	 * @return 主キーを構成するカラム
	 */
	public static String[] getPrimaryKeyColumnNames(final ResourceLocator locator) {
		return BlendeeContext.get(BlendeeManager.class).getConnection().getPrimaryKeyMetadata(locator).getColumnNames();
	}

	/**
	 * パラメータのテーブルの主キーの名称を返します。
	 *
	 * @param locator 対象となるテーブル
	 * @return 主キー名
	 */
	public static String getPrimaryKeyName(ResourceLocator locator) {
		return BlendeeContext.get(BlendeeManager.class).getConnection().getPrimaryKeyMetadata(locator).getName();
	}

	/**
	 * パラメータのテーブルが参照しているテーブルを返します。
	 *
	 * @param locator 対象となるテーブル
	 * @return 参照しているテーブル
	 */
	public static ResourceLocator[] getResourcesOfImportedKey(ResourceLocator locator) {
		return BlendeeContext.get(BlendeeManager.class).getConnection().getResourcesOfImportedKey(locator);
	}

	/**
	 * パラメータのテーブルを参照しているテーブルを返します。
	 *
	 * @param locator 対象となるテーブル
	 * @return 参照されているテーブル
	 */
	public static ResourceLocator[] getResourcesOfExportedKey(ResourceLocator locator) {
		return BlendeeContext.get(BlendeeManager.class).getConnection().getResourcesOfExportedKey(locator);
	}

	/**
	 * 二つのテーブル間の参照関係を返します。
	 *
	 * @param exported 主キー側テーブル
	 * @param imported 外部キー側テーブル
	 * @return テーブル間の関係情報
	 */
	public static CrossReference[] getCrossReferences(ResourceLocator exported, ResourceLocator imported) {
		return BlendeeContext.get(BlendeeManager.class).getConnection().getCrossReferences(exported, imported);
	}

	/**
	 * パラメータで指定された名称を、データベースで使用される標準的な名前に変換します。
	 *
	 * @param name 標準化前の名称
	 * @return 標準化された名称
	 */
	public static String regularize(String name) {
		return BlendeeContext.get(BlendeeManager.class).getConnection().regularize(name);
	}

	/**
	 * パラメータで指定された名称を、データベースで使用される標準的な名前に変換します。
	 *
	 * @param names 標準化前の名称の配列
	 * @return 標準化された名称の配列
	 */
	public static String[] regularize(String[] names) {
		String[] regularized = new String[names.length];;

		BConnection connection = BlendeeContext.get(BlendeeManager.class).getConnection();
		for (int i = 0; i < names.length; i++) {
			regularized[i] = connection.regularize(names[i]);
		}

		return regularized;
	}

	/**
	 * パラメータのテーブルとそれが参照しているテーブル間の参照関係を返します。
	 *
	 * @param foreignKeyTable 対象となるテーブル
	 * @return テーブル間の関係情報
	 */
	public static CrossReference[] getCrossReferencesOfImportedKeys(ResourceLocator foreignKeyTable) {
		ResourceLocator[] targetTables = getResourcesOfImportedKey(foreignKeyTable);
		List<CrossReference> references = new LinkedList<>();
		for (ResourceLocator locator : targetTables)
			references.addAll(Arrays.asList(getCrossReferences(locator, foreignKeyTable)));

		return references.toArray(new CrossReference[references.size()]);
	}

	/**
	 * パラメータのテーブルとそれを参照しているテーブル間の参照関係を返します。
	 *
	 * @param primaryKeyTable 対象となるテーブル
	 * @return テーブル間の関係情報
	 */
	public static CrossReference[] getCrossReferencesOfExportedKeys(ResourceLocator primaryKeyTable) {
		ResourceLocator[] targetTables = getResourcesOfExportedKey(primaryKeyTable);
		List<CrossReference> references = new LinkedList<>();
		for (ResourceLocator locator : targetTables)
			references.addAll(Arrays.asList(getCrossReferences(primaryKeyTable, locator)));

		return references.toArray(new CrossReference[references.size()]);
	}
}
