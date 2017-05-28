package org.blendee.support;

import org.blendee.internal.U;

/**
 * {@link BEntity} 一件と同一であり、それを参照する {@link BEntity} も併せ持ちます。
 *
 * @author 千葉 哲嗣
 *
 * @param <O> One　一対多の一側の型
 * @param <M> Many　一対多の多側の型連鎖
 */
public class One<O extends BEntity, M> {

	private final O oneself;

	private final M many;

	One(O oneself, M many) {
		this.oneself = oneself;
		this.many = many;
	}

	/**
	 * このインスタンスと同一の {@link BEntity} を返します。
	 *
	 * @return {@link BEntity}
	 */
	public O oneself() {
		return oneself;
	}

	/**
	 * このインスタンスを参照しているテーブルの {@link BEntity} と、さらに参照している {@link BEntity} を返します。
	 *
	 * @return {@link Many}
	 */
	public M many() {
		return many;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
