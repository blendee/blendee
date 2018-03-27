package org.blendee.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;

import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.jdbc.wrapperbase.PreparedStatementBase;

class CountingPreparedStatement extends PreparedStatementBase {

	private int complemented = 0;

	CountingPreparedStatement(BlenPreparedStatement base) {
		super(base);
	}

	/**
	 * 現時点でプレースホルダにセットされた数を返します。
	 * @return プレースホルダにセットされた数
	 */
	int getComplementedCount() {
		return complemented;
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) {
		complemented++;
		super.setBoolean(parameterIndex, x);
	}

	@Override
	public void setDouble(int parameterIndex, double x) {
		complemented++;
		super.setDouble(parameterIndex, x);
	}

	@Override
	public void setFloat(int parameterIndex, float x) {
		complemented++;
		super.setFloat(parameterIndex, x);
	}

	@Override
	public void setInt(int parameterIndex, int x) {
		complemented++;
		super.setInt(parameterIndex, x);
	}

	@Override
	public void setLong(int parameterIndex, long x) {
		complemented++;
		super.setLong(parameterIndex, x);
	}

	@Override
	public void setString(int parameterIndex, String x) {
		complemented++;
		super.setString(parameterIndex, x);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x) {
		complemented++;
		super.setTimestamp(parameterIndex, x);
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x) {
		complemented++;
		super.setBigDecimal(parameterIndex, x);
	}

	@Override
	public void setObject(int parameterIndex, Object x) {
		complemented++;
		super.setObject(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream stream, int length) {
		complemented++;
		super.setBinaryStream(parameterIndex, stream, length);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) {
		complemented++;
		super.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) {
		complemented++;
		super.setBytes(parameterIndex, x);
	}

	@Override
	public void setBlob(int parameterIndex, Blob blob) {
		complemented++;
		super.setBlob(parameterIndex, blob);
	}

	@Override
	public void setClob(int parameterIndex, Clob clob) {
		complemented++;
		super.setClob(parameterIndex, clob);
	}

	@Override
	public void setNull(int parameterIndex, int type) {
		complemented++;
		super.setNull(parameterIndex, type);
	}
}
