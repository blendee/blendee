package org.blendee.jdbc.wrapperbase;

import java.sql.Connection;

import org.blendee.jdbc.BConnection;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.JDBCBorrower;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.jdbc.StatementWrapper;

/**
 * {@link BConnection} のラッパーを実装するベースとなる、抽象基底クラスです。
 * @author 千葉 哲嗣
 */
public abstract class ConnectionBase implements BConnection {

	/**
	 * ベースとなるインスタンスを返します。
	 * @return ベースとなるインスタンス
	 */
	protected abstract BConnection base();

	@Override
	public BStatement getStatement(String sql) {
		return base().getStatement(sql);
	}

	@Override
	public BStatement getStatement(String sql, PreparedStatementComplementer complementer) {
		return base().getStatement(sql, complementer);
	}

	@Override
	public BPreparedStatement prepareStatement(String sql) {
		return base().prepareStatement(sql);
	}

	@Override
	public BatchStatement getBatchStatement() {
		return base().getBatchStatement();
	}

	@Override
	public void setStatementWrapper(StatementWrapper wrapper) {
		base().setStatementWrapper(wrapper);
	}

	@Override
	public void lend(JDBCBorrower<Connection> borrower) {
		base().lend(borrower);
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
}
