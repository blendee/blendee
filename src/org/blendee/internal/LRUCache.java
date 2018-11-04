package org.blendee.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * 内部使用ユーティリティクラス
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public class LRUCache<K, V> implements Cache<K, V> {

	private final LRUQueue<CacheElement<K, V>> queue;

	private Map<K, CacheElement<K, V>> map;

	public static <K, V> LRUCache<K, V> newInstance(int capacity) {
		return new LRUCache<K, V>(capacity);
	}

	public LRUCache(int capacity) {
		queue = new LRUQueue<CacheElement<K, V>>(capacity);
		map = new HashMap<>(capacity + 1);
	}

	@Override
	public void ensureCapacity(int capacity) {
		queue.ensureCapacity(capacity);
		Map<K, CacheElement<K, V>> newMap = new HashMap<>(capacity + 1);
		newMap.putAll(map);
		map = newMap;
	}

	@Override
	public int getCapacity() {
		return queue.getCapacity();
	}

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public V get(K key) {
		CacheElement<K, V> element = map.get(key);
		if (element == null) return null;
		element.top();
		return element.value;
	}

	@Override
	public void cache(K key, V value) {
		CacheElement<K, V> element = new CacheElement<K, V>(key, value);
		queue.addFirst(element);
		CacheElement<K, V> cached = map.put(key, element);
		//値が置き換えられた場合、 Queue から削除する
		if (cached != null) {
			cached.remove();
		}
	}

	@Override
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	@Override
	public V remove(K key) {
		CacheElement<K, V> element = map.remove(key);
		if (element == null) return null;
		element.remove();
		return element.value;
	}

	@Override
	public void clear() {
		queue.clear();
	}

	@Override
	public void shrink() {
		queue.shrink();
	}

	public void shrink(K key) {
		CacheElement<K, V> element = map.get(key);
		if (element == null) return;
		element.remove();
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private class CacheElement<EK, EV> extends LRUQueueElement {

		private final EK key;

		private final EV value;

		private CacheElement(EK key, EV value) {
			this.key = key;
			this.value = value;
		}

		@SuppressWarnings("unlikely-arg-type")
		@Override
		public void remove() {
			super.remove();
			map.remove(key);
		}
	}
}
