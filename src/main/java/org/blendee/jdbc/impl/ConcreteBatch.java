package org.blendee.jdbc.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.blendee.internal.U;
import org.blendee.jdbc.Batch;
import org.blendee.jdbc.PreparedStatementComplementer;

/**
 * @author 千葉 哲嗣
 */
class ConcreteBatch implements Batch {

	private static final PreparedStatementComplementer nullComplementer = statement -> {
	};

	private final ConcreteConnection connection;

	private final Map<String, BatchResultHolder> batchMap = new HashMap<>();

	private int counter = 0;

	private int batchOrder = 0;

	private int threshold = Integer.MAX_VALUE;

	private int[] currentResults = new int[0];

	ConcreteBatch(ConcreteConnection connection) {
		this.connection = connection;
	}

	@Override
	public void add(String sql) {
		add(sql, nullComplementer);
	}

	@Override
	public void add(String sql, PreparedStatementComplementer complementer) {
		if (isThresholdCrossed()) flushBatch();
		var holder = batchMap.get(sql);
		BatchPreparedStatement statement;
		if (holder == null) {
			statement = connection.createForBatch(sql);
			holder = new BatchResultHolder(statement);
			batchMap.put(sql, holder);
		} else {
			statement = holder.getStatement();
		}

		counter++;
		holder.addGlobalOrder(batchOrder++);
		complementer.complement(connection.wrap(statement));
		statement.addBatch();
	}

	@Override
	public int[] execute() {
		flushBatch();
		batchOrder = 0;
		var results = currentResults;
		currentResults = new int[0];
		return results;
	}

	@Override
	public void setThreshold(int threshold) {
		this.threshold = threshold;
		if (isThresholdCrossed()) flushBatch();
	}

	@Override
	public void close() {
		for (BatchResultHolder holder : batchMap.values()) {
			holder.statement.close();
		}
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private boolean isThresholdCrossed() {
		return counter >= threshold;
	}

	private void flushBatch() {
		var results = new int[batchOrder];
		if (currentResults.length > 0)
			System.arraycopy(currentResults, 0, results, 0, currentResults.length);

		for (var entry : batchMap.entrySet()) {
			var holder = entry.getValue();
			holder.executeBatch(results);
		}

		batchMap.clear();
		counter = 0;
		currentResults = results;
	}

	private class BatchResultHolder {

		private final BatchPreparedStatement statement;

		private final List<Integer> orders = new ArrayList<>();

		private BatchResultHolder(BatchPreparedStatement statement) {
			this.statement = statement;
		}

		private void addGlobalOrder(int order) {
			orders.add(order);
		}

		private void executeBatch(int[] allResults) {
			var results = statement.executeBatch();
			for (var i = 0; i < results.length; i++) {
				var globalOrder = orders.get(i);
				allResults[globalOrder] = results[i];
			}
		}

		private BatchPreparedStatement getStatement() {
			return statement;
		}
	}
}
