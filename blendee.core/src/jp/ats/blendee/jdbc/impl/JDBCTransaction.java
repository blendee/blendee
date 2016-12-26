package jp.ats.blendee.jdbc.impl;

import java.sql.Connection;
import java.sql.SQLException;

import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.Configure;
import jp.ats.blendee.jdbc.BConnection;
import jp.ats.blendee.jdbc.BlendeeContext;
import jp.ats.blendee.jdbc.BlendeeManager;
import jp.ats.blendee.jdbc.BTransaction;

/**
 * Blendee が使用する {@link BTransaction} の標準実装クラスです。
 *
 * @author 千葉 哲嗣
 */
public class JDBCTransaction extends BTransaction {

	private final Configure config = BlendeeContext.get(BlendeeManager.class).getConfigure();

	private final java.sql.Connection jdbcConnection;

	private final ConcreteConnection connection;

	/**
	 * JDBC 接続を使用してインスタンスを生成します。
	 *
	 * @param jdbcConnection JDBC 接続
	 */
	public JDBCTransaction(Connection jdbcConnection) {
		this.jdbcConnection = jdbcConnection;
		connection = new ConcreteConnection(config, jdbcConnection);
	}

	@Override
	protected BConnection getConnectionInternal() {
		return connection;
	}

	@Override
	protected void commitInternal() {
		try {
			jdbcConnection.commit();
		} catch (SQLException e) {
			close();
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	protected void rollbackInternal() {
		try {
			jdbcConnection.rollback();
		} catch (SQLException e) {
			close();
			throw config.getErrorConverter().convert(e);
		}
	}

	/**
	 * 内部で使用する {@link Connection} を閉じます。
	 * <br>
	 * 何らかの理由で接続を後で閉じたい場合、このメソッドをオーバーライドしてください。
	 */
	@Override
	protected void closeInternal() {
		U.close(jdbcConnection);
	}
}
