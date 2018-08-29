package org.blendee.jdbc.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.blendee.internal.U;
import org.blendee.jdbc.AutoCloseableFinalizer;
import org.blendee.jdbc.BConnection;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.BatchStatementWrapper;
import org.blendee.jdbc.BlendeeException;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.Configure;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.JDBCBorrower;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.jdbc.PreparedStatementWrapper;

/**
 * Blendee が使用する {@link BConnection} の標準実装クラスです。
 * @author 千葉 哲嗣
 */
public class ConcreteConnection implements BConnection {

	private final Connection connection;

	private final List<PreparedStatementWrapper> preparedStatementWrappers = new LinkedList<>();

	private final List<BatchStatementWrapper> batchStatementWrappers = new LinkedList<>();

	private final Configure config;

	private final AutoCloseableFinalizer finalizer;

	/**
	 * JDBC 接続を使用してインスタンスを生成します。
	 * @param config {@link Configure}
	 * @param connection JDBC 接続
	 */
	public ConcreteConnection(Configure config, Connection connection) {
		try {
			if (!config.usesAutoCommit())
				connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}

		AutoCloseableFinalizer finalizer = ContextManager.get(BlendeeManager.class).getAutoCloseableFinalizer();
		if (finalizer.started()) {
			finalizer.regist(this, connection);
			this.finalizer = finalizer;
		} else {
			this.finalizer = null;
		}

		this.config = config;
		this.connection = connection;
	}

	@Override
	public BStatement getStatement(String sql) {
		ConcretePreparedStatement statement = create(sql);
		return wrap(statement, preparedStatementWrappers);
	}

	@Override
	public BStatement getStatement(String sql, PreparedStatementComplementer complementer) {
		ConcretePreparedStatement statement = create(sql);
		BPreparedStatement wrapped = wrap(statement, preparedStatementWrappers);
		complementer.complement(wrapped);
		return wrapped;
	}

	@Override
	public BPreparedStatement prepareStatement(String sql) {
		ConcretePreparedStatement statement = create(sql);
		BPreparedStatement wrapped = wrap(statement, preparedStatementWrappers);
		return wrapped;
	}

	@Override
	public BatchStatement getBatchStatement() {
		ConcreteBatchStatement statement = new ConcreteBatchStatement(this);
		return wrap(statement, batchStatementWrappers);
	}

	@Override
	public void setPreparedStatementWrapper(PreparedStatementWrapper wrapper) {
		preparedStatementWrappers.add(wrapper);
	}

	@Override
	public void setBatchStatementWrapper(BatchStatementWrapper wrapper) {
		batchStatementWrappers.add(wrapper);
	}

	@Override
	public void lend(JDBCBorrower<Connection> borrower) {
		try {
			borrower.accept(connection);
		} catch (SQLException e) {
			throw new BlendeeException(e);
		}
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	BatchPreparedStatement createForBatch(String sql) {
		return new BatchPreparedStatement(config, createStatement(sql), finalizer);
	}

	BPreparedStatement wrapInternal(ConcretePreparedStatement statement) {
		return wrap(statement, preparedStatementWrappers);
	}

	private ConcretePreparedStatement create(String sql) {
		return new ConcretePreparedStatement(config, createStatement(sql), finalizer);
	}

	private static BPreparedStatement wrap(
		BPreparedStatement statement,
		List<PreparedStatementWrapper> wrappers) {
		for (PreparedStatementWrapper wrapper : wrappers)
			statement = wrapper.wrap(statement);
		return statement;
	}

	private static BatchStatement wrap(
		BatchStatement statement,
		List<BatchStatementWrapper> wrappers) {
		for (BatchStatementWrapper wrapper : wrappers)
			statement = wrapper.wrap(statement);
		return statement;
	}

	private PreparedStatement createStatement(String sql) {
		try {
			return connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}
}
