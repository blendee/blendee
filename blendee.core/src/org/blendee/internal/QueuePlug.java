package org.blendee.internal;

import java.util.NoSuchElementException;

/**
 * @author 千葉 哲嗣
 */
class QueuePlug extends QueueElement {

	@Override
	public boolean hasPrevious() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasNext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueueElement getPrevious() {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueueElement getNext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	boolean isNotTerminal() {
		return false;
	}

	@Override
	void setPrevious(QueueElement previous) {}

	@Override
	void setNext(QueueElement next) {}

	@Override
	QueueElement self() {
		throw new NoSuchElementException();
	}
}
