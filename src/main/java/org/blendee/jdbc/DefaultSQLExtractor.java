package org.blendee.jdbc;

class DefaultSQLExtractor implements SQLExtractor {

	@Override
	public BConnection newLoggingConnection(BConnection base, SQLLogger logger) {
		return new LoggingConnection(base, logger);
	}
}
