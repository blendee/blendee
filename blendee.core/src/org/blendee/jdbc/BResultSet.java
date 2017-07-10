package org.blendee.jdbc;

import java.sql.ResultSet;

/**
 * {@link ResultSet} に似せ、機能を制限したインターフェイスです。
 * @author 千葉 哲嗣
 * @see BStatement#executeQuery()
 */
public interface BResultSet extends AutoCloseable, BResult {

	/**
	 * 検索結果のカーソルを次の行へ移動します。
	 * @return 次の行が存在する場合、 true
	 */
	boolean next();

	/**
	 * 検索結果を閉じます。
	 */
	@Override
	void close();
}
