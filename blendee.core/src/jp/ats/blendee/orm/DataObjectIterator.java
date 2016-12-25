package jp.ats.blendee.orm;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import jp.ats.blendee.internal.U;
import jp.ats.blendee.selector.Optimizer;
import jp.ats.blendee.selector.SelectedValuesIterator;
import jp.ats.blendee.sql.Condition;
import jp.ats.blendee.sql.OrderByClause;
import jp.ats.blendee.sql.Relationship;

/**
 * 検索結果から {@link DataObject} を生成するクラスです。
 *
 * @author 千葉 哲嗣
 * @param <E> 要素
 * @see DataAccessHelper#getDataObjects(Optimizer, Condition, OrderByClause, QueryOption...)
 * @see DataAccessHelper#regetDataObjects(Optimizer)
 */
public class DataObjectIterator<E extends DataObject>
	implements AutoCloseable, Iterable<E>, Iterator<E> {

	private final Relationship relationship;

	private final SelectedValuesIterator iterator;

	private final boolean readonly;

	DataObjectIterator(
		Relationship relationship,
		SelectedValuesIterator iterator,
		boolean readonly) {
		this.relationship = relationship;
		this.iterator = iterator;
		this.readonly = readonly;
	}

	@Override
	public Iterator<E> iterator() {
		return this;
	}

	/**
	 * Stream に変換します。
	 *
	 * @return {@link Stream}
	 */
	public Stream<E> stream() {
		return StreamSupport.stream(
			Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED),
			false);
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@SuppressWarnings("unchecked")
	@Override
	public E next() {
		if (readonly) return (E) new DataObject(relationship, iterator.next());
		return (E) new UpdatableDataObject(relationship, iterator.next());
	}

	/**
	 * 検索結果の一行を {@link DataObject} として返します。
	 *
	 * @return 検索結果の一行
	 */
	public DataObject nextDataObject() {
		return new DataObject(relationship, iterator.next());
	}

	/**
	 * 検索結果の一行を {@link UpdatableDataObject} として返します。
	 *
	 * @return 検索結果の一行
	 */
	public UpdatableDataObject nextUpdatableDataObject() {
		return new UpdatableDataObject(relationship, iterator.next());
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
	 *
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
