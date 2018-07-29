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
	 * @param jdbcObject
	 * @throws SQLException
	 */
	void accept(T jdbcObject) throws SQLException;
}
