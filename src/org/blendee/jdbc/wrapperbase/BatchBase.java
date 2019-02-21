package org.blendee.jdbc.wrapperbase;

import org.blendee.jdbc.Batch;
import org.blendee.jdbc.PreparedStatementComplementer;

/**
 * {@link Batch} のラッパーを実装するベースとなる、抽象基底クラスです。
 * @author 千葉 哲嗣
 */
public abstract class BatchBase implements Batch {

	/**
	 * ベースとなるインスタンスを返します。
	 * @return ベースとなるインスタンス
	 */
	protected abstract Batch base();

	@Override
	public void add(String sql) {
		base().add(sql);
	}

	@Override
	public void add(String sql, PreparedStatementComplementer complementer) {
		base().add(sql, complementer);
	}

	@Override
	public int[] execute() {
		return base().execute();
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
