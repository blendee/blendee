package jp.ats.blendee.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

/**
 * {@link PreparedStatement} に似せ、機能を制限したインターフェイスです。
 *
 * @author 千葉 哲嗣
 */
public interface BPreparedStatement extends BStatement {

	/**
	 * この文のプレースホルダに boolean 値をセットします。
	 *
	 * @param parameterIndex プレースホルダの位置
	 * @param x 値
	 */
	void setBoolean(int parameterIndex, boolean x);

	/**
	 * この文のプレースホルダに double 値をセットします。
	 *
	 * @param parameterIndex プレースホルダの位置
	 * @param x 値
	 */
	void setDouble(int parameterIndex, double x);

	/**
	 * この文のプレースホルダに float 値をセットします。
	 *
	 * @param parameterIndex プレースホルダの位置
	 * @param x 値
	 */
	void setFloat(int parameterIndex, float x);

	/**
	 * この文のプレースホルダに int 値をセットします。
	 *
	 * @param parameterIndex プレースホルダの位置
	 * @param x 値
	 */
	void setInt(int parameterIndex, int x);

	/**
	 * この文のプレースホルダに long 値をセットします。
	 *
	 * @param parameterIndex プレースホルダの位置
	 * @param x 値
	 */
	void setLong(int parameterIndex, long x);

	/**
	 * この文のプレースホルダに {@link String} 値をセットします。
	 *
	 * @param parameterIndex プレースホルダの位置
	 * @param x 値
	 */
	void setString(int parameterIndex, String x);

	/**
	 * この文のプレースホルダに {@link Timestamp} 値をセットします。
	 *
	 * @param parameterIndex プレースホルダの位置
	 * @param x 値
	 */
	void setTimestamp(int parameterIndex, Timestamp x);

	/**
	 * この文のプレースホルダに {@link BigDecimal} 値をセットします。
	 *
	 * @param parameterIndex プレースホルダの位置
	 * @param x 値
	 */
	void setBigDecimal(int parameterIndex, BigDecimal x);

	/**
	 * この文のプレースホルダに {@link Object} 値をセットします。
	 *
	 * @param parameterIndex プレースホルダの位置
	 * @param x 値
	 */
	void setObject(int parameterIndex, Object x);

	/**
	 * この文のプレースホルダに {@link InputStream} から読み込んだ値をセットします。
	 *
	 * @param parameterIndex プレースホルダの位置
	 * @param stream 読み込むストリーム
	 * @param length 読み込むデータのサイズ
	 */
	void setBinaryStream(int parameterIndex, InputStream stream, int length);

	/**
	 * この文のプレースホルダに {@link Reader} から読み込んだ値をセットします。
	 *
	 * @param parameterIndex プレースホルダの位置
	 * @param reader 読み込むストリーム
	 * @param length 読み込むデータのサイズ
	 */
	void setCharacterStream(int parameterIndex, Reader reader, int length);

	/**
	 * この文のプレースホルダに byte の配列をセットします。
	 *
	 * @param parameterIndex プレースホルダの位置
	 * @param x 値
	 */
	void setBytes(int parameterIndex, byte[] x);

	/**
	 * この文のプレースホルダに {@link Blob} 値をセットします。
	 *
	 * @param parameterIndex プレースホルダの位置
	 * @param x 値
	 */
	void setBlob(int parameterIndex, Blob x);

	/**
	 * この文のプレースホルダに {@link Clob} 値をセットします。
	 *
	 * @param parameterIndex プレースホルダの位置
	 * @param x 値
	 */
	void setClob(int parameterIndex, Clob x);

	/**
	 * この文のプレースホルダに NULL をセットします。
	 *
	 * @param parameterIndex プレースホルダの位置
	 * @param type 対象カラムの型
	 * @see java.sql.Types
	 */
	void setNull(int parameterIndex, int type);
}
