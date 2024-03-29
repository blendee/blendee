package org.blendee.jdbc;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * メタデータに関するユーティリティクラスです。
 * @author 千葉 哲嗣
 */
public class MetadataUtilities {

	private MetadataUtilities() {
	}

	/**
	 * パラメータのスキーマに存在する全てのテーブルを返します。
	 * @param schemaName 対象となるスキーマ
	 * @return スキーマに存在する全てのテーブル
	 */
	public static TablePath[] getTables(final String schemaName) {
		return BlendeeManager.get().getMetadata().getTables(schemaName);
	}

	/**
	 * パラメータのテーブルに存在する全てのカラムを返します。
	 * @param path 対象となるテーブル
	 * @return テーブルに存在する全てのカラム
	 */
	public static ColumnMetadata[] getColumnMetadatas(final TablePath path) {
		return BlendeeManager.get().getMetadata().getColumnMetadatas(path);
	}

	/**
	 * パラメータのテーブルに存在する全てのカラム名を返します。
	 * @param path 対象となるテーブル
	 * @return テーブルに存在する全てのカラム
	 */
	public static String[] getColumnNames(final TablePath path) {
		var metadatas = getColumnMetadatas(path);
		var names = new String[metadatas.length];
		for (var i = 0; i < metadatas.length; i++) {
			names[i] = metadatas[i].getName();
		}

		return names;
	}

	/**
	 * パラメータのテーブルの主キーを構成するカラムを返します。
	 * @param path 対象となるテーブル
	 * @return 主キーを構成するカラム
	 */
	public static String[] getPrimaryKeyColumnNames(final TablePath path) {
		return BlendeeManager.get().getMetadata().getPrimaryKeyMetadata(path).getColumnNames();
	}

	/**
	 * パラメータのテーブルの主キーの名称を返します。
	 * @param path 対象となるテーブル
	 * @return 主キー名
	 */
	public static String getPrimaryKeyName(TablePath path) {
		return BlendeeManager.get().getMetadata().getPrimaryKeyMetadata(path).getName();
	}

	/**
	 * パラメータのテーブルが参照しているテーブルを返します。
	 * @param path 対象となるテーブル
	 * @return 参照しているテーブル
	 */
	public static TablePath[] getResourcesOfImportedKey(TablePath path) {
		return BlendeeManager.get().getMetadata().getResourcesOfImportedKey(path);
	}

	/**
	 * パラメータのテーブルを参照しているテーブルを返します。
	 * @param path 対象となるテーブル
	 * @return 参照されているテーブル
	 */
	public static TablePath[] getResourcesOfExportedKey(TablePath path) {
		return BlendeeManager.get().getMetadata().getResourcesOfExportedKey(path);
	}

	/**
	 * 二つのテーブル間の参照関係を返します。
	 * @param exported 主キー側テーブル
	 * @param imported 外部キー側テーブル
	 * @return テーブル間の関係情報
	 */
	public static CrossReference[] getCrossReferences(TablePath exported, TablePath imported) {
		return BlendeeManager.get().getMetadata().getCrossReferences(exported, imported);
	}

	/**
	 * パラメータで指定された名称を、データベースで使用される標準的な名前に変換します。
	 * @param name 標準化前の名称
	 * @return 標準化された名称
	 */
	public static String regularize(String name) {
		return BlendeeManager.get().getMetadata().getStoredIdentifier().regularize(name);
	}

	/**
	 * パラメータで指定された名称を、データベースで使用される標準的な名前に変換します。
	 * @param names 標準化前の名称の配列
	 * @return 標準化された名称の配列
	 */
	public static String[] regularize(String[] names) {
		var regularized = new String[names.length];;

		var identifier = BlendeeManager.get().getMetadata().getStoredIdentifier();
		for (int i = 0; i < names.length; i++) {
			regularized[i] = identifier.regularize(names[i]);
		}

		return regularized;
	}

	/**
	 * パラメータのテーブルとそれが参照しているテーブル間の参照関係を返します。
	 * @param foreignKeyTable 対象となるテーブル
	 * @return テーブル間の関係情報
	 */
	public static CrossReference[] getCrossReferencesOfImportedKeys(TablePath foreignKeyTable) {
		var targetTables = getResourcesOfImportedKey(foreignKeyTable);
		var references = new LinkedList<CrossReference>();
		for (var path : targetTables)
			references.addAll(Arrays.asList(getCrossReferences(path, foreignKeyTable)));

		return references.toArray(new CrossReference[references.size()]);
	}

	/**
	 * パラメータのテーブルとそれを参照しているテーブル間の参照関係を返します。
	 * @param primaryKeyTable 対象となるテーブル
	 * @return テーブル間の関係情報
	 */
	public static CrossReference[] getCrossReferencesOfExportedKeys(TablePath primaryKeyTable) {
		var targetTables = getResourcesOfExportedKey(primaryKeyTable);
		var references = new LinkedList<CrossReference>();
		for (var path : targetTables)
			references.addAll(Arrays.asList(getCrossReferences(primaryKeyTable, path)));

		return references.toArray(new CrossReference[references.size()]);
	}
}
