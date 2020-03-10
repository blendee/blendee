package org.blendee.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 一度生成した SQL をキャッシュし、次回実行時にその SQL を使用することで処理を高速化するためのクラスです。
 * @author 千葉 哲嗣
 */
public abstract class Recorder {

	private final Map<Class<?>, Reproducer> executorCache = new HashMap<>();

	private final Map<Class<?>, Map<?, Reproducer>> executorMapCache = new HashMap<>();

	private static final Recorder syncRecorder = new SyncRecorder();

	/**
	 * 単一の SQL キャッシュをもつ {@link Recorder} を返します。<br>
	 * SQL キャッシュのアクセスは同期化されます。
	 * @return {@link Recorder}
	 */
	public static Recorder instance() {
		return syncRecorder;
	}

	/**
	 * インスタンス別の SQL キャッシュをもつ {@link Recorder} を新たに生成し返します。<br>
	 * SQL キャッシュのアクセスは同期化されません。
	 * @return {@link Recorder}
	 */
	public static Recorder newAsyncInstance() {
		return new AsyncRecorder();
	}

	/**
	 * {@link Reproducible} を生成する処理を実行します。<br>
	 * 既に一度 {@link Reproducible} が生成されていた場合、新たに {@link Reproducible} を生成せず、以前の {@link Reproducible} を返します。<br>
	 * 以前の {@link Reproducible} を返す場合、プレースホルダにセットする値として、引数の playbackPlaceHolderValues を使用します。
	 * また、プレースホルダを使用しているのに playbackPlaceHolderValues を指定しない場合、このメソッドが返すのは {@link Reproducible#reproduce(Object...)} をまだ行っていない（プレースホルダの値を持っていない） {@link Reproducible} となります。<br>
	 * @param supplier {@link Reproducible} を生成する処理
	 * @param playbackPlaceHolderValues supplier で使用した {@link Placeholder} に、再実行時セットする値
	 * @param <E> {@link Reproducible} の実装
	 * @return {@link Reproducible}
	 */
	@SuppressWarnings("unchecked")
	public <E extends Reproducible<E>> E play(Supplier<E> supplier, Object... playbackPlaceHolderValues) {
		if (playbackPlaceHolderValues.length == 0)
			return (E) prepare(supplier, lock()).reproduce();

		return (E) prepare(supplier, lock()).reproduce(playbackPlaceHolderValues);
	}

	/**
	 * {@link Reproducible} を生成する処理を実行します。<br>
	 * 既に一度 {@link Reproducible} が生成されていた場合、新たに {@link Reproducible} を生成せず、以前の {@link Reproducible} を返します。<br>
	 * 以前の {@link Reproducible} を返す場合、プレースホルダにセットする値として、引数の playbackPlaceHolderValues を使用します。
	 * また、プレースホルダを使用しているのに playbackPlaceHolderValues を指定しない場合、このメソッドが返すのは {@link Reproducible#reproduce(Object...)} をまだ行っていない（プレースホルダの値を持っていない） {@link Reproducible} となります。<br>
	 * @param keySupplier {@link Reproducible} をキャッシュする際のキーを返す処理
	 * @param supplier {@link Reproducible} を生成する処理
	 * @param playbackPlaceHolderValues supplier で使用した {@link Placeholder} に、再実行時セットする値
	 * @param <E> {@link Reproducible} の実装
	 * @return {@link Reproducible}
	 */
	public <R, E extends Reproducible<E>> E play(
		Supplier<R> keySupplier,
		Supplier<E> supplier,
		Object... playbackPlaceHolderValues) {
		return play(keySupplier, r -> supplier.get(), r -> playbackPlaceHolderValues, lock());
	}

	/**
	 * {@link Reproducible} を生成する処理を実行します。<br>
	 * 既に一度 {@link Reproducible} が生成されていた場合、新たに {@link Reproducible} を生成せず、以前の {@link Reproducible} を返します。<br>
	 * 以前の {@link Reproducible} を返す場合、プレースホルダにセットする値として、引数の playbackPlaceHolderValues を使用します。<br>
	 * decision により、作成されるクエリを複数タイプキャッシュすることが可能です。<br>
	 * decision が返す結果は、{@link HashMap} のキーとして使用されるので、キーの要件を満たす必要があります。
	 * @param decision 使用するクエリを判定し、それを知らせる結果を返す {@link Supplier}
	 * @param supplier decision の結果を受け取り、状況にあった {@link Reproducible} を生成する処理
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
	 * SQL キャッシュをクリアします。
	 */
	public void clearCache() {
		Lock lock = lock();
		lock.lock();
		try {
			executorCache.clear();
			executorMapCache.clear();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @return {@link Lock}
	 */
	protected abstract Lock lock();

	private <E extends Reproducible<E>> Reproducer prepare(Supplier<E> supplier, Lock lock) {
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

		return reproducer;
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
				//"値の数は " + placeholderIndexes.length + " である必要があります"
				throw new IllegalStateException("the number of values ​​must be " + placeholderIndexes.length);

			Object[] clone = values.clone();

			for (int i = 0; i < newValues.length; i++) {
				clone[placeholderIndexes[i]] = newValues[i];
			}

			return reproducible.reproduce(clone);
		}

		private Reproducible<?> reproduce() {
			return reproducible.reproduce();
		}
	}
}
