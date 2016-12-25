package jp.ats.blendee.jdbc.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import jp.ats.blendee.jdbc.ContextStrategy;

/**
 * {@link ContextStrategy} の簡易実装クラスです。
 *
 * @author 千葉 哲嗣
 */
public class SimpleContextStrategy implements ContextStrategy {

	private final Object lock = new Object();

	private final Map<Class<?>, Object> original = new HashMap<>();

	private final ThreadLocal<Map<Class<?>, Object>> clones = ThreadLocal.withInitial(
		() -> new HashMap<>(original));

	@Override
	public <T> T getManagedInstance(Class<T> clazz) {
		Objects.requireNonNull(clazz);

		Map<Class<?>, Object> clone = clones.get();

		Object instance = clone.get(clazz);
		if (instance == null) {
			synchronized (lock) {
				instance = original.get(clazz);

				if (instance == null) {
					try {
						instance = clazz.newInstance();
					} catch (Exception e) {
						throw new IllegalStateException(e);
					}

					original.put(clazz, instance);
				}
			}

			clone.put(clazz, instance);
		}

		@SuppressWarnings("unchecked")
		T result = (T) instance;

		return result;
	}
}
