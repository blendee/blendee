package org.blendee.jdbc.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.blendee.internal.U;
import org.blendee.jdbc.BlenConnection;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.Configure;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.Transaction;

/**
 * Blendee が使用する {@link Transaction} の標準実装クラスです。
 * @author 千葉 哲嗣
 */
public class JDBCTransaction extends Transaction {

	private final Configure config = ContextManager.get(BlendeeManager.class).getConfigure();

	private final Connection jdbcConnection;

	private final ConcreteConnection connection;

	/**
	 * JDBC 接続を使用してインスタンスを生成します。
	 * @param jdbcConnection JDBC 接続
	 */
	public JDBCTransaction(Connection jdbcConnection) {
		this.jdbcConnection = jdbcConnection;
		connection = new ConcreteConnection(config, jdbcConnection);
	}

	@Override
	protected BlenConnection getConnectionInternal() {
		return connection;
	}

	@Override
	protected void commitInternal() {
		try {
			if (!jdbcConnection.isClosed())
				jdbcConnection.commit();
		} catch (SQLException e) {
			close();
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	protected void rollbackInternal() {
		try {
			if (!jdbcConnection.isClosed())
				jdbcConnection.rollback();
		} catch (SQLException e) {
			close();
			throw config.getErrorConverter().convert(e);
		}
	}

	/**
	 * 内部で使用する {@link Connection} を閉じます。<br>
	 * 何らかの理由で接続を後で閉じたい場合、このメソッドをオーバーライドしてください。
	 */
	@Override
	protected void closeInternal() {
		U.close(jdbcConnection);
	}
}
