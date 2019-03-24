package org.blendee.orm;

import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.blendee.internal.U;
import org.blendee.jdbc.AutoCloseableIterator;
import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.BStatement;
import org.blendee.sql.Column;

/**
 * 検索結果から {@link SelectedValues} を生成するクラスです。
 * @author 千葉 哲嗣
 */
public class SelectedValuesIterator implements AutoCloseableIterator<SelectedValues> {

	private final BStatement statement;

	private final BResultSet result;

	private final Column[] columns;

	private final SelectedValuesConverter converter;

	private int counter = 0;

	private boolean called = false;

	private boolean hasNext = false;

	SelectedValuesIterator(
		BStatement statement,
		BResultSet result,
		Column[] columns,
		SelectedValuesConverter converter) {
		this.statement = statement;
		this.result = result;
		this.columns = columns;
		this.converter = converter;
	}

	/**
	 * Stream に変換します。
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

		if (!hasNext) close();

		return hasNext;
	}

	@Override
	public SelectedValues next() {
		if (!called) hasNext();
		if (!hasNext) throw new NoSuchElementException();
		SelectedValues values = converter.convert(result, columns);
		called = false;
		counter++;
		return values;
	}

	/**
	 * {@link #next()} を行った回数を返します。
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
