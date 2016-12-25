package jp.ats.blendee.jdbc.wrapperbase;

import jp.ats.blendee.jdbc.BatchStatement;
import jp.ats.blendee.jdbc.BatchStatementWrapper;
import jp.ats.blendee.jdbc.BConnection;
import jp.ats.blendee.jdbc.BPreparedStatement;
import jp.ats.blendee.jdbc.BStatement;
import jp.ats.blendee.jdbc.PreparedStatementComplementer;
import jp.ats.blendee.jdbc.PreparedStatementWrapper;

/**
 * {@link BConnection} のラッパーを実装するベースとなる、抽象基底クラスです。
 *
 * @author 千葉 哲嗣
 */
public abstract class ConnectionBase extends MetadataBase implements BConnection {

	private final BConnection base;

	/**
	 * ラップするインスタンスを受け取るコンストラクタです。
	 *
	 * @param base ベースとなるインスタンス
	 */
	protected ConnectionBase(BConnection base) {
		super(base);
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
