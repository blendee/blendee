package org.blendee.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

import org.blendee.internal.U;
import org.blendee.jdbc.BSQLException;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.Transaction;
import org.blendee.jdbc.TransactionFactory;
import org.blendee.jdbc.impl.JDBCTransaction;

/**
 * {@link Driver} を利用してデータベースとの接続を確立する {@link TransactionFactory} です。
 * @author 千葉 哲嗣
 */
public class DriverTransactionFactory implements TransactionFactory {

	private final String url;

	private final Driver driver;

	private final Properties properties = new Properties();

	/**
	 * このクラスのコンストラクタです。
	 * @throws Exception ドライバークラスのロード時に発生した例外
	 */
	public DriverTransactionFactory() throws Exception {
		var config = ContextManager.get(BlendeeManager.class).getConfigure();

		driver = U.getInstance(Objects.requireNonNull(config.getOption(BlendeeConstants.JDBC_DRIVER_CLASS_NAME).get()));
		properties.setProperty("user", config.getOption(BlendeeConstants.JDBC_USER).get());
		properties.setProperty("password", config.getOption(BlendeeConstants.JDBC_PASSWORD).get());
		url = Objects.requireNonNull(config.getOption(BlendeeConstants.JDBC_URL).get());
	}

	@Override
	public Transaction createTransaction() {
		return new JDBCTransaction(getJDBCConnection());
	}

	/**
	 * @return JDBC URL
	 */
	protected String url() {
		return url;
	}

	/**
	 * @return JDBC Driver
	 */
	protected Driver driver() {
		return driver;
	}

	/**
	 * @return {@link Properties}
	 */
	protected Properties properties() {
		return properties;
	}

	/**
	 * @return JDBC {@link Connection}
	 */
	protected Connection getJDBCConnection() {
		try {
			var connection = driver().connect(url(), properties());

			if (connection == null) throw new IllegalStateException(driver.getClass() + "#connect(String, Properties) returns null");

			return connection;
		} catch (SQLException e) {
			throw new BSQLException(e);
		}
	}
}
