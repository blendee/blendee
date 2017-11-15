package org.blendee.jdbc.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.blendee.jdbc.ContextStrategy;

/**
 * {@link ContextStrategy} の簡易実装クラスです。
 * @author 千葉 哲嗣
 */
public class SimpleContextStrategy implements ContextStrategy {

	private final Object lock = new Object();

	private final Map<String, Map<Class<?>, Object>> original = new HashMap<>();

	private final ThreadLocal<Map<String, Map<Class<?>, Object>>> clones = ThreadLocal.withInitial(
		() -> new HashMap<>(original));

	@Override
	public <T> T getManagedInstance(String contextName, Class<T> clazz) {
		Objects.requireNonNull(clazz);

		Map<String, Map<Class<?>, Object>> clone = clones.get();

		Map<Class<?>, Object> map = getInnerMap(clone, contextName);
		Object instance = map.get(clazz);
		if (instance == null) {
			synchronized (lock) {
				Map<Class<?>, Object> originalMap = getInnerMap(original, contextName);

				instance = originalMap.get(clazz);
				if (instance == null) {
					try {
						instance = clazz.getDeclaredConstructor().newInstance();
					} catch (Exception e) {
						throw new IllegalStateException(e);
					}

					originalMap.put(clazz, instance);
				}
			}

			map.put(clazz, instance);
		}

		@SuppressWarnings("unchecked")
		T result = (T) instance;

		return result;
	}

	private static Map<Class<?>, Object> getInnerMap(Map<String, Map<Class<?>, Object>> map, String name) {
		Map<Class<?>, Object> inner = map.get(name);
		if (inner == null) {
			inner = new HashMap<>();
			map.put(name, inner);
		}

		return inner;
	}
}
