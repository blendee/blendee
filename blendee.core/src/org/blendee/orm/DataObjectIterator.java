package org.blendee.orm;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.blendee.internal.U;
import org.blendee.selector.Optimizer;
import org.blendee.selector.SelectedValuesIterator;
import org.blendee.sql.Criteria;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.Relationship;

/**
 * 検索結果から {@link DataObject} を生成するクラスです。
 * @author 千葉 哲嗣
 * @see DataAccessHelper#getDataObjects(Optimizer, Criteria, OrderByClause, SQLDecorator...)
 * @see DataAccessHelper#regetDataObjects(Optimizer)
 */
public class DataObjectIterator
	implements AutoCloseable, Iterable<DataObject>, Iterator<DataObject> {

	private final Relationship relationship;

	private final SelectedValuesIterator iterator;

	private final boolean readonly;

	@SuppressWarnings("javadoc")
	public DataObjectIterator(
		Relationship relationship,
		SelectedValuesIterator iterator,
		boolean readonly) {
		this.relationship = relationship;
		this.iterator = iterator;
		this.readonly = readonly;
	}

	@Override
	public Iterator<DataObject> iterator() {
		return this;
	}

	/**
	 * Stream に変換します。
	 * @return {@link Stream}
	 */
	public Stream<DataObject> stream() {
		return StreamSupport.stream(
			Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED),
			false);
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public DataObject next() {
		if (readonly) return new DataObject(relationship, iterator.next());
		return new DataObject(relationship, iterator.next());
	}

	/**
	 * 検索結果の一行を {@link DataObject} として返します。
	 * @return 検索結果の一行
	 */
	public DataObject nextDataObject() {
		return new DataObject(relationship, iterator.next());
	}

	/**
	 * @throws UnsupportedOperationException 使用不可
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
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
