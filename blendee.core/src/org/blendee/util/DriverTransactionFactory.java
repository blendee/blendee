package org.blendee.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

import org.blendee.jdbc.BTransaction;
import org.blendee.jdbc.BlendeeContext;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.Configure;
import org.blendee.jdbc.TransactionFactory;
import org.blendee.jdbc.impl.JDBCTransaction;

/**
 * {@link DriverManager} を利用してデータベースとの接続を確立する {@link TransactionFactory} です。
 * @author 千葉 哲嗣
 */
public class DriverTransactionFactory implements TransactionFactory {

	private final String url;

	private final String user;

	private final String password;

	/**
	 * このクラスのコンストラクタです。
	 * @throws Exception ドライバークラスのロード時に発生した例外
	 */
	public DriverTransactionFactory() throws Exception {
		Configure config = BlendeeContext.get(BlendeeManager.class).getConfigure();

		Class.forName(
			Objects.requireNonNull(config.getOption(BlendeeConstants.JDBC_DRIVER_CLASS_NAME).get()),
			false,
			getClassLoader());

		url = Objects.requireNonNull(config.getOption(BlendeeConstants.JDBC_URL).get());

		user = config.getOption(BlendeeConstants.JDBC_USER).get();
		password = config.getOption(BlendeeConstants.JDBC_PASSWORD).get();
	}

	@Override
	public BTransaction createTransaction() {
		try {
			return new JDBCTransaction(getConnection(url, user, password));
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * {@link Driver} をロードするためのクラスローダーを返します。
	 * @return {@link ClassLoader}
	 */
	protected ClassLoader getClassLoader() {
		return DriverManager.class.getClassLoader();
	}

	/**
	 * {@link Connection} を取得します。
	 * @param url 接続先
	 * @param user ユーザー
	 * @param password パスワード
	 * @return {@link Connection}
	 * @throws SQLException 接続時の例外
	 */
	protected Connection getConnection(String url, String user, String password) throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}
}
