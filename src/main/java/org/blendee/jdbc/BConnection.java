package org.blendee.jdbc;

import java.sql.Connection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * {@link Connection} に似せ、機能を制限したインターフェイスです。
 * @author 千葉 哲嗣
 * @see BlendeeManager#getConnection()
 */
public interface BConnection {

	/**
	 * SQL 文から {@link BStatement} のインスタンスを生成し、返します。<br>
	 * パラメータで指定される SQL 文にはプレースホルダ '?' を使用することはできません。
	 * @param sql プレースホルダを持たない SQL
	 * @return {@link BStatement} のインスタンス
	 */
	BStatement getStatement(String sql);

	/**
	 * {@link #getStatement(String)} の簡易実行メソッドです。
	 * @param sql プレースホルダを持たない SQL
	 * @param consumer {@link BStatement} を受け取る {@link Consumer}
	 */
	default void createStatement(String sql, Consumer<BStatement> consumer) {
		try (BStatement statement = getStatement(sql)) {
			consumer.accept(statement);
		}
	}

	/**
	 * {@link #getStatement(String)} の簡易実行メソッドです。
	 * @param <T> function の戻り値
	 * @param sql プレースホルダを持たない SQL
	 * @param function {@link BStatement} を受け取る {@link Function}
	 * @return T
	 */
	default <T> T createStatementAndGet(String sql, Function<BStatement, T> function) {
		try (BStatement statement = getStatement(sql)) {
			return function.apply(statement);
		}
	}

	/**
	 * SQL 文から {@link BStatement} のインスタンスを生成し、返します。<br>
	 * パラメータで指定される SQL 文にはプレースホルダ '?' を含めることが可能です。
	 * @param sql プレースホルダを持つ SQL
	 * @param complementer プレースホルダに結びつける値を持つ
	 * @return {@link BStatement} のインスタンス
	 */
	BStatement getStatement(String sql, PreparedStatementComplementer complementer);

	/**
	 * SQL 文から {@link BStatement} のインスタンスを生成し、返します。<br>
	 * @param sql SQL とプレースホルダの値
	 * @return {@link BStatement} のインスタンス
	 */
	default BStatement getStatement(ComposedSQL sql) {
		return getStatement(sql.sql(), sql);
	}

	/**
	 * {@link #getStatement(String, PreparedStatementComplementer)} の簡易実行メソッドです。
	 * @param sql プレースホルダを持つ SQL
	 * @param complementer プレースホルダに結びつける値を持つ
	 * @param consumer {@link BStatement} を受け取る {@link Consumer}
	 */
	default void createStatement(
		String sql,
		PreparedStatementComplementer complementer,
		Consumer<BStatement> consumer) {
		try (BStatement statement = getStatement(sql, complementer)) {
			consumer.accept(statement);
		}
	}

	/**
	 * {@link #getStatement(String, PreparedStatementComplementer)} の簡易実行メソッドです。
	 * @param sql SQL とプレースホルダの値
	 * @param consumer {@link BStatement} を受け取る {@link Consumer}
	 */
	default void createStatement(ComposedSQL sql, Consumer<BStatement> consumer) {
		try (BStatement statement = getStatement(sql)) {
			consumer.accept(statement);
		}
	}

	/**
	 * {@link #getStatement(String, PreparedStatementComplementer)} の簡易実行メソッドです。
	 * @param <T> function の戻り値
	 * @param sql プレースホルダを持つ SQL
	 * @param complementer プレースホルダに結びつける値を持つ
	 * @param function {@link BStatement} を受け取る {@link Function}
	 * @return T
	 */
	default <T> T createStatementAndGet(
		String sql,
		PreparedStatementComplementer complementer,
		Function<BStatement, T> function) {
		try (BStatement statement = getStatement(sql, complementer)) {
			return function.apply(statement);
		}
	}

	/**
	 * {@link #getStatement(String, PreparedStatementComplementer)} の簡易実行メソッドです。
	 * @param <T> function の戻り値
	 * @param sql SQL とプレースホルダの値
	 * @param function {@link BStatement} を受け取る {@link Function}
	 * @return T
	 */
	default <T> T createStatementAndGet(ComposedSQL sql, Function<BStatement, T> function) {
		try (BStatement statement = getStatement(sql)) {
			return function.apply(statement);
		}
	}

	/**
	 * SQL 文から {@link BPreparedStatement} のインスタンスを生成し、返します。<br>
	 * パラメータで指定される SQL 文にはプレースホルダ '?' を含めることが可能です。
	 * @param sql プレースホルダを持つ SQL
	 * @return {@link BPreparedStatement} のインスタンス
	 */
	BPreparedStatement prepareStatement(String sql);

	/**
	 * {@link #prepareStatement(String)} の簡易実行メソッドです。
	 * @param sql プレースホルダを持つ SQL
	 * @param consumer {@link BPreparedStatement} を受け取る {@link Consumer}
	 */
	default void prepareStatement(String sql, Consumer<BPreparedStatement> consumer) {
		try (BPreparedStatement statement = prepareStatement(sql)) {
			consumer.accept(statement);
		}
	}

	/**
	 * {@link #prepareStatement(String)} の簡易実行メソッドです。
	 * @param <T> function の戻り値
	 * @param sql プレースホルダを持つ SQL
	 * @param function {@link BPreparedStatement} を受け取る {@link Function}
	 * @return T
	 */
	default <T> T prepareStatementAndGet(String sql, Function<BPreparedStatement, T> function) {
		try (BPreparedStatement statement = prepareStatement(sql)) {
			return function.apply(statement);
		}
	}

	/**
	 * {@link Batch} のインスタンスを生成し、返します。
	 * @return {@link Batch} のインスタンス
	 */
	Batch getBatch();

	/**
	 * {@link StatementWrapper} を設定し、この接続が生成する各 BPreparedStatement をラップさせます。
	 * @param wrapper この接続が生成する Statement のラッパー
	 */
	void setStatementWrapper(StatementWrapper wrapper);

	/**
	 * このクラスのインスタンスが内部に {@link Connection} を持つ場合、それを貸します。
	 * @param borrower 借り手
	 */
	void lend(JDBCBorrower<Connection> borrower);
}
