package org.blendee.orm;

import java.util.NoSuchElementException;

import org.blendee.internal.U;

/**
 * @author 千葉 哲嗣
 */
class EmptyDataObjectIterator extends DataObjectIterator {

	EmptyDataObjectIterator() {
		super(null, null, false);
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public DataObject next() {
		throw new NoSuchElementException();
	}

	@Override
	public DataObject nextDataObject() {
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
