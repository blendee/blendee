package org.blendee.jdbc;

import java.util.Objects;

import org.blendee.jdbc.wrapperbase.StatementBase;

/**
 * @author 千葉 哲嗣
 */
class LoggingStatement extends StatementBase {

	private final Logger logger;

	private final BStatement base;

	LoggingStatement(BStatement statement, Logger logger) {
		Objects.requireNonNull(statement);
		Objects.requireNonNull(logger);

		base = statement;
		this.logger = logger;
	}

	@Override
	protected BStatement base() {
		return base;
	}

	@Override
	public BResultSet executeQuery() {
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
