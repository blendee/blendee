package org.blendee.jdbc;

import java.sql.SQLException;

/**
 * Blendee が内部で保持する JDBC のオブジェクトにアクセスするためのインターフェイスです。
 * @param <T> JDBC クラス
 */
@FunctionalInterface
public interface JDBCBorrower<T> {

	/**
	 * JDBC オブジェクトを借り受けます。
	 * @param jdbcObject JDBC の各インスタンス
	 * @throws SQLException JDBC から投げられた例外
	 */
	void accept(T jdbcObject) throws SQLException;
}
