package org.blendee.jdbc;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
	 * {@link Stream} に変換します。
	 * @return {@link Stream}
	 */
	default Stream<Result> stream() {

		boolean[] hasNext = { false };

		boolean[] nexted = { false };

		var iterator = new Iterator<Result>() {

			@Override
			public boolean hasNext() {
				hasNext[0] = BResultSet.this.next();

				var hasNextCurrent = hasNext[0];

				nexted[0] = true;

				if (!hasNextCurrent) close();

				return hasNextCurrent;
			}

			@Override
			public Result next() {
				if (!nexted[0]) hasNext();

				if (!hasNext[0]) throw new NoSuchElementException();

				nexted[0] = false;

				return BResultSet.this;
			}
		};

		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
	}

	/**
	 * このクラスのインスタンスが内部に {@link ResultSet} を持つ場合、それを貸します。
	 * @param borrower 借り手
	 */
	void lend(JDBCBorrower<ResultSet> borrower);
}
