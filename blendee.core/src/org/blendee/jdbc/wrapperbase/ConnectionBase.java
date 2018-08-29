package org.blendee.jdbc.wrapperbase;

import java.sql.Connection;

import org.blendee.jdbc.BConnection;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.BatchStatementWrapper;
import org.blendee.jdbc.JDBCBorrower;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.jdbc.PreparedStatementWrapper;

/**
 * {@link BConnection} のラッパーを実装するベースとなる、抽象基底クラスです。
 * @author 千葉 哲嗣
 */
public abstract class ConnectionBase implements BConnection {

	private final BConnection base;

	/**
	 * ラップするインスタンスを受け取るコンストラクタです。
	 * @param base ベースとなるインスタンス
	 */
	protected ConnectionBase(BConnection base) {
		this.base = base;
	}

	@Override
	public BStatement getStatement(String sql) {
		return base.getStatement(sql);
	}

	@Override
	public BStatement getStatement(String sql, PreparedStatementComplementer complementer) {
		return base.getStatement(sql, complementer);
	}

	@Override
	public BPreparedStatement prepareStatement(String sql) {
		return base.prepareStatement(sql);
	}

	@Override
	public BatchStatement getBatchStatement() {
		return base.getBatchStatement();
	}

	@Override
	public void setPreparedStatementWrapper(PreparedStatementWrapper wrapper) {
		base.setPreparedStatementWrapper(wrapper);
	}

	@Override
	public void setBatchStatementWrapper(BatchStatementWrapper wrapper) {
		base.setBatchStatementWrapper(wrapper);
	}

	@Override
	public void lend(JDBCBorrower<Connection> borrower) {
		base.lend(borrower);
	}
}
