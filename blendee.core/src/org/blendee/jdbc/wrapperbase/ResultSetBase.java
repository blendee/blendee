package org.blendee.jdbc.wrapperbase;

import org.blendee.jdbc.BResultSet;

/**
 * {@link BResultSet} のラッパーを実装するベースとなる、抽象基底クラスです。
 *
 * @author 千葉 哲嗣
 */
public abstract class ResultSetBase extends ResultBase implements BResultSet {

	private final BResultSet base;

	/**
	 * ラップするインスタンスを受け取るコンストラクタです。
	 *
	 * @param base ベースとなるインスタンス
	 */
	protected ResultSetBase(BResultSet base) {
		super(base);
		this.base = base;
	}

	@Override
	public boolean next() {
		return base.next();
	}

	@Override
	public void close() {
		base.close();
	}
}
