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

	private final Map<String, Map<Class<?>, Object>> contextMap = new HashMap<>();

	@Override
	public <T> T getManagedInstance(String contextName, Class<T> clazz) {
		Objects.requireNonNull(clazz);

		Object instance;
		synchronized (lock) {
			var map = getContextMap(contextMap, contextName);

			instance = map.get(clazz);

			if (instance == null) {
				try {
					instance = clazz.getDeclaredConstructor().newInstance();
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}

				map.put(clazz, instance);
			}
		}

		@SuppressWarnings("unchecked")
		var result = (T) instance;

		return result;
	}

	@Override
	public void release(String contextName) {
		synchronized (lock) {
			contextMap.remove(contextName);
		}
	}

	private static Map<Class<?>, Object> getContextMap(Map<String, Map<Class<?>, Object>> map, String name) {
		var contextMap = map.get(name);
		if (contextMap == null) {
			contextMap = new HashMap<>();
			map.put(name, contextMap);
		}

		return contextMap;
	}
}
