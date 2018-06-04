package org.blendee.support;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 一度生成した SQL をキャッシュし、次回実行時にその SQL を使用することで処理を高速化するためのクラスです。
 * @author 千葉 哲嗣
 */
public class Recorder {

	private static final Map<Class<?>, Executor<?, ?>> executorCache = new HashMap<>();

	/**
	 * {@link Query} から {@link Executor} を生成する処理を実行します。<br>
	 * 既に一度 {@link Executor} が生成されていた場合、新たに {@link Executor} を生成せず、以前の {@link Executor} を返します。<br>
	 * 以前の {@link Executor} を返す場合、プレースホルダにセットする値として、引数の playbackPlaceHolderValues を使用します。
	 * @param supplier {@link Executor} を生成する処理
	 * @param playbackPlaceHolderValues 再実行時のプレースホルダにセットする値
	 * @return {@link Executor}
	 */
	public static <T extends Executor<?, ?>> T play(Supplier<T> supplier, Object... playbackPlaceHolderValues) {
		return play(true, supplier, playbackPlaceHolderValues);
	}

	/**
	 * {@link Query} から {@link Executor} を生成する処理を実行します。<br>
	 * 既に一度 {@link Executor} が生成されていた場合、新たに {@link Executor} を生成せず、以前の {@link Executor} を返します。<br>
	 * 以前の {@link Executor} を返す場合、プレースホルダにセットする値として、引数の playbackPlaceHolderValues を使用します。<br>
	 * canPlayback により、キャッシュの使用を制御できます。
	 * @param canPlayback false の場合、強制的に supplier から {@link Executor} を生成する
	 * @param supplier {@link Executor} を生成する処理
	 * @param playbackPlaceHolderValues 再実行時のプレースホルダにセットする値
	 * @return {@link Executor}
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Executor<?, ?>> T play(
		boolean canPlayback,
		Supplier<T> supplier,
		Object... playbackPlaceHolderValues) {
		Class<?> lambdaClass = supplier.getClass();

		Executor<?, ?> executor = null;
		synchronized (executorCache) {
			if (!canPlayback || (executor = executorCache.get(lambdaClass)) == null) {
				executor = supplier.get().reproduce(playbackPlaceHolderValues);
				executorCache.put(lambdaClass, executor);

				return (T) executor;
			}
		}

		return (T) executor.reproduce(playbackPlaceHolderValues);
	}
}
