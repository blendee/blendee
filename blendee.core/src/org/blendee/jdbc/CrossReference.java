package org.blendee.jdbc;

/**
 * テーブル間の関係情報を表します。
 * @author 千葉 哲嗣
 * @see Metadata#getCrossReferences(TablePath, TablePath)
 */
public interface CrossReference {

	/**
	 * 主キー側テーブルのもつ主キー名を返します。
	 * @return 主キー名
	 */
	String getPrimaryKeyName();

	/**
	 * 外部キー側テーブルのもつ外部キー名を返します。
	 * @return 外部キー名
	 */
	String getForeignKeyName();

	/**
	 * 主キー側テーブル名を返します。
	 * @return 主キー側のテーブル名
	 */
	TablePath getPrimaryKeyTable();

	/**
	 * 外部キー側テーブル名を返します。
	 * @return 外部キー側のテーブル名
	 */
	TablePath getForeignKeyTable();

	/**
	 * 主キーを構成するカラム名を返します。
	 * @return 主キーを構成するカラム名
	 */
	String[] getPrimaryKeyColumnNames();

	/**
	 * 外部キーを構成するカラム名を返します。
	 * @return 外部キーを構成するカラム名
	 */
	String[] getForeignKeyColumnNames();

	/**
	 * 主キーのメンバであるカラム名を、対応する外部キーのメンバに変換します。
	 * @param primaryKeyColumnName 主キーのメンバであるカラム名
	 * @return 対応する外部キーのメンバ
	 */
	String convertToForeignKeyColumnName(String primaryKeyColumnName);

	/**
	 * 外部キーのメンバであるカラム名を、対応する主キーのメンバに変換します。
	 * @param foreignKeyColumnName 外部キーのメンバであるカラム名
	 * @return 対応する主キーのメンバ
	 */
	String convertToPrimaryKeyColumnName(String foreignKeyColumnName);

	/**
	 * この外部キーが、実際のデータベースで定義されたものではなく、擬似的に追加されたものであるかどうかを返します。
	 * @return 疑似外部キーがどうか
	 */
	boolean isPseudo();
}
