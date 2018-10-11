package org.blendee.support;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.blendee.internal.U;
import org.blendee.jdbc.AutoCloseableIterator;
import org.blendee.orm.DataObject;
import org.blendee.orm.DataObjectIterator;

/**
 * 自動生成される RowManager の実装が使用する Iterator クラスです。
 * @author 千葉 哲嗣
 * @param <R> 要素
 */
public abstract class RowIterator<R extends Row>
	implements AutoCloseableIterator<R> {

	private final DataObjectIterator iterator;

	/**
	 * 唯一のコンストラクタです。
	 * @param iterator 検索結果オブジェクト
	 */
	public RowIterator(DataObjectIterator iterator) {
		this.iterator = iterator;
	}

	/**
	 * Stream に変換します。
	 * @return {@link Stream}
	 */
	public Stream<R> stream() {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED), false);
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	/**
	 * 一件の検索結果を返します。
	 * @return 検索結果一件
	 */
	protected DataObject nextDataObject() {
		return iterator.next();
	}

	/**
	 * 検索結果オブジェクトを返します。
	 * @return 検索結果オブジェクト
	 */
	protected DataObjectIterator getDataObjectIterator() {
		return iterator;
	}

	/**
	 * {@link #next()} を行った回数を返します。
	 * @return {@link #next()} を行った回数
	 */
	public int getCounter() {
		return iterator.getCounter();
	}

	/**
	 * 検索結果を閉じます。
	 */
	@Override
	public void close() {
		iterator.close();
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
