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

	private static final Object lock = new Object();

	private static final Map<String, Map<Class<?>, Object>> originalContextMap = new HashMap<>();

	private static final ThreadLocal<Map<String, Map<Class<?>, Object>>> clones = ThreadLocal.withInitial(
		() -> {
			synchronized (lock) {
				return new HashMap<>(originalContextMap);
			}
		});

	@Override
	public <T> T getManagedInstance(String contextName, Class<T> clazz) {
		Objects.requireNonNull(clazz);

		Map<String, Map<Class<?>, Object>> clone = clones.get();

		Map<Class<?>, Object> map = getContextMap(clone, contextName);
		Object instance = map.get(clazz);
		if (instance == null) {
			synchronized (lock) {
				Map<Class<?>, Object> originalMap = getContextMap(originalContextMap, contextName);

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

	/**
	 * クラス内に保持する {@link ThreadLocal} の値をクリアします。
	 */
	public static void removeThreadLocal() {
		clones.remove();
	}

	private static Map<Class<?>, Object> getContextMap(Map<String, Map<Class<?>, Object>> map, String name) {
		Map<Class<?>, Object> contextMap = map.get(name);
		if (contextMap == null) {
			contextMap = new HashMap<>();
			map.put(name, contextMap);
		}

		return contextMap;
	}
}
