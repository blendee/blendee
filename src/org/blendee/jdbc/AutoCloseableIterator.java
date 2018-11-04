package org.blendee.jdbc;

import java.util.Iterator;

/**
 * {@link Iterator} と {@link Iterable} と {@link AutoCloseable} の複合インターフェイスです。
 * @author 千葉 哲嗣
 * @param <E> 要素
 */
public interface AutoCloseableIterator<E> extends AutoCloseable, Iterator<E>, Iterable<E> {

	@Override
	default AutoCloseableIterator<E> iterator() {
		return this;
	}

	@Override
	void close();
}
