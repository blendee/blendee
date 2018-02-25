package org.blendee.jdbc;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.blendee.internal.U;
import org.blendee.jdbc.BlenConnection;
import org.blendee.jdbc.BlenResultSet;
import org.blendee.jdbc.BlenStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.jdbc.Result;

/**
 * {@link BlenResultSet} を {@link Iterator} として使用するためのクラスです。
 * @author 千葉 哲嗣
 */
public class ResultSetIterator implements Iterator<Result>, Iterable<Result>, AutoCloseable {

	private final BlenStatement statement;

	private final BlenResultSet result;

	private boolean hasNext = false;

	private boolean nexted;

	/**
	 * ベースとなる結果セットを使用し、インスタンスを生成します。
	 * @param sql 
	 * @param complementer 
	 */
	public ResultSetIterator(String sql, PreparedStatementComplementer complementer) {
		BlenConnection connection = ContextManager.get(BlendeeManager.class).getConnection();
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
		return hasNext;
	}

	@Override
	public Result next() {
		if (!nexted) hasNext();
		if (!hasNext) throw new NoSuchElementException();

		nexted = false;

		return result;
	}

	/**
	 * @throws UnsupportedOperationException 使用不可
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws Exception {
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
