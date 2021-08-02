package org.blendee.jdbc.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.blendee.internal.U;
import org.blendee.jdbc.AutoCloseableFinalizer;
import org.blendee.jdbc.BConnection;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.Batch;
import org.blendee.jdbc.BlendeeException;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.Configure;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.JDBCBorrower;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.jdbc.StatementWrapper;

/**
 * Blendee が使用する {@link BConnection} の標準実装クラスです。
 * @author 千葉 哲嗣
 */
public class ConcreteConnection implements BConnection {

	private final Connection connection;

	private final Set<StatementWrapper> statementWrappers = new LinkedHashSet<>();

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
		if (finalizer != null && finalizer.started()) {
			finalizer.register(this, connection);
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
		return wrap((BStatement) statement, statementWrappers);
	}

	@Override
	public BPreparedStatement getStatement(String sql, PreparedStatementComplementer complementer) {
		ConcretePreparedStatement statement = create(sql);
		BPreparedStatement wrapped = wrap(statement, statementWrappers);
		complementer.complement(wrapped);
		return wrapped;
	}

	@Override
	public BPreparedStatement prepareStatement(String sql) {
		ConcretePreparedStatement statement = create(sql);
		BPreparedStatement wrapped = wrap(statement, statementWrappers);
		return wrapped;
	}

	@Override
	public Batch getBatch() {
		ConcreteBatch batch = new ConcreteBatch(this);
		return wrap(batch, statementWrappers);
	}

	@Override
	public void setStatementWrapper(StatementWrapper wrapper) {
		statementWrappers.add(wrapper);
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

	BPreparedStatement wrap(ConcretePreparedStatement statement) {
		return wrap(statement, statementWrappers);
	}

	private ConcretePreparedStatement create(String sql) {
		return new ConcretePreparedStatement(config, createStatement(sql), finalizer);
	}

	private static BStatement wrap(
		BStatement statement,
		Set<StatementWrapper> wrappers) {
		for (StatementWrapper wrapper : wrappers)
			statement = wrapper.wrap(statement);
		return statement;
	}

	private static BPreparedStatement wrap(
		BPreparedStatement statement,
		Set<StatementWrapper> wrappers) {
		for (StatementWrapper wrapper : wrappers)
			statement = wrapper.wrap(statement);
		return statement;
	}

	private static Batch wrap(
		Batch batch,
		Set<StatementWrapper> wrappers) {
		for (StatementWrapper wrapper : wrappers)
			batch = wrapper.wrap(batch);
		return batch;
	}

	private PreparedStatement createStatement(String sql) {
		try {
			return connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}
}
