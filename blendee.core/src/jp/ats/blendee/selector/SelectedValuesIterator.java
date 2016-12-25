package jp.ats.blendee.selector;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.BResultSet;
import jp.ats.blendee.jdbc.BStatement;
import jp.ats.blendee.sql.Column;

/**
 * 検索結果から {@link SelectedValues} を生成するクラスです。
 *
 * @author 千葉 哲嗣
 * @see Selector#select()
 */
public class SelectedValuesIterator
	implements AutoCloseable, Iterable<SelectedValues>, Iterator<SelectedValues> {

	private final BStatement statement;

	private final BResultSet result;

	private final Column[] columns;

	private final Optimizer optimizer;

	private int counter = 0;

	private boolean called = false;

	private boolean hasNext = false;

	SelectedValuesIterator(
		BStatement statement,
		BResultSet result,
		Column[] columns,
		Optimizer optimizer) {
		this.statement = statement;
		this.result = result;
		this.columns = columns;
		this.optimizer = optimizer;
	}

	@Override
	public Iterator<SelectedValues> iterator() {
		return this;
	}

	/**
	 * Stream に変換します。
	 *
	 * @return {@link Stream}
	 */
	public Stream<SelectedValues> stream() {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED), false);
	}

	@Override
	public boolean hasNext() {
		if (called) return hasNext;
		hasNext = result.next();
		called = true;
		return hasNext;
	}

	@Override
	public SelectedValues next() {
		if (!called) hasNext();
		if (!hasNext) throw new NoSuchElementException();
		SelectedValues values = optimizer.convert(result, columns);
		called = false;
		counter++;
		return values;
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
		return counter;
	}

	/**
	 * 検索結果を閉じます。
	 */
	@Override
	public void close() {
		try {
			result.close();
		} finally {
			statement.close();
		}
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
