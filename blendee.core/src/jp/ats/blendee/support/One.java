package jp.ats.blendee.support;

import jp.ats.blendee.internal.U;

/**
 * {@link DTO} 一件と同一であり、それを参照する {@link DTO} も併せ持ちます。
 *
 * @author 千葉 哲嗣
 *
 * @param <O> One　一対多の一側の型
 * @param <M> Many　一対多の多側の型連鎖
 */
public class One<O extends DTO, M> {

	private final O oneself;

	private final M many;

	One(O oneself, M many) {
		this.oneself = oneself;
		this.many = many;
	}

	/**
	 * このインスタンスと同一の {@link DTO} を返します。
	 *
	 * @return {@link DTO}
	 */
	public O oneself() {
		return oneself;
	}

	/**
	 * このインスタンスを参照しているテーブルの {@link DTO} と、さらに参照している {@link DTO} を返します。
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
