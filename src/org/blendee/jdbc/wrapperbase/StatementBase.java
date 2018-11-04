package org.blendee.jdbc.wrapperbase;

import java.sql.Statement;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.JDBCBorrower;

/**
 * {@link BPreparedStatement} のラッパーを実装するベースとなる、抽象基底クラスです。
 * @author 千葉 哲嗣
 */
public abstract class StatementBase implements BStatement {

	/**
	 * ベースとなるインスタンスを返します。
	 * @return ベースとなるインスタンス
	 */
	protected abstract BStatement base();

	@Override
	public BResultSet executeQuery() {
		return base().executeQuery();
	}

	@Override
	public int executeUpdate() {
		return base().executeUpdate();
	}

	@Override
	public boolean execute() {
		return base().execute();
	}

	@Override
	public BResultSet getResultSet() {
		return base().getResultSet();
	}

	@Override
	public int getUpdateCount() {
		return base().getUpdateCount();
	}

	@Override
	public boolean getMoreResults() {
		return base().getMoreResults();
	}

	@Override
	public void close() {
		base().close();
	}

	@Override
	public String toString() {
		return base().toString();
	}

	@Override
	public boolean equals(Object o) {
		return base().equals(o);
	}

	@Override
	public int hashCode() {
		return base().hashCode();
	}

	@Override
	public void lend(JDBCBorrower<Statement> borrower) {
		base().lend(borrower);
	}
}
