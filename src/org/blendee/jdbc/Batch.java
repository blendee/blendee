package org.blendee.jdbc;

/**
 * SQL 文による更新をバッチ実行するためのオブジェクトを表すインターフェイスです。
 * @author 千葉 哲嗣
 * @see BConnection#getBatch()
 */
public interface Batch extends AutoCloseable {

	/**
	 * バッチ更新を行う SQL 文を追加します。<br>
	 * パラメータで指定される SQL 文にはプレースホルダ '?' を使用することはできません。
	 * @param sql SQL更新文
	 */
	void add(String sql);

	/**
	 * バッチ更新を行う SQL 文を追加します。<br>
	 * パラメータで指定される SQL 文にはプレースホルダ '?' を含めることが可能です。
	 * @param sql SQL更新文
	 * @param complementer プレースホルダに結びつける値を持つ
	 */
	void add(String sql, PreparedStatementComplementer complementer);

	/**
	 * バッチ更新を行う SQL 文を追加します。<br>
	 * @param sql SQL更新文
	 */
	default void add(ComposedSQL sql) {
		add(sql.sql(), sql);
	}

	/**
	 * 溜められた SQL 文を実行します。
	 * @return 実行結果件数の配列
	 */
	int[] execute();

	/**
	 * SQL 文を溜める閾値を設定します。<br>
	 * ここで設定された閾値を超える SQL 文が追加されると、内部で {@link #execute()} が実行されます。
	 * @param threshold SQL 文の閾値
	 */
	void setThreshold(int threshold);

	/**
	 * このステートメントを閉じます。
	 */
	@Override
	void close();
}
