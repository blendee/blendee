package org.blendee.internal;

/**
 * 内部使用ユーティリティクラス
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public abstract class QueueElement {

	private static final QueueElement plug = new QueuePlug();

	private QueueElement previous = plug;

	private QueueElement next = plug;

	public boolean hasPrevious() {
		return previous.isNotTerminal();
	}

	public boolean hasNext() {
		return next.isNotTerminal();
	}

	public QueueElement getPrevious() {
		return previous.self();
	}

	public QueueElement getNext() {
		return next.self();
	}

	public void remove() {
		previous.setNext(next);
		next.setPrevious(previous);
		previous = plug;
		next = plug;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	boolean isNotTerminal() {
		return true;
	}

	void setPrevious(QueueElement previous) {
		this.previous = previous;
	}

	void setNext(QueueElement next) {
		this.next = next;
	}

	QueueElement self() {
		return this;
	}
}
