package jp.ats.blendee.jdbc;

import jp.ats.blendee.jdbc.wrapperbase.ConnectionBase;

/**
 * @author 千葉 哲嗣
 */
class LoggingConnection extends ConnectionBase
	implements PreparedStatementWrapper, BatchStatementWrapper {

	private final Logger logger;

	LoggingConnection(BConnection conn, Logger logger) {
		super(conn);
		conn.setPreparedStatementWrapper(this);
		conn.setBatchStatementWrapper(this);
		this.logger = logger;
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
	public BPreparedStatement wrap(BPreparedStatement statement) {
		return new LoggingPreparedStatement(statement, logger);
	}

	@Override
	public BatchStatement wrap(BatchStatement statement) {
		return new LoggingBatchStatement(statement, logger);
	}
}
