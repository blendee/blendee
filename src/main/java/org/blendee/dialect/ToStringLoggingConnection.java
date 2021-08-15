package org.blendee.dialect;

import java.util.Objects;
import java.util.logging.Level;

import org.blendee.jdbc.BConnection;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.Batch;
import org.blendee.jdbc.LoggingStatement;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.jdbc.SQLLogger;
import org.blendee.jdbc.StatementWrapper;
import org.blendee.jdbc.wrapperbase.BatchBase;
import org.blendee.jdbc.wrapperbase.ConnectionBase;
import org.blendee.jdbc.wrapperbase.PreparedStatementBase;

/**
 * @author 千葉 哲嗣
 */
class ToStringLoggingConnection extends ConnectionBase implements StatementWrapper {

	static final Level level = Level.INFO;

	private final SQLLogger logger;

	private final BConnection base;

	ToStringLoggingConnection(BConnection conn, SQLLogger logger) {
		Objects.requireNonNull(logger);
		base = conn;
		conn.setStatementWrapper(this);
		this.logger = logger;
	}

	@Override
	public BStatement wrap(BStatement statement) {
		return new LoggingStatement(statement, logger);
	}

	@Override
	public BPreparedStatement wrap(BPreparedStatement statement) {
		return new LoggingPreparedStatement(statement, logger);
	}

	@Override
	public Batch wrap(Batch statement) {
		return new LoggingBatch(statement, logger);
	}

	@Override
	protected BConnection base() {
		return base;
	}

	private static class LoggingPreparedStatement extends PreparedStatementBase {

		private final SQLLogger logger;

		private final BPreparedStatement base;

		LoggingPreparedStatement(BPreparedStatement statement, SQLLogger logger) {
			Objects.requireNonNull(statement);
			Objects.requireNonNull(logger);

			base = statement;
			this.logger = logger;
		}

		@Override
		protected BPreparedStatement base() {
			return base;
		}

		@Override
		public BResultSet executeQuery() {
			logger.setSql(sql());
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
			logger.setSql(sql());
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
			logger.setSql(sql());
			logger.flush();
			long start = System.nanoTime();
			try {
				return super.execute();
			} finally {
				logger.logElapsed(start);
			}
		}

		private String sql() {
			return base.lendPreparedStatementAndGet(s -> s.toString());
		}
	}

	private static class LoggingBatch extends BatchBase {

		private final SQLLogger logger;

		private final Batch base;

		LoggingBatch(Batch base, SQLLogger logger) {
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
			super.add(sql, new LoggingComplementer(logger, complementer));
		}

		@Override
		public int[] execute() {
			long start = System.nanoTime();
			try {
				return super.execute();
			} finally {
				logger.logElapsed(start);
			}
		}

		private static class LoggingComplementer implements PreparedStatementComplementer {

			private final SQLLogger logger;

			private final PreparedStatementComplementer base;

			private LoggingComplementer(
				SQLLogger logger,
				PreparedStatementComplementer base) {
				this.logger = logger;
				this.base = base;
			}

			@Override
			public void complement(BPreparedStatement statement) {
				base.complement(statement);
				logger.setSql(statement.lendPreparedStatementAndGet(s -> s.toString()));
				logger.flush();
			}
		}
	}
}
