package org.blendee.jdbc;

import java.sql.Statement;
import java.util.function.Consumer;

/**
 * {@link Statement} に似せ、機能を制限したインターフェイスです。
 * @author 千葉 哲嗣
 * @see BConnection#getStatement(String)
 * @see BConnection#getStatement(String, PreparedStatementComplementer)
 */
public interface BStatement extends AutoCloseable {

	/**
	 * 検索を行います。
	 * @return 検索結果
	 */
	BResultSet executeQuery();

	/**
	 * 検索を行います。
	 * @param consumer 検索結果を受け取る {@link Consumer}
	 */
	default void executeQuery(Consumer<BResultSet> consumer) {
		try (BResultSet result = executeQuery()) {
			consumer.accept(result);
		}
	}

	/**
	 * 更新を行います。
	 * @return 更新件数
	 */
	int executeUpdate();

	/**
	 * SQL文を実行します。
	 * @return 最初の結果が {@link BResultSet} オブジェクトの場合は true 
	 */
	boolean execute();

	/**
	 * {@link #execute()} の結果の {@link BResultSet} を取得します。
	 * @return 更新カウントであるか、または結果がない場合は null
	 */
	BResultSet getResultSet();

	/**
	 * {@link #execute()} の結果の、更新カウントを取得します。
	 * @return 現在の結果が {@link BResultSet} オブジェクトであるか、または結果がない場合は -1 
	 */
	int getUpdateCount();

	/**
	 * Statement オブジェクトの次の結果に移動します。
	 * @return 次の結果が ResultSet オブジェクトの場合は true
	 */
	boolean getMoreResults();

	/**
	 * このステートメントを閉じます。<br>
	 * このステートメントが生成した {@link BResultSet} も同時に閉じます。
	 */
	@Override
	void close();
}
