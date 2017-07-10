package org.blendee.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * {@link ResultSet} に似せ、一行に対する操作のみに機能を制限したインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface BResult {

	/**
	 * 指定されたカラムの値を boolean として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	boolean getBoolean(String columnName);

	/**
	 * 指定されたカラムの値を double として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	double getDouble(String columnName);

	/**
	 * 指定されたカラムの値を float として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	float getFloat(String columnName);

	/**
	 * 指定されたカラムの値を int として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	int getInt(String columnName);

	/**
	 * 指定されたカラムの値を long として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	long getLong(String columnName);

	/**
	 * 指定されたカラムの値を {@link String} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	String getString(String columnName);

	/**
	 * 指定されたカラムの値を {@link Timestamp} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	Timestamp getTimestamp(String columnName);

	/**
	 * 指定されたカラムの値を {@link BigDecimal} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	BigDecimal getBigDecimal(String columnName);

	/**
	 * 指定されたカラムの値を {@link Object} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	Object getObject(String columnName);

	/**
	 * 指定されたカラムの値を {@link InputStream} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	InputStream getBinaryStream(String columnName);

	/**
	 * 指定されたカラムの値を {@link Reader} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	Reader getCharacterStream(String columnName);

	/**
	 * 指定されたカラムの値を byte の配列として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	byte[] getBytes(String columnName);

	/**
	 * 指定されたカラムの値を {@link Blob} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	Blob getBlob(String columnName);

	/**
	 * 指定されたカラムの値を {@link Clob} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	Clob getClob(String columnName);

	/**
	 * 指定されたカラムの値を boolean として返します。
	 * @param columnIndex カラム位置
	 * @return カラムの値
	 */
	boolean getBoolean(int columnIndex);

	/**
	 * 指定されたカラムの値を double として返します。
	 * @param columnIndex カラム位置
	 * @return カラムの値
	 */
	double getDouble(int columnIndex);

	/**
	 * 指定されたカラムの値を float として返します。
	 * @param columnIndex カラム位置
	 * @return カラムの値
	 */
	float getFloat(int columnIndex);

	/**
	 * 指定されたカラムの値を int として返します。
	 * @param columnIndex カラム位置
	 * @return カラムの値
	 */
	int getInt(int columnIndex);

	/**
	 * 指定されたカラムの値を long として返します。
	 * @param columnIndex カラム位置
	 * @return カラムの値
	 */
	long getLong(int columnIndex);

	/**
	 * 指定されたカラムの値を {@link String} として返します。
	 * @param columnIndex カラム位置
	 * @return カラムの値
	 */
	String getString(int columnIndex);

	/**
	 * 指定されたカラムの値を {@link Timestamp} として返します。
	 * @param columnIndex カラム位置
	 * @return カラムの値
	 */
	Timestamp getTimestamp(int columnIndex);

	/**
	 * 指定されたカラムの値を {@link BigDecimal} として返します。
	 * @param columnIndex カラム位置
	 * @return カラムの値
	 */
	BigDecimal getBigDecimal(int columnIndex);

	/**
	 * 指定されたカラムの値を {@link Object} として返します。
	 * @param columnIndex カラム位置
	 * @return カラムの値
	 */
	Object getObject(int columnIndex);

	/**
	 * 指定されたカラムの値を {@link InputStream} として返します。
	 * @param columnIndex カラム位置
	 * @return カラムの値
	 */
	InputStream getBinaryStream(int columnIndex);

	/**
	 * 指定されたカラムの値を {@link Reader} として返します。
	 * @param columnIndex カラム位置
	 * @return カラムの値
	 */
	Reader getCharacterStream(int columnIndex);

	/**
	 * 指定されたカラムの値を byte の配列として返します。
	 * @param columnIndex カラム位置
	 * @return カラムの値
	 */
	byte[] getBytes(int columnIndex);

	/**
	 * 指定されたカラムの値を {@link Blob} として返します。
	 * @param columnIndex カラム位置
	 * @return カラムの値
	 */
	Blob getBlob(int columnIndex);

	/**
	 * 指定されたカラムの値を {@link Clob} として返します。
	 * @param columnIndex カラム位置
	 * @return カラムの値
	 */
	Clob getClob(int columnIndex);

	/**
	 * 直前に取得したカラムの値が NULL かどうか検査します。
	 * @return 直前に取得したカラムが NULL か
	 */
	boolean wasNull();

	/**
	 * この検索結果の列数を返します。
	 * @return 列数
	 */
	int getColumnCount();

	/**
	 * 指定されたカラムの名称を返します。
	 * @param columnIndex カラム位置
	 * @return カラムの名称
	 */
	String getColumnName(int columnIndex);

	/**
	 * 指定されたカラムの値が SQL のどの型に該当するか返します。
	 * @param columnIndex カラム位置
	 * @return カラムの値の型
	 * @see java.sql.Types
	 */
	int getColumnType(int columnIndex);

	/**
	 * 指定されたカラムのデータベース固有の型名を取得します。 
	 * @param columnIndex カラム位置
	 * @return データベースが使用する型名
	 */
	String getColumnTypeName(int columnIndex);
}
