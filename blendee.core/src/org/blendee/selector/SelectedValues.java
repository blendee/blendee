package org.blendee.selector;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.UUID;

import org.blendee.jdbc.Result;
import org.blendee.sql.Binder;
import org.blendee.sql.Column;

/**
 * 検索結果をラップし、 {@link Column} で値を取得可能にする機能を定めたインターフェイスです。
 * @author 千葉 哲嗣
 * @see SelectedValuesIterator#next()
 */
public interface SelectedValues {

	/**
	 * 指定されたカラムの値を boolean として返します。
	 * @param column {@link Column} インスタンス
	 * @return カラムの値
	 * @throws IllegalValueException 検索結果にカラムが存在しない場合
	 */
	boolean getBoolean(Column column);

	/**
	 * 指定されたカラムの値を double として返します。
	 * @param column {@link Column} インスタンス
	 * @return カラムの値
	 * @throws IllegalValueException 検索結果にカラムが存在しない場合
	 */
	double getDouble(Column column);

	/**
	 * 指定されたカラムの値を float として返します。
	 * @param column {@link Column} インスタンス
	 * @return カラムの値
	 * @throws IllegalValueException 検索結果にカラムが存在しない場合
	 */
	float getFloat(Column column);

	/**
	 * 指定されたカラムの値を int として返します。
	 * @param column {@link Column} インスタンス
	 * @return カラムの値
	 * @throws IllegalValueException 検索結果にカラムが存在しない場合
	 */
	int getInt(Column column);

	/**
	 * 指定されたカラムの値を long として返します。
	 * @param column {@link Column} インスタンス
	 * @return カラムの値
	 * @throws IllegalValueException 検索結果にカラムが存在しない場合
	 */
	long getLong(Column column);

	/**
	 * 指定されたカラムの値を {@link String} として返します。
	 * @param column {@link Column} インスタンス
	 * @return カラムの値
	 * @throws IllegalValueException 検索結果にカラムが存在しない場合
	 */
	String getString(Column column);

	/**
	 * 指定されたカラムの値を {@link Timestamp} として返します。
	 * @param column {@link Column} インスタンス
	 * @return カラムの値
	 * @throws IllegalValueException 検索結果にカラムが存在しない場合
	 */
	Timestamp getTimestamp(Column column);

	/**
	 * 指定されたカラムの値を {@link BigDecimal} として返します。
	 * @param column {@link Column} インスタンス
	 * @return カラムの値
	 * @throws IllegalValueException 検索結果にカラムが存在しない場合
	 */
	BigDecimal getBigDecimal(Column column);

	/**
	 * 指定されたカラムの値を {@link UUID} として返します。
	 * @param column {@link Column} インスタンス
	 * @return カラムの値
	 * @throws IllegalValueException 検索結果にカラムが存在しない場合
	 */
	UUID getUUID(Column column);

	/**
	 * 指定されたカラムの値を {@link Object} として返します。
	 * @param column {@link Column} インスタンス
	 * @return カラムの値
	 * @throws IllegalValueException 検索結果にカラムが存在しない場合
	 */
	Object getObject(Column column);

	/**
	 * 指定されたカラムの値を byte の配列として返します。
	 * @param column {@link Column} インスタンス
	 * @return カラムの値
	 * @throws IllegalValueException 検索結果にカラムが存在しない場合
	 */
	byte[] getBytes(Column column);

	/**
	 * 指定されたカラムの値を {@link Blob} として返します。
	 * @param column {@link Column} インスタンス
	 * @return カラムの値
	 * @throws IllegalValueException 検索結果にカラムが存在しない場合
	 */
	Blob getBlob(Column column);

	/**
	 * 指定されたカラムの値を {@link Clob} として返します。
	 * @param column {@link Column} インスタンス
	 * @return カラムの値
	 * @throws IllegalValueException 検索結果にカラムが存在しない場合
	 */
	Clob getClob(Column column);

	/**
	 * 指定されたカラムの値を {@link Binder} として返します。
	 * @param column {@link Column} インスタンス
	 * @return カラムの値
	 * @throws IllegalValueException 検索結果にカラムが存在しない場合
	 */
	Binder getBinder(Column column);

	/**
	 * 指定されたカラムの値が NULL かどうか検査します。
	 * @param column {@link Column} インスタンス
	 * @return NULL の場合、 true
	 * @throws IllegalValueException 検索結果にカラムが存在しない場合
	 */
	boolean isNull(Column column);

	/**
	 * 検索に使用したカラムを返します。
	 * @return 検索に使用したカラム
	 */
	Column[] getSelectedColumns();

	/**
	 * 検索結果オブジェクトを返します。
	 * @return 検索結果
	 */
	Result getResult();

	/**
	 * 引数のカラムが今回の検索に使用されたかを検査します。
	 * @param column 検査対象
	 * @return SELECT されたかどうか
	 */
	boolean isSelected(Column column);
}
