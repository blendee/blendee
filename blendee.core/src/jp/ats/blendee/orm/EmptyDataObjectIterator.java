package jp.ats.blendee.orm;

import java.util.NoSuchElementException;

import jp.ats.blendee.internal.U;

/**
 * @author 千葉 哲嗣
 */
class EmptyDataObjectIterator<E extends DataObject> extends DataObjectIterator<E> {

	EmptyDataObjectIterator() {
		super(null, null, false);
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public E next() {
		throw new NoSuchElementException();
	}

	@Override
	public DataObject nextDataObject() {
		throw new NoSuchElementException();
	}

	@Override
	public UpdatableDataObject nextUpdatableDataObject() {
		throw new NoSuchElementException();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getCounter() {
		return 0;
	}

	@Override
	public void close() {}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
