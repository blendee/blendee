package org.blendee.jdbc.wrapperbase;

import org.blendee.jdbc.BResultSet;

/**
 * {@link BResultSet} のラッパーを実装するベースとなる、抽象基底クラスです。
 * @author 千葉 哲嗣
 */
public abstract class ResultSetBase extends ResultBase implements BResultSet {

	/**
	 * ベースとなるインスタンスを返します。
	 * @return ベースとなるインスタンス
	 */
	@Override
	protected abstract BResultSet base();

	@Override
	public boolean next() {
		return base().next();
	}

	@Override
	public void close() {
		base().close();
	}
}
