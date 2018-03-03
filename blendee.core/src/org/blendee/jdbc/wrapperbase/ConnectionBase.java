package org.blendee.jdbc.wrapperbase;

import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.BatchStatementWrapper;
import org.blendee.jdbc.BlenConnection;
import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.jdbc.BlenStatement;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.jdbc.PreparedStatementWrapper;

/**
 * {@link BlenConnection} のラッパーを実装するベースとなる、抽象基底クラスです。
 * @author 千葉 哲嗣
 */
public abstract class ConnectionBase extends MetadataBase implements BlenConnection {

	private final BlenConnection base;

	/**
	 * ラップするインスタンスを受け取るコンストラクタです。
	 * @param base ベースとなるインスタンス
	 */
	protected ConnectionBase(BlenConnection base) {
		super(base);
		this.base = base;
	}

	@Override
	public BlenStatement getStatement(String sql) {
		return base.getStatement(sql);
	}

	@Override
	public BlenStatement getStatement(String sql, PreparedStatementComplementer complementer) {
		return base.getStatement(sql, complementer);
	}

	@Override
	public BlenPreparedStatement prepareStatement(String sql) {
		return base.prepareStatement(sql);
	}

	@Override
	public BatchStatement getBatchStatement() {
		return base.getBatchStatement();
	}

	@Override
	public String regularize(String name) {
		return base.regularize(name);
	}

	@Override
	public void setPreparedStatementWrapper(PreparedStatementWrapper wrapper) {
		base.setPreparedStatementWrapper(wrapper);
	}

	@Override
	public void setBatchStatementWrapper(BatchStatementWrapper wrapper) {
		base.setBatchStatementWrapper(wrapper);
	}
}
