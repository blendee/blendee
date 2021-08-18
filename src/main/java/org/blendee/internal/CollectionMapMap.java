package org.blendee.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 内部使用ユーティリティクラス
 * @author 千葉 哲嗣
 * @param <K1> Key1
 * @param <K2> Key2
 * @param <V> Value
 */
@SuppressWarnings("javadoc")
public class CollectionMapMap<K1, K2, V> implements Cloneable {

	private final Map<K1, CollectionMap<K2, V>> map;

	public static <V, K1, K2> CollectionMapMap<K1, K2, V> newInstance() {
		return new CollectionMapMap<>();
	}

	public static <V, K1, K2> CollectionMapMap<K1, K2, V> newInstance(
		@SuppressWarnings("rawtypes")
		Class<? extends Map> mapClass) {
		return new CollectionMapMap<>(mapClass);
	}

	public CollectionMapMap() {
		map = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	public CollectionMapMap(
		@SuppressWarnings("rawtypes")
		Class<? extends Map> mapClass) {
		try {
			this.map = mapClass.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public CollectionMap<K2, V> get(K1 key) {
		var sub = map.get(key);
		if (sub == null) {
			sub = createNewMap();
			map.put(key, sub);
		}
		return sub;
	}

	public Set<K1> keySet() {
		return map.keySet();
	}

	public CollectionMap<K2, V> remove(K1 key) {
		var sub = map.remove(key);
		if (sub == null) return createNewMap();
		return sub;
	}

	public int size() {
		return map.size();
	}

	public void clear() {
		map.clear();
	}

	public boolean containsKey(K1 key) {
		return map.containsKey(key);
	}

	public Map<K1, CollectionMap<K2, V>> getInnerMap() {
		return Collections.unmodifiableMap(map);
	}

	@Override
	public CollectionMapMap<K1, K2, V> clone() {
		CollectionMapMap<K1, K2, V> clone = newInstance(map.getClass());
		for (var entry : map.entrySet()) {
			clone.map.put(entry.getKey(), entry.getValue().clone());
		}

		return clone;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	protected CollectionMap<K2, V> createNewMap() {
		return CollectionMap.newInstance();
	}
}
