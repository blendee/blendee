package org.blendee.jdbc;

import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.blendee.jdbc.impl.JDBCTransaction;

/**
 * {@link DataSource} を利用してデータベースとの接続を確立する {@link TransactionFactory} です。<br>
 * データソースは "jdbc/datasource" で定義してください。
 * @author 千葉 哲嗣
 */
public class DataSourceTransactionFactory implements TransactionFactory {

	private final DataSource dataSource;

	/**
	 * データソースは "jdbc/datasource" で設定してください。
	 * @throws NamingException データソース取得時の例外
	 */
	public DataSourceTransactionFactory() throws NamingException {
		dataSource = (DataSource) new InitialContext().lookup(
			"java:comp/env/jdbc/datasource");
	}

	/**
	 * 任意のデータソース名でインスタンスを生成するコンストラクタです。
	 * @param datasourceName 任意のデータソース名
	 * @throws NamingException データソース取得時の例外
	 */
	public DataSourceTransactionFactory(String datasourceName) throws NamingException {
		dataSource = (DataSource) new InitialContext().lookup(
			"java:comp/env/" + datasourceName);
	}

	@Override
	public BTransaction createTransaction() {
		try {
			return new JDBCTransaction(dataSource.getConnection());
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
}
