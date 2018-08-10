package org.blendee.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.blendee.support.Query;
import org.blendee.support.QueryBuilder;

/**
 * 一度生成した SQL をキャッシュし、次回実行時にその SQL を使用することで処理を高速化するためのクラスです。
 * @author 千葉 哲嗣
 */
public class Recorder {

	private static final Map<Class<?>, Query<?, ?>> executorCache = new HashMap<>();

	private static final Map<Class<?>, Map<?, Query<?, ?>>> executorMapCache = new HashMap<>();

	/**
	 * {@link QueryBuilder} から {@link Query} を生成する処理を実行します。<br>
	 * 既に一度 {@link Query} が生成されていた場合、新たに {@link Query} を生成せず、以前の {@link Query} を返します。<br>
	 * 以前の {@link Query} を返す場合、プレースホルダにセットする値として、引数の playbackPlaceHolderValues を使用します。
	 * @param supplier {@link Query} を生成する処理
	 * @param playbackPlaceHolderValues 再実行時のプレースホルダにセットする値
	 * @return {@link Query}
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Query<?, ?>> E play(Supplier<E> supplier, Object... playbackPlaceHolderValues) {
		Class<?> lambdaClass = supplier.getClass();

		Query<?, ?> executor = null;
		synchronized (executorCache) {
			if ((executor = executorCache.get(lambdaClass)) == null) {
				executor = supplier.get().reproduce(playbackPlaceHolderValues);
				executorCache.put(lambdaClass, executor);

				return (E) executor;
			}
		}

		return (E) executor.reproduce(playbackPlaceHolderValues);
	}

	/**
	 * {@link QueryBuilder} から {@link Query} を生成する処理を実行します。<br>
	 * 既に一度 {@link Query} が生成されていた場合、新たに {@link Query} を生成せず、以前の {@link Query} を返します。<br>
	 * 以前の {@link Query} を返す場合、プレースホルダにセットする値として、引数の playbackPlaceHolderValues を使用します。<br>
	 * decision により、作成されるクエリを複数タイプキャッシュすることが可能です。<br>
	 * decision が返す結果は、{@link HashMap} のキーとして使用されるので、キーの要件を満たす必要があります。
	 * @param decision 使用するクエリを判定し、それを知らせる結果を返す {@link Supplier}
	 * @param supplier decision の結果を受け取り、状況にあった {@link Query} を生成する処理
	 * @param playbackPlaceHolderValuesSupplier decision の結果を受け取り、再実行時のプレースホルダにセットする値を生成する処理
	 * @return {@link Query}
	 */
	@SuppressWarnings("unchecked")
	public static <R, E extends Query<?, ?>> E play(
		Supplier<R> decision,
		Function<R, E> supplier,
		Function<R, Object[]> playbackPlaceHolderValuesSupplier) {
		Class<?> lambdaClass = supplier.getClass();

		R result = decision.get();

		Object[] values = playbackPlaceHolderValuesSupplier.apply(result);

		Query<?, ?> executor = null;
		synchronized (executorMapCache) {
			Map<R, Query<?, ?>> map = (Map<R, Query<?, ?>>) executorMapCache.get(lambdaClass);

			if (map == null) {
				map = new HashMap<>();
				executorMapCache.put(lambdaClass, map);
			}

			executor = map.get(result);
			if (executor == null) {
				executor = supplier.apply(result).reproduce(values);
				map.put(result, executor);

				return (E) executor;
			}
		}

		return (E) executor.reproduce(values);
	}
}
