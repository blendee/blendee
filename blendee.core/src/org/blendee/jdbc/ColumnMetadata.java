package org.blendee.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.Types;

/**
 * カラムの定義情報をもつクラスです。
 * @author 千葉 哲嗣
 */
public interface ColumnMetadata {

	/**
	 * {@link DatabaseMetaData#getColumns(String, String, String, String)} TABLE_SCHEM を参照のこと。
	 * @return このカラムのスキーマ名
	 */
	String getSchemaName();

	/**
	 * {@link DatabaseMetaData#getColumns(String, String, String, String)} TABLE_NAME を参照のこと。
	 * @return このカラムのテーブル名
	 */
	String getTableName();

	/**
	 * {@link DatabaseMetaData#getColumns(String, String, String, String)} COLUMN_NAME を参照のこと。
	 * @return このカラムの名称
	 */
	String getName();

	/**
	 * {@link DatabaseMetaData#getColumns(String, String, String, String)} DATA_TYPE を参照のこと。<br>
	 * {@link Types} を参照のこと。
	 * @return このカラムの型
	 */
	int getType();

	/**
	 * {@link DatabaseMetaData#getColumns(String, String, String, String)} TYPE_NAME を参照のこと。
	 * @return このカラムのデータソース依存の型名
	 */
	String getTypeName();

	/**
	 * {@link DatabaseMetaData#getColumns(String, String, String, String)} COLUMN_SIZE を参照のこと。
	 * @return カラムサイズ
	 */
	int getSize();

	/**
	 * 小数点以下の桁数を持つかどうかを返します。
	 * {@link DatabaseMetaData#getColumns(String, String, String, String)} DECIMAL_DIGITS を参照のこと。
	 * @return 小数点以下の桁数を持つ場合、 true
	 */
	boolean hasDecimalDigits();

	/**
	 * {@link DatabaseMetaData#getColumns(String, String, String, String)} DECIMAL_DIGITS を参照のこと。
	 * @return 小数点以下の桁数
	 */
	int getDecimalDigits();

	/**
	 * {@link DatabaseMetaData#getColumns(String, String, String, String)} REMARKS を参照のこと。
	 * @return コメント記述列
	 */
	String getRemarks();

	/**
	 * {@link DatabaseMetaData#getColumns(String, String, String, String)} COLUMN_DEF を参照のこと。
	 * @return デフォルト値
	 */
	String getDefaultValue();

	/**
	 * {@link DatabaseMetaData#getColumns(String, String, String, String)} ORDINAL_POSITION を参照のこと。
	 * @return テーブル内の位置 (1 から始まる )
	 */
	int getOrdinalPosition();

	/**
	 * このカラムが、 NULL 値を許可しない場合、 true を返します。
	 * NULL 値を許可する、または NULL 値を許可するかどうか不明である場合は false を返します。
	 * {@link DatabaseMetaData#getColumns(String, String, String, String)} IS_NULLABLE を参照のこと。
	 * @return NULL 値を許可するかしないか
	 */
	boolean isNotNull();
}
