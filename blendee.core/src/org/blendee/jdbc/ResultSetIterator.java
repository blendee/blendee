package org.blendee.jdbc;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.blendee.internal.U;

/**
 * {@link BResultSet} を {@link Iterator} として使用するためのクラスです。
 * @author 千葉 哲嗣
 */
public class ResultSetIterator implements Iterator<Result>, Iterable<Result>, AutoCloseable {

	private final BStatement statement;

	private final BResultSet result;

	private boolean hasNext = false;

	private boolean nexted;

	/**
	 * ベースとなる結果セットを使用し、インスタンスを生成します。
	 * @param sql {@link ComposedSQL}
	 */
	public ResultSetIterator(ComposedSQL sql) {
		BConnection connection = BlendeeManager.getConnection();
		statement = connection.getStatement(sql);
		result = statement.executeQuery();
	}

	/**
	 * ベースとなる結果セットを使用し、インスタンスを生成します。
	 * @param sql SQL 文
	 * @param complementer {@link PreparedStatementComplementer}
	 */
	public ResultSetIterator(String sql, PreparedStatementComplementer complementer) {
		BConnection connection = BlendeeManager.getConnection();
		statement = connection.getStatement(sql, complementer);
		result = statement.executeQuery();
	}

	@Override
	public Iterator<Result> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		hasNext = result.next();

		nexted = true;

		if (!hasNext) close();

		return hasNext;
	}

	@Override
	public Result next() {
		if (!nexted) hasNext();

		if (!hasNext) throw new NoSuchElementException();

		nexted = false;

		return result;
	}

	@Override
	public void close() {
		try {
			result.close();
		} finally {
			statement.close();
		}
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
