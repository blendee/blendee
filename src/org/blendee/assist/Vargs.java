package org.blendee.assist;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * 可変パラメータの引数をパックするユーティリティクラスです。
 * @author 千葉 哲嗣
 * @param <T> 引数の型
 */
public class Vargs<T> {

	/**
	 * 可変パラメータの引数をパックします。
	 * @param args 引数
	 * @param <T> 引数の型
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

	/**
	 * {@link Stream} として返します。
	 * @return {@link Stream}
	 */
	public Stream<T> stream() {
		return Arrays.stream(args);
	}
}
