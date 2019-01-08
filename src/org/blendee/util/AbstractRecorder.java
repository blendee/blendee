package org.blendee.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.function.Supplier;

import org.blendee.assist.Query;
import org.blendee.assist.SelectStatement;
import org.blendee.sql.Binder;
import org.blendee.sql.Reproducible;

/**
 * 一度生成した SQL をキャッシュし、次回実行時にその SQL を使用することで処理を高速化するためのクラスです。
 * @author 千葉 哲嗣
 */
public abstract class AbstractRecorder {

	private final Map<Class<?>, Reproducer> executorCache = new HashMap<>();

	private final Map<Class<?>, Map<?, Reproducer>> executorMapCache = new HashMap<>();

	/**
	 * {@link SelectStatement} から {@link Query} を生成する処理を実行します。<br>
	 * 既に一度 {@link Query} が生成されていた場合、新たに {@link Query} を生成せず、以前の {@link Query} を返します。<br>
	 * 以前の {@link Query} を返す場合、プレースホルダにセットする値として、引数の playbackPlaceHolderValues を使用します。
	 * @param supplier {@link Query} を生成する処理
	 * @param playbackPlaceHolderValues supplier で使用した {@link Placeholder} に、再実行時セットする値
	 * @param <E> {@link Reproducible} の実装
	 * @return {@link Reproducible}
	 */
	public <E extends Reproducible<E>> E play(Supplier<E> supplier, Object... playbackPlaceHolderValues) {
		return play(supplier, lock(), playbackPlaceHolderValues);
	}

	/**
	 * {@link SelectStatement} から {@link Query} を生成する処理を実行します。<br>
	 * 既に一度 {@link Query} が生成されていた場合、新たに {@link Query} を生成せず、以前の {@link Query} を返します。<br>
	 * 以前の {@link Query} を返す場合、プレースホルダにセットする値として、引数の playbackPlaceHolderValues を使用します。<br>
	 * decision により、作成されるクエリを複数タイプキャッシュすることが可能です。<br>
	 * decision が返す結果は、{@link HashMap} のキーとして使用されるので、キーの要件を満たす必要があります。
	 * @param decision 使用するクエリを判定し、それを知らせる結果を返す {@link Supplier}
	 * @param supplier decision の結果を受け取り、状況にあった {@link Query} を生成する処理
	 * @param playbackPlaceHolderValuesSupplier decision の結果を受け取り、 supplier で使用した {@link Placeholder} に、再実行時セットする値
	 * @param <R> 条件判定のための材料の型
	 * @param <E> {@link Reproducible} の実装
	 * @return {@link Reproducible}
	 */
	public <R, E extends Reproducible<E>> E play(
		Supplier<R> decision,
		Function<R, E> supplier,
		Function<R, Object[]> playbackPlaceHolderValuesSupplier) {
		return play(decision, supplier, playbackPlaceHolderValuesSupplier, lock());
	}

	/**
	 * @return {@link Lock}
	 */
	protected abstract Lock lock();

	@SuppressWarnings("unchecked")
	private <E extends Reproducible<E>> E play(Supplier<E> supplier, Lock lock, Object... playbackPlaceHolderValues) {
		Class<?> lambdaClass = supplier.getClass();

		Reproducer reproducer = null;

		lock.lock();
		try {
			if ((reproducer = executorCache.get(lambdaClass)) == null) {
				E reproducible = supplier.get();
				try {
					Placeholder.start();
					//ここで初めてSQLをreproduceし、Placeholderの位置を記録
					reproducer = new Reproducer(reproducible.reproduce());
				} finally {
					Placeholder.remove();
				}

				executorCache.put(lambdaClass, reproducer);
			}
		} finally {
			lock.unlock();
		}

		return (E) reproducer.reproduce(playbackPlaceHolderValues);
	}

	@SuppressWarnings("unchecked")
	private <R, E extends Reproducible<E>> E play(
		Supplier<R> decision,
		Function<R, E> supplier,
		Function<R, Object[]> playbackPlaceHolderValuesSupplier,
		Lock lock) {
		Class<?> lambdaClass = supplier.getClass();

		R result = decision.get();

		Object[] values = playbackPlaceHolderValuesSupplier.apply(result);

		Reproducer reproducer = null;

		lock.lock();
		try {
			Map<R, Reproducer> map = (Map<R, Reproducer>) executorMapCache.get(lambdaClass);

			if (map == null) {
				map = new HashMap<>();
				executorMapCache.put(lambdaClass, map);
			}

			reproducer = map.get(result);
			if (reproducer == null) {
				E reproducible = supplier.apply(result);
				try {
					Placeholder.start();
					//ここで初めてSQLをreproduceし、Placeholderの位置を記録
					reproducer = new Reproducer(reproducible.reproduce());
				} finally {
					Placeholder.remove();
				}

				map.put(result, reproducer);
			}
		} finally {
			lock.unlock();
		}

		return (E) reproducer.reproduce(values);
	}

	private static class Reproducer {

		private final Reproducible<?> reproducible;

		private final Object[] values;

		private final int[] placeholderIndexes;

		private Reproducer(Reproducible<?> reproducible) {
			this.reproducible = reproducible;

			List<Integer> indexes = Placeholder.getIndexes();

			Binder[] binders = reproducible.currentBinders().clone();
			values = new Object[binders.length];
			for (int i = 0; i < binders.length; i++) {
				values[i] = binders[i].getValue();
			}

			placeholderIndexes = new int[indexes.size()];
			for (int i = 0; i < placeholderIndexes.length; i++) {
				placeholderIndexes[i] = indexes.get(i) - 1;
			}
		}

		private Reproducible<?> reproduce(Object[] newValues) {
			if (newValues.length != placeholderIndexes.length)
				throw new IllegalStateException("値の数は " + placeholderIndexes.length + " である必要があります");

			Object[] clone = values.clone();

			for (int i = 0; i < newValues.length; i++) {
				clone[placeholderIndexes[i]] = newValues[i];
			}

			return reproducible.reproduce(clone);
		}
	}
}
