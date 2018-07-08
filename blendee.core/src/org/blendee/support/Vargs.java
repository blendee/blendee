package org.blendee.support;

/**
 * 可変パラメータの引数をパックするクラスです。
 * @author 千葉 哲嗣
 * @param <T>
 */
public class Vargs<T> {

	/**
	 * 可変パラメータの引数をパックします。
	 * @param args
	 * @return このクラスのインスタンス
	 */
	@SafeVarargs
	public static <T> Vargs<T> of(T... args) {
		return new Vargs<>(args);
	}

	private final T[] args;

	private Vargs(T[] args) {
		this.args = args;
	}

	/**
	 * パックされた引数を返します。
	 * @return args
	 */
	public T[] get() {
		return args;
	}

	/**
	 * 引数の個数を返します。
	 * @return length
	 */
	public int length() {
		return args.length;
	}
}
