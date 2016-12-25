package jp.ats.blendee.selector;

import java.util.NoSuchElementException;

import jp.ats.blendee.internal.U;

/**
 * @author 千葉 哲嗣
 */
class EmptySelectedValuesIterator extends SelectedValuesIterator {

	EmptySelectedValuesIterator() {
		super(null, null, null, null);
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public SelectedValues next() {
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
