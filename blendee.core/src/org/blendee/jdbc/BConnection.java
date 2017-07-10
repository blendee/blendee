package org.blendee.jdbc;

import java.sql.Connection;
import java.util.function.Consumer;

/**
 * {@link Connection} に似せ、機能を制限したインターフェイスです。
 * @author 千葉 哲嗣
 * @see BlendeeManager#getConnection()
 */
public interface BConnection extends Metadata {

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
	default void statement(String sql, Consumer<BStatement> consumer) {
		try (BStatement statement = getStatement(sql)) {
			consumer.accept(statement);
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
	 * {@link #getStatement(String, PreparedStatementComplementer)} の簡易実行メソッドです。
	 * @param sql プレースホルダを持つ SQL
	 * @param complementer プレースホルダに結びつける値を持つ
	 * @param consumer {@link BStatement} を受け取る {@link Consumer}
	 */
	default void statement(
		String sql,
		PreparedStatementComplementer complementer,
		Consumer<BStatement> consumer) {
		try (BStatement statement = getStatement(sql, complementer)) {
			consumer.accept(statement);
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
	 * {@link PreparedStatementWrapper} を設定し、この接続が生成する {@link BPreparedStatement} をラップさせます。
	 * @param wrapper この接続が生成する {@link BPreparedStatement} のラッパー
	 */
	void setPreparedStatementWrapper(PreparedStatementWrapper wrapper);

	/**
	 * {@link BatchStatementWrapper} を設定し、この接続が生成する {@link BatchStatement} をラップさせます。
	 * @param wrapper この接続が生成する {@link BatchStatement} のラッパー
	 */
	void setBatchStatementWrapper(BatchStatementWrapper wrapper);
}
