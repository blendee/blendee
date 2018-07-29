package org.blendee.jdbc;

import java.sql.ResultSet;
import java.util.function.Consumer;

/**
 * {@link ResultSet} に似せ、機能を制限したインターフェイスです。
 * @author 千葉 哲嗣
 * @see BStatement#executeQuery()
 */
public interface BResultSet extends AutoCloseable, Result {

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

	/**
	 * 全件処理するための簡易メソッドです。
	 * @param consumer {@link Consumer}
	 */
	default void forEach(Consumer<Result> consumer) {
		try {
			while (next()) {
				consumer.accept(this);
			}
		} finally {
			close();
		}
	}

	/**
	 * このクラスのインスタンスが内部に {@link ResultSet} を持つ場合、それを貸します。
	 * @param borrower 借り手
	 */
	void lend(Borrower<ResultSet> borrower);
}
