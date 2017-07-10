package org.blendee.jdbc.wrapperbase;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;

import org.blendee.jdbc.BResult;

/**
 * {@link BResult} のラッパーを実装するベースとなる、抽象基底クラスです。
 * @author 千葉 哲嗣
 */
public abstract class ResultBase implements BResult {

	private final BResult base;

	/**
	 * ラップするインスタンスを受け取るコンストラクタです。
	 * @param base ベースとなるインスタンス
	 */
	protected ResultBase(BResult base) {
		this.base = base;
	}

	@Override
	public boolean getBoolean(String columnName) {
		return base.getBoolean(columnName);
	}

	@Override
	public double getDouble(String columnName) {
		return base.getDouble(columnName);
	}

	@Override
	public float getFloat(String columnName) {
		return base.getFloat(columnName);
	}

	@Override
	public int getInt(String columnName) {
		return base.getInt(columnName);
	}

	@Override
	public long getLong(String columnName) {
		return base.getLong(columnName);
	}

	@Override
	public String getString(String columnName) {
		return base.getString(columnName);
	}

	@Override
	public Timestamp getTimestamp(String columnName) {
		return base.getTimestamp(columnName);
	}

	@Override
	public BigDecimal getBigDecimal(String columnName) {
		return base.getBigDecimal(columnName);
	}

	@Override
	public Object getObject(String columnName) {
		return base.getObject(columnName);
	}

	@Override
	public InputStream getBinaryStream(String columnName) {
		return base.getBinaryStream(columnName);
	}

	@Override
	public Reader getCharacterStream(String columnName) {
		return base.getCharacterStream(columnName);
	}

	@Override
	public byte[] getBytes(String columnName) {
		return base.getBytes(columnName);
	}

	@Override
	public Blob getBlob(String columnName) {
		return base.getBlob(columnName);
	}

	@Override
	public Clob getClob(String columnName) {
		return base.getClob(columnName);
	}

	@Override
	public boolean getBoolean(int columnIndex) {
		return base.getBoolean(columnIndex);
	}

	@Override
	public double getDouble(int columnIndex) {
		return base.getDouble(columnIndex);
	}

	@Override
	public float getFloat(int columnIndex) {
		return base.getFloat(columnIndex);
	}

	@Override
	public int getInt(int columnIndex) {
		return base.getInt(columnIndex);
	}

	@Override
	public long getLong(int columnIndex) {
		return base.getLong(columnIndex);
	}

	@Override
	public String getString(int columnIndex) {
		return base.getString(columnIndex);
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) {
		return base.getTimestamp(columnIndex);
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) {
		return base.getBigDecimal(columnIndex);
	}

	@Override
	public Object getObject(int columnIndex) {
		return base.getObject(columnIndex);
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) {
		return base.getBinaryStream(columnIndex);
	}

	@Override
	public Reader getCharacterStream(int columnIndex) {
		return base.getCharacterStream(columnIndex);
	}

	@Override
	public byte[] getBytes(int columnIndex) {
		return base.getBytes(columnIndex);
	}

	@Override
	public Blob getBlob(int columnIndex) {
		return base.getBlob(columnIndex);
	}

	@Override
	public Clob getClob(int columnIndex) {
		return base.getClob(columnIndex);
	}

	@Override
	public boolean wasNull() {
		return base.wasNull();
	}

	@Override
	public int getColumnCount() {
		return base.getColumnCount();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return base.getColumnName(columnIndex);
	}

	@Override
	public int getColumnType(int columnIndex) {
		return base.getColumnType(columnIndex);
	}

	@Override
	public String getColumnTypeName(int columnIndex) {
		return base.getColumnTypeName(columnIndex);
	}

	@Override
	public String toString() {
		return base.toString();
	}

	@Override
	public boolean equals(Object o) {
		return base.equals(o);
	}

	@Override
	public int hashCode() {
		return base.hashCode();
	}
}
