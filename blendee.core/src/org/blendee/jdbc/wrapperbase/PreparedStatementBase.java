package org.blendee.jdbc.wrapperbase;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;

import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.jdbc.BlenResultSet;

/**
 * {@link BlenPreparedStatement} のラッパーを実装するベースとなる、抽象基底クラスです。
 * @author 千葉 哲嗣
 */
public abstract class PreparedStatementBase implements BlenPreparedStatement {

	private final BlenPreparedStatement base;

	/**
	 * ラップするインスタンスを受け取るコンストラクタです。
	 * @param base ベースとなるインスタンス
	 */
	protected PreparedStatementBase(BlenPreparedStatement base) {
		this.base = base;
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) {
		base.setBoolean(parameterIndex, x);
	}

	@Override
	public void setDouble(int parameterIndex, double x) {
		base.setDouble(parameterIndex, x);
	}

	@Override
	public void setFloat(int parameterIndex, float x) {
		base.setFloat(parameterIndex, x);
	}

	@Override
	public void setInt(int parameterIndex, int x) {
		base.setInt(parameterIndex, x);
	}

	@Override
	public void setLong(int parameterIndex, long x) {
		base.setLong(parameterIndex, x);
	}

	@Override
	public void setString(int parameterIndex, String x) {
		base.setString(parameterIndex, x);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x) {
		base.setTimestamp(parameterIndex, x);
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x) {
		base.setBigDecimal(parameterIndex, x);
	}

	@Override
	public void setObject(int parameterIndex, Object x) {
		base.setObject(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream stream, int length) {
		base.setBinaryStream(parameterIndex, stream, length);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) {
		base.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) {
		base.setBytes(parameterIndex, x);
	}

	@Override
	public void setBlob(int parameterIndex, Blob blob) {
		base.setBlob(parameterIndex, blob);
	}

	@Override
	public void setClob(int parameterIndex, Clob clob) {
		base.setClob(parameterIndex, clob);
	}

	@Override
	public void setNull(int parameterIndex, int type) {
		base.setNull(parameterIndex, type);
	}

	@Override
	public BlenResultSet executeQuery() {
		return base.executeQuery();
	}

	@Override
	public int executeUpdate() {
		return base.executeUpdate();
	}

	@Override
	public boolean execute() {
		return base.execute();
	}

	@Override
	public BlenResultSet getResultSet() {
		return base.getResultSet();
	}

	@Override
	public int getUpdateCount() {
		return base.getUpdateCount();
	}

	@Override
	public boolean getMoreResults() {
		return base.getMoreResults();
	}

	@Override
	public void close() {
		base.close();
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
