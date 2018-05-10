package org.blendee.support;

/**
 * 自動生成された {@link Query} の reuse 処理で使用する Function をまとめたクラスです。
 * @author 千葉 哲嗣
 */
public class ReuseFunctions {

	interface ReuseFunction<Q extends Query, R> {

		R apply(Q value);
	}

	/**
	 * 通常の {@link Executor} 用
	 * @param <Q> {@link Query}
	 * @param <R> {@link Executor}
	 */
	@FunctionalInterface
	public interface ExecutorFunction<Q extends Query, R extends Executor<?, ?>> extends ReuseFunction<Q, R> {}

	/**
	 * intercept された {@link Executor} 用
	 * @param <Q> {@link Query}
	 * @param <R> {@link Executor}
	 */
	@FunctionalInterface
	public interface O2MExecutorFunction<Q extends Query, R extends Executor<?, ?>> extends ReuseFunction<Q, R> {}

	/**
	 * intercept された {@link Aggregator} 用
	 * @param <Q> {@link Query}
	 * @param <R> {@link Aggregator}
	 */
	@FunctionalInterface
	public interface AggregatorFunction<Q extends Query, R extends Aggregator> extends ReuseFunction<Q, R> {}
}
