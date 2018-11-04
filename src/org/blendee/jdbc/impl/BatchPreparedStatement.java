package org.blendee.jdbc.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.blendee.internal.U;
import org.blendee.jdbc.AutoCloseableFinalizer;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.Configure;

/**
 * {@link ConcreteBatchStatement} で使用する {@link BPreparedStatement} の実装クラスです。
 * @author 千葉 哲嗣
 */
class BatchPreparedStatement extends ConcretePreparedStatement {

	private final Configure config;

	private PreparedStatement statement;

	BatchPreparedStatement(Configure config, PreparedStatement statement, AutoCloseableFinalizer finalizer) {
		super(config, statement, finalizer);
		this.config = config;
		this.statement = statement;
	}

	void addBatch() {
		try {
			statement.addBatch();
		} catch (SQLException e) {
			close();
			throw config.getErrorConverter().convert(e);
		}
	}

	int[] executeBatch() {
		try {
			return statement.executeBatch();
		} catch (SQLException e) {
			close();
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void close() {
		U.close(statement);
	}

	@Override
	public BResultSet executeQuery() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeUpdate() {
		throw new UnsupportedOperationException();
	}
}
