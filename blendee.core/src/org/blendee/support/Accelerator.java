package org.blendee.support;

import org.blendee.jdbc.PreparedStatementComplementer;

@SuppressWarnings("javadoc")
public class Accelerator {

	public <E extends Executor<?, ?>> E evaluate(
		ExecutorFunction<E> function,
		PreparedStatementComplementer complementer) {
		return null;
	}

	public Aggregator evaluate(
		AggregatorFunction function,
		PreparedStatementComplementer complementer) {
		return null;
	}

	@FunctionalInterface
	public interface ExecutorFunction<E extends Executor<?, ?>> {

		E execute();
	}

	@FunctionalInterface
	public interface AggregatorFunction {

		Aggregator execute();
	}
}
