package org.blendee.develop;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.Initializer;
import org.blendee.jdbc.Transaction;
import org.blendee.jdbc.TransactionFactory;
import org.blendee.jdbc.impl.JDBCTransaction;

/**
 * Blendee を、 JDBC ドライバのクラス名、データベースの URL 、ユーザー名、パスワードのみで、開発時など、一時的に使用するためのクラスです。
 * @author 千葉 哲嗣
 */
public class EasyStarter {

	private static final Object lock = new Object();

	private static String url;

	private static String userName;

	private static String password;

	/**
	 * Blendee を使用可能な状態にします。
	 * @param jdbcDriverClassName JDBC ドライバの完全クラス名
	 * @param url データベースの URL
	 * @param schemaNames 使用するスキーマ名
	 * @param userName ユーザー名
	 * @param password パスワード
	 */
	public static void start(
		String jdbcDriverClassName,
		String url,
		String[] schemaNames,
		String userName,
		String password) {
		try {
			Class.forName(jdbcDriverClassName);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		synchronized (lock) {
			EasyStarter.url = url;
			EasyStarter.userName = userName;
			EasyStarter.password = password;
		}

		Initializer init = new Initializer();
		for (String schemaName : schemaNames)
			init.addSchemaName(schemaName);
		init.setTransactionFactoryClass(EasyTransactionFactory.class);
		ContextManager.get(BlendeeManager.class).initialize(init);
	}

	/**
	 * {@link EasyStarter} で使用する {@link TransactionFactory} の簡易実装クラスです。
	 */
	public static class EasyTransactionFactory implements TransactionFactory {

		@Override
		public Transaction createTransaction() {
			try {
				synchronized (lock) {
					return new JDBCTransaction(DriverManager.getConnection(url, userName, password));
				}
			} catch (SQLException e) {
				throw new IllegalStateException(e);
			}
		}
	}
}
