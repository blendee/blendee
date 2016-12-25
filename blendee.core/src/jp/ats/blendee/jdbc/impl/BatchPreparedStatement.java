package jp.ats.blendee.jdbc.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.Configure;
import jp.ats.blendee.jdbc.BPreparedStatement;
import jp.ats.blendee.jdbc.BResultSet;

/**
 * {@link ConcreteBatchStatement} で使用する {@link BPreparedStatement} の実装クラスです。
 *
 * @author 千葉 哲嗣
 */
class BatchPreparedStatement extends ConcretePreparedStatement {

	private final Configure config;

	private PreparedStatement statement;

	BatchPreparedStatement(Configure config, PreparedStatement statement) {
		super(config, statement);
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

	@Override
	protected void finalize() {
		close();
	}
}
