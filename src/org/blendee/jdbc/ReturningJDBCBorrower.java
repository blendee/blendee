package org.blendee.jdbc;

import java.sql.SQLException;

/**
 * Blendee が内部で保持する JDBC のオブジェクトにアクセスするためのインターフェイスです。
 * @param <T> JDBC クラス
 * @param <R> 戻り値
 */
@FunctionalInterface
public interface ReturningJDBCBorrower<T, R> {

	/**
	 * JDBC オブジェクトを借り受けます。
	 * @param jdbcObject JDBC の各インスタンス
	 * @return 戻り値
	 * @throws SQLException JDBC から投げられた例外
	 */
	R apply(T jdbcObject) throws SQLException;
}
