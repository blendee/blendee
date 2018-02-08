package org.blendee.jdbc;

import java.sql.Statement;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * {@link Statement} に似せ、機能を制限したインターフェイスです。
 * @author 千葉 哲嗣
 * @see BlenConnection#getStatement(String)
 * @see BlenConnection#getStatement(String, PreparedStatementComplementer)
 */
public interface BlenStatement extends AutoCloseable {

	/**
	 * 検索を行います。
	 * @return 検索結果
	 */
	BlenResultSet executeQuery();

	/**
	 * 検索を行います。
	 * @param consumer 検索結果を受け取る {@link Consumer}
	 */
	default void executeQuery(Consumer<BlenResultSet> consumer) {
		try (BlenResultSet result = executeQuery()) {
			consumer.accept(result);
		}
	}

	/**
	 * 検索を行います。<br>
	 * 何らかの結果を返すことが可能です。
	 * @param function 検索結果を受け取る {@link Function}
	 * @return T
	 */
	default <T> T executeQueryAndGet(Function<BlenResultSet, T> function) {
		try (BlenResultSet result = executeQuery()) {
			return function.apply(result);
		}
	}

	/**
	 * 更新を行います。
	 * @return 更新件数
	 */
	int executeUpdate();

	/**
	 * SQL文を実行します。
	 * @return 最初の結果が {@link BlenResultSet} オブジェクトの場合は true 
	 */
	boolean execute();

	/**
	 * {@link #execute()} の結果の {@link BlenResultSet} を取得します。
	 * @return 更新カウントであるか、または結果がない場合は null
	 */
	BlenResultSet getResultSet();

	/**
	 * {@link #execute()} の結果の、更新カウントを取得します。
	 * @return 現在の結果が {@link BlenResultSet} オブジェクトであるか、または結果がない場合は -1 
	 */
	int getUpdateCount();

	/**
	 * Statement オブジェクトの次の結果に移動します。
	 * @return 次の結果が ResultSet オブジェクトの場合は true
	 */
	boolean getMoreResults();

	/**
	 * このステートメントを閉じます。<br>
	 */
	@Override
	void close();
}
