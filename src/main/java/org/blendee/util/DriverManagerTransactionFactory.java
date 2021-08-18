package org.blendee.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.blendee.jdbc.BSQLException;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.Transaction;
import org.blendee.jdbc.TransactionFactory;
import org.blendee.jdbc.impl.JDBCTransaction;

/**
 * {@link DriverManager} を利用してデータベースとの接続を確立する {@link TransactionFactory} です。
 * @author 千葉 哲嗣
 */
public class DriverManagerTransactionFactory implements TransactionFactory {

	private final String url;

	private final String user;

	private final String password;

	/**
	 * このクラスのコンストラクタです。
	 * @throws IllegalStateException ドライバークラスのロード時に発生した例外
	 */
	public DriverManagerTransactionFactory() {
		var config = ContextManager.get(BlendeeManager.class).getConfigure();

		config.getOption(BlendeeConstants.JDBC_DRIVER_CLASS_NAME).ifPresent(c -> {
			try {
				Class.forName(c, false, getClassLoader());
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		});

		url = config.getOption(BlendeeConstants.JDBC_URL).get();

		user = config.getOption(BlendeeConstants.JDBC_USER).get();
		password = config.getOption(BlendeeConstants.JDBC_PASSWORD).get();
	}

	@Override
	public Transaction createTransaction() {
		return new JDBCTransaction(getJDBCConnection());
	}

	/**
	 * {@link Driver} をロードするためのクラスローダーを返します。
	 * @return {@link ClassLoader}
	 */
	protected ClassLoader getClassLoader() {
		return DriverManagerTransactionFactory.class.getClassLoader();
	}

	/**
	 * {@link Connection} を取得します。
	 * @return {@link Connection}
	 */
	protected Connection getJDBCConnection() {
		try {
			return DriverManager.getConnection(url(), user(), password());
		} catch (SQLException e) {
			throw new BSQLException(e);
		}
	}

	/**
	 * 接続先
	 * @return URL
	 */
	protected String url() {
		return url;
	}

	/**
	 * ユーザー
	 * @return ユーザー
	 */
	protected String user() {
		return user;
	}

	/**
	 * パスワード
	 * @return パスワード
	 */
	protected String password() {
		return password;
	}
}
