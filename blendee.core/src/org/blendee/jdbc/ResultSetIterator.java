package org.blendee.jdbc;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.blendee.internal.U;

/**
 * {@link BResultSet} を、 {@link Iterator} として扱えるようにするラッパークラスです。
 *
 * @author 千葉 哲嗣
 */
public class ResultSetIterator implements Iterable<Map<String, ?>>, Iterator<Map<String, ?>> {

	private final BResultSet set;

	private final Map<String, Object> map = new InnerMap();

	private int counter = 0;

	private boolean init = false;

	private boolean hasNext = false;

	/**
	 * ベースとなる結果セットを使用し、インスタンスを生成します。
	 *
	 * @param set ベースとなる結果セット
	 */
	public ResultSetIterator(BResultSet set) {
		this.set = set;
	}

	@Override
	public Iterator<Map<String, ?>> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		hasNext = set.next();
		init = true;
		return hasNext;
	}

	/**
	 * {@link BResultSet} を一つ進め、一行の値を項目名がキーとなる {@link Map} として返します。
	 * <br>
	 * 返される {@link Map} に対する変更操作は行えません。
	 *
	 * @return 一行の値をもつ {@link Map}
	 */
	@Override
	public Map<String, ?> next() {
		if (!init) hasNext();
		if (!hasNext) throw new NoSuchElementException();
		counter++;
		return map;
	}

	/**
	 * @throws UnsupportedOperationException 使用不可
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 現在の取得済み件数を返します。
	 *
	 * @return 現在の取得済み件数
	 */
	public int getCounter() {
		return counter;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private class InnerMap implements Map<String, Object> {

		@Override
		public int size() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isEmpty() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsKey(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsValue(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object get(Object key) {
			return set.getObject(key.toString());
		}

		@Override
		public Object put(String key, Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object remove(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void putAll(Map<? extends String, ? extends Object> map) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<String> keySet() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<Object> values() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<Entry<String, Object>> entrySet() {
			throw new UnsupportedOperationException();
		}
	}
}
