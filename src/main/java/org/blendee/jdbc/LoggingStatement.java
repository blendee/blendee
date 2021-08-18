package org.blendee.jdbc;

import java.util.Objects;

import org.blendee.jdbc.wrapperbase.StatementBase;

/**
 * @author 千葉 哲嗣
 */
public class LoggingStatement extends StatementBase {

	private final SQLLogger logger;

	private final BStatement base;

	/**
	 * @param statement
	 * @param logger
	 */
	public LoggingStatement(BStatement statement, SQLLogger logger) {
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
		var start = System.nanoTime();
		try {
			return super.executeQuery();
		} finally {
			logger.logElapsed(start);
		}
	}

	@Override
	public int executeUpdate() {
		logger.flush();
		var start = System.nanoTime();
		try {
			return super.executeUpdate();
		} finally {
			logger.logElapsed(start);
		}
	}

	@Override
	public boolean execute() {
		logger.flush();
		var start = System.nanoTime();
		try {
			return super.execute();
		} finally {
			logger.logElapsed(start);
		}
	}
}
