package org.blendee.jdbc.wrapperbase;

import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.PreparedStatementComplementer;

/**
 * {@link BatchStatement} のラッパーを実装するベースとなる、抽象基底クラスです。
 * @author 千葉 哲嗣
 */
public abstract class BatchStatementBase implements BatchStatement {

	/**
	 * ベースとなるインスタンスを返します。
	 * @return ベースとなるインスタンス
	 */
	protected abstract BatchStatement base();

	@Override
	public void addBatch(String sql) {
		base().addBatch(sql);
	}

	@Override
	public void addBatch(String sql, PreparedStatementComplementer complementer) {
		base().addBatch(sql, complementer);
	}

	@Override
	public int[] executeBatch() {
		return base().executeBatch();
	}

	@Override
	public void setThreshold(int threshold) {
		base().setThreshold(threshold);
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
}
