package org.blendee.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Timestamp;

import org.blendee.jdbc.wrapperbase.PreparedStatementBase;

/**
 * @author 千葉 哲嗣
 */
class LoggingPreparedStatement extends PreparedStatementBase {

	private final Logger logger;

	LoggingPreparedStatement(BlenPreparedStatement statement, Logger logger) {
		super(statement);
		this.logger = logger;
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) {
		logger.addBindingValue("boolean", parameterIndex, Boolean.valueOf(x));
		super.setBoolean(parameterIndex, x);
	}

	@Override
	public void setDouble(int parameterIndex, double x) {
		logger.addBindingValue("double", parameterIndex, Double.valueOf(x));
		super.setDouble(parameterIndex, x);
	}

	@Override
	public void setFloat(int parameterIndex, float x) {
		logger.addBindingValue("float", parameterIndex, Float.valueOf(x));
		super.setFloat(parameterIndex, x);
	}

	@Override
	public void setInt(int parameterIndex, int x) {
		logger.addBindingValue("int", parameterIndex, Integer.valueOf(x));
		super.setInt(parameterIndex, x);
	}

	@Override
	public void setLong(int parameterIndex, long x) {
		logger.addBindingValue("long", parameterIndex, Long.valueOf(x));
		super.setLong(parameterIndex, x);
	}

	@Override
	public void setString(int parameterIndex, String x) {
		logger.addBindingValue("String", parameterIndex, x);
		super.setString(parameterIndex, x);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x) {
		logger.addBindingValue("Timestamp", parameterIndex, x);
		super.setTimestamp(parameterIndex, x);
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x) {
		logger.addBindingValue("BigDecimal", parameterIndex, x);
		super.setBigDecimal(parameterIndex, x);
	}

	@Override
	public void setObject(int parameterIndex, Object x) {
		logger.addBindingValue("Object", parameterIndex, x);
		super.setObject(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(
		int parameterIndex,
		InputStream stream,
		int length) {
		logger.addBindingValue("BinaryStream", parameterIndex, "length=" + length);
		super.setBinaryStream(parameterIndex, stream, length);
	}

	@Override
	public void setCharacterStream(
		int parameterIndex,
		Reader reader,
		int length) {
		logger.addBindingValue("CharacterStream", parameterIndex, "length=" + length);
		super.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setNull(int parameterIndex, int type) {
		logger.addBindingValue("Null", parameterIndex, null);
		super.setNull(parameterIndex, type);
	}

	@Override
	public BlenResultSet executeQuery() {
		logger.flush();
		long start = System.nanoTime();
		try {
			return super.executeQuery();
		} finally {
			logger.logElapsed(start);
		}
	}

	@Override
	public int executeUpdate() {
		logger.flush();
		long start = System.nanoTime();
		try {
			return super.executeUpdate();
		} finally {
			logger.logElapsed(start);
		}
	}

	@Override
	public boolean execute() {
		logger.flush();
		long start = System.nanoTime();
		try {
			return super.execute();
		} finally {
			logger.logElapsed(start);
		}
	}
}
