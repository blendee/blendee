package org.blendee.support;

import java.util.function.Consumer;
import java.util.function.Function;

import org.blendee.jdbc.BlenResultSet;
import org.blendee.jdbc.ResultSetIterator;

/**
 * 集約関数を使用した検索の実行メソッドを定義したインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface AggregateExecutor {

	/**
	 * 集合関数を含む検索を実行します。
	 * @param consumer {@link Consumer}
	 */
	void aggregate(Consumer<BlenResultSet> consumer);

	/**
	 * 集合関数を含む検索を実行します。
	 * @param function {@link Function}
	 * @return 任意の型の戻り値
	 */
	<T> T aggregateAndGet(Function<BlenResultSet, T> function);

	/**
	 * 集合関数を含む検索を実行します。
	 * @return {@link ResultSetIterator}
	 */
	ResultSetIterator aggregate();

	AggregateExecutor yield();
}
