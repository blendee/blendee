package org.blendee.jdbc;

import java.sql.Connection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * {@link Connection} に似せ、機能を制限したインターフェイスです。
 * @author 千葉 哲嗣
 * @see BlendeeManager#getConnection()
 */
public interface BlenConnection extends Metadata {

	/**
	 * SQL 文から {@link BlenStatement} のインスタンスを生成し、返します。<br>
	 * パラメータで指定される SQL 文にはプレースホルダ '?' を使用することはできません。
	 * @param sql プレースホルダを持たない SQL
	 * @return {@link BlenStatement} のインスタンス
	 */
	BlenStatement getStatement(String sql);

	/**
	 * {@link #getStatement(String)} の簡易実行メソッドです。
	 * @param sql プレースホルダを持たない SQL
	 * @param consumer {@link BlenStatement} を受け取る {@link Consumer}
	 */
	default void executeStatement(String sql, Consumer<BlenStatement> consumer) {
		try (BlenStatement statement = getStatement(sql)) {
			consumer.accept(statement);
		}
	}

	/**
	 * {@link #getStatement(String)} の簡易実行メソッドです。
	 * @param sql プレースホルダを持たない SQL
	 * @param function {@link BlenStatement} を受け取る {@link Function}
	 * @return T
	 */
	default <T> T executeStatementAndGet(String sql, Function<BlenStatement, T> function) {
		try (BlenStatement statement = getStatement(sql)) {
			return function.apply(statement);
		}
	}

	/**
	 * SQL 文から {@link BlenStatement} のインスタンスを生成し、返します。<br>
	 * パラメータで指定される SQL 文にはプレースホルダ '?' を含めることが可能です。
	 * @param sql プレースホルダを持つ SQL
	 * @param complementer プレースホルダに結びつける値を持つ
	 * @return {@link BlenStatement} のインスタンス
	 */
	BlenStatement getStatement(String sql, PreparedStatementComplementer complementer);

	/**
	 * SQL 文から {@link BlenStatement} のインスタンスを生成し、返します。<br>
	 * @param sql SQL とプレースホルダの値
	 * @return {@link BlenStatement} のインスタンス
	 */
	BlenStatement getStatement(ComposedSQL sql);

	/**
	 * {@link #getStatement(String, PreparedStatementComplementer)} の簡易実行メソッドです。
	 * @param sql プレースホルダを持つ SQL
	 * @param complementer プレースホルダに結びつける値を持つ
	 * @param consumer {@link BlenStatement} を受け取る {@link Consumer}
	 */
	default void execute(
		String sql,
		PreparedStatementComplementer complementer,
		Consumer<BlenStatement> consumer) {
		try (BlenStatement statement = getStatement(sql, complementer)) {
			consumer.accept(statement);
		}
	}

	/**
	 * {@link #getStatement(String, PreparedStatementComplementer)} の簡易実行メソッドです。
	 * @param sql SQL とプレースホルダの値
	 * @param consumer {@link BlenStatement} を受け取る {@link Consumer}
	 */
	default void execute(ComposedSQL sql, Consumer<BlenStatement> consumer) {
		try (BlenStatement statement = getStatement(sql)) {
			consumer.accept(statement);
		}
	}

	/**
	 * {@link #getStatement(String, PreparedStatementComplementer)} の簡易実行メソッドです。
	 * @param sql プレースホルダを持つ SQL
	 * @param complementer プレースホルダに結びつける値を持つ
	 * @param function {@link BlenStatement} を受け取る {@link Function}
	 * @return T
	 */
	default <T> T executeAndGet(
		String sql,
		PreparedStatementComplementer complementer,
		Function<BlenStatement, T> function) {
		try (BlenStatement statement = getStatement(sql, complementer)) {
			return function.apply(statement);
		}
	}

	/**
	 * {@link #getStatement(String, PreparedStatementComplementer)} の簡易実行メソッドです。
	 * @param sql SQL とプレースホルダの値
	 * @param function {@link BlenStatement} を受け取る {@link Function}
	 * @return T
	 */
	default <T> T executeAndGet(ComposedSQL sql, Function<BlenStatement, T> function) {
		try (BlenStatement statement = getStatement(sql)) {
			return function.apply(statement);
		}
	}

	/**
	 * SQL 文から {@link BlenPreparedStatement} のインスタンスを生成し、返します。<br>
	 * パラメータで指定される SQL 文にはプレースホルダ '?' を含めることが可能です。
	 * @param sql プレースホルダを持つ SQL
	 * @return {@link BlenPreparedStatement} のインスタンス
	 */
	BlenPreparedStatement prepareStatement(String sql);

	/**
	 * {@link #prepareStatement(String)} の簡易実行メソッドです。
	 * @param sql プレースホルダを持つ SQL
	 * @param consumer {@link BlenPreparedStatement} を受け取る {@link Consumer}
	 */
	default void execute(String sql, Consumer<BlenPreparedStatement> consumer) {
		try (BlenPreparedStatement statement = prepareStatement(sql)) {
			consumer.accept(statement);
		}
	}

	/**
	 * {@link #prepareStatement(String)} の簡易実行メソッドです。
	 * @param sql プレースホルダを持つ SQL
	 * @param function {@link BlenPreparedStatement} を受け取る {@link Function}
	 * @return T
	 */
	default <T> T executeAndGet(String sql, Function<BlenPreparedStatement, T> function) {
		try (BlenPreparedStatement statement = prepareStatement(sql)) {
			return function.apply(statement);
		}
	}

	/**
	 * {@link BatchStatement} のインスタンスを生成し、返します。
	 * @return {@link BatchStatement} のインスタンス
	 */
	BatchStatement getBatchStatement();

	/**
	 * パラメータで指定された名称を、データベースで使用される標準的な名前に変換します。
	 * @param name 標準化前の名称
	 * @return 標準化された名称
	 */
	String regularize(String name);

	/**
	 * {@link PreparedStatementWrapper} を設定し、この接続が生成する {@link BlenPreparedStatement} をラップさせます。
	 * @param wrapper この接続が生成する {@link BlenPreparedStatement} のラッパー
	 */
	void setPreparedStatementWrapper(PreparedStatementWrapper wrapper);

	/**
	 * {@link BatchStatementWrapper} を設定し、この接続が生成する {@link BatchStatement} をラップさせます。
	 * @param wrapper この接続が生成する {@link BatchStatement} のラッパー
	 */
	void setBatchStatementWrapper(BatchStatementWrapper wrapper);
}
