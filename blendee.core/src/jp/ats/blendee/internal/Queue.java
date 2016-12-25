package jp.ats.blendee.internal;

import java.util.Iterator;

/**
 * 内部使用ユーティリティクラス
 *
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public class Queue<E extends QueueElement> implements Iterable<E> {

	private final QueueElement firstTerminator = new FirstTerminator();

	private final QueueElement lastTerminator = new LastTerminator();

	private QueueElement first;

	private QueueElement last;

	public Queue() {
		clear();
	}

	public void addFirst(E element) {
		element.setPrevious(firstTerminator);
		element.setNext(first);
		first.setPrevious(element);
		first = element;
	}

	public void addLast(E element) {
		element.setPrevious(last);
		element.setNext(lastTerminator);
		last.setNext(element);
		last = element;
	}

	@SuppressWarnings("unchecked")
	public E getFirst() {
		return (E) first.self();
	}

	@SuppressWarnings("unchecked")
	public E getLast() {
		return (E) last.self();
	}

	public void clear() {
		first = firstTerminator;
		last = lastTerminator;
	}

	@Override
	public Iterator<E> iterator() {
		return new QueueIterator();
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private class QueueIterator implements Iterator<E> {

		private E current = getFirst();

		@Override
		public boolean hasNext() {
			return current.isNotTerminal();
		}

		@SuppressWarnings("unchecked")
		@Override
		public E next() {
			E next = current;
			current = (E) current.getNext();
			return next;
		}

		@Override
		public void remove() {
			current.remove();
		}
	}

	private class FirstTerminator extends QueuePlug {

		@Override
		void setPrevious(QueueElement previous) {
			last = previous;
			previous.setNext(lastTerminator);
		}

		@Override
		void setNext(QueueElement next) {
			if (next == lastTerminator) {
				first = this;
				last = lastTerminator;
			} else {
				first = next;
			}
		}
	}

	private class LastTerminator extends QueuePlug {

		@Override
		void setPrevious(QueueElement previous) {
			if (previous == firstTerminator) {
				last = this;
				first = firstTerminator;
			} else {
				last = previous;
			}
		}

		@Override
		void setNext(QueueElement next) {
			first = next;
			next.setPrevious(firstTerminator);
		}
	}
}
