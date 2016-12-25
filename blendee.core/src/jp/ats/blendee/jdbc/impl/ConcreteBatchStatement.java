package jp.ats.blendee.jdbc.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.BatchStatement;
import jp.ats.blendee.jdbc.PreparedStatementComplementer;

/**
 * @author 千葉 哲嗣
 */
class ConcreteBatchStatement implements BatchStatement {

	private static final PreparedStatementComplementer nullComplementer = statement -> {
		return 0;
	};

	private final ConcreteConnection connection;

	private final Map<String, BatchResultHolder> batchMap = new HashMap<>();

	private int counter = 0;

	private int batchOrder = 0;

	private int threshold = Integer.MAX_VALUE;

	private int[] currentResults = new int[0];

	ConcreteBatchStatement(ConcreteConnection connection) {
		this.connection = connection;
	}

	@Override
	public void addBatch(String sql) {
		addBatch(sql, nullComplementer);
	}

	@Override
	public void addBatch(String sql, PreparedStatementComplementer complementer) {
		if (isThresholdCrossed()) flushBatch();
		BatchResultHolder holder = batchMap.get(sql);
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
		complementer.complement(connection.wrapInternal(statement));
		statement.addBatch();
	}

	@Override
	public int[] executeBatch() {
		flushBatch();
		batchOrder = 0;
		int[] results = currentResults;
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
		int[] results = new int[batchOrder];
		if (currentResults.length > 0)
			System.arraycopy(currentResults, 0, results, 0, currentResults.length);

		for (Entry<String, BatchResultHolder> entry : batchMap.entrySet()) {
			BatchResultHolder holder = entry.getValue();
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
			int[] results;
			results = statement.executeBatch();
			for (int i = 0; i < results.length; i++) {
				int globalOrder = orders.get(i);
				allResults[globalOrder] = results[i];
			}
		}

		private BatchPreparedStatement getStatement() {
			return statement;
		}
	}
}
