package org.blendee.jdbc;

import java.sql.DatabaseMetaData;

/**
 * テーブルの定義情報をもつクラスです。
 *
 * @author 千葉 哲嗣
 */
public interface TableMetadata {

	/**
	 * {@link DatabaseMetaData#getTables(String, String, String, String[])} TABLE_SCHEM を参照のこと。
	 *
	 * @return このテーブルのスキーマ名
	 */
	String getSchemaName();

	/**
	 * {@link DatabaseMetaData#getTables(String, String, String, String[])} TABLE_NAME を参照のこと。
	 *
	 * @return テーブル名
	 */
	String getName();

	/**
	 * {@link DatabaseMetaData#getTables(String, String, String, String[])} TABLE_TYPE を参照のこと。
	 *
	 * @return このテーブルの型
	 */
	String getType();

	/**
	 * {@link DatabaseMetaData#getTables(String, String, String, String[])} REMARKS を参照のこと。
	 *
	 * @return 表に関する説明
	 */
	String getRemarks();
}
