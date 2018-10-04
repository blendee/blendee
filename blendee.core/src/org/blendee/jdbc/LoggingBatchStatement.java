package org.blendee.jdbc;

import java.util.Objects;

import org.blendee.jdbc.wrapperbase.BatchStatementBase;

/**
 * @author 千葉 哲嗣
 */
class LoggingBatchStatement extends BatchStatementBase {

	private final SQLLogger logger;

	private final BatchStatement base;

	LoggingBatchStatement(BatchStatement statement, SQLLogger logger) {
		Objects.requireNonNull(statement);
		Objects.requireNonNull(logger);
		base = statement;
		this.logger = logger;
	}

	@Override
	protected BatchStatement base() {
		return base;
	}

	@Override
	public void addBatch(String sql) {
		logger.setSql(sql);
		super.addBatch(sql);
		logger.flush();
	}

	@Override
	public void addBatch(String sql, PreparedStatementComplementer complementer) {
		super.addBatch(sql, new LoggingComplementer(sql, logger, complementer));
	}

	@Override
	public int[] executeBatch() {
		long start = System.nanoTime();
		try {
			return super.executeBatch();
		} finally {
			logger.logElapsed(start);
		}
	}

	private static class LoggingComplementer implements PreparedStatementComplementer {

		private final String sql;

		private final SQLLogger logger;

		private final PreparedStatementComplementer base;

		private LoggingComplementer(
			String sql,
			SQLLogger logger,
			PreparedStatementComplementer base) {
			this.sql = sql;
			this.logger = logger;
			this.base = base;
		}

		@Override
		public void complement(BPreparedStatement statement) {
			logger.setSql(sql);
			base.complement(statement);
			logger.flush();
		}
	}
}
