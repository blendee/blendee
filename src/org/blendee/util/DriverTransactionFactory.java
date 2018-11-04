package org.blendee.util;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

import org.blendee.internal.U;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.Configure;
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
		Configure config = ContextManager.get(BlendeeManager.class).getConfigure();

		driver = U.getInstance(Objects.requireNonNull(config.getOption(BlendeeConstants.JDBC_DRIVER_CLASS_NAME).get()));
		properties.setProperty("user", config.getOption(BlendeeConstants.JDBC_USER).get());
		properties.setProperty("password", config.getOption(BlendeeConstants.JDBC_PASSWORD).get());
		url = Objects.requireNonNull(config.getOption(BlendeeConstants.JDBC_URL).get());
	}

	@Override
	public Transaction createTransaction() {
		try {
			driver.connect(url, properties);
			return new JDBCTransaction(driver.connect(url, properties));
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
}
