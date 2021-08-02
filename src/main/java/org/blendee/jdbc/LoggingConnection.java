package org.blendee.jdbc;

import java.util.Objects;
import java.util.logging.Level;

import org.blendee.jdbc.wrapperbase.ConnectionBase;

/**
 * @author 千葉 哲嗣
 */
class LoggingConnection extends ConnectionBase implements StatementWrapper {

	static final Level level = Level.INFO;

	private final SQLLogger logger;

	private final BConnection base;

	LoggingConnection(BConnection conn, SQLLogger logger) {
		Objects.requireNonNull(logger);
		base = conn;
		conn.setStatementWrapper(this);
		this.logger = logger;
	}

	@Override
	protected BConnection base() {
		return base;
	}

	@Override
	public BStatement getStatement(String sql) {
		logger.setSql(sql);
		return super.getStatement(sql);
	}

	@Override
	public BStatement getStatement(
		String sql,
		PreparedStatementComplementer complementer) {
		logger.setSql(sql);
		return super.getStatement(sql, complementer);
	}

	@Override
	public BPreparedStatement prepareStatement(String sql) {
		logger.setSql(sql);
		return super.prepareStatement(sql);
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
}
