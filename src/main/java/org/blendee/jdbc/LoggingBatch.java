package org.blendee.jdbc;

import java.util.Objects;

import org.blendee.jdbc.wrapperbase.BatchBase;

/**
 * @author 千葉 哲嗣
 */
public class LoggingBatch extends BatchBase {

	private final SQLLogger logger;

	private final Batch base;

	/**
	 * @param base
	 * @param logger
	 */
	public LoggingBatch(Batch base, SQLLogger logger) {
		this.base = Objects.requireNonNull(base);
		this.logger = Objects.requireNonNull(logger);
	}

	@Override
	protected Batch base() {
		return base;
	}

	@Override
	public void add(String sql) {
		logger.setSql(sql);
		super.add(sql);
		logger.flush();
	}

	@Override
	public void add(String sql, PreparedStatementComplementer complementer) {
		super.add(sql, new LoggingComplementer(sql, logger, complementer));
	}

	@Override
	public int[] execute() {
		var start = System.nanoTime();
		try {
			return super.execute();
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
