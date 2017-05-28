package org.blendee.internal;

/**
 * 内部使用ユーティリティクラス
 *
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public class LRUQueue<E extends LRUQueueElement> extends Queue<E> {

	private int size = 0;

	private int capacity;

	public LRUQueue(int capacity) {
		this.capacity = capacity < 1 ? 1 : capacity;
	}

	@Override
	public void addFirst(E element) {
		while (capacity <= size)
			shrink();
		super.addFirst(element);
		element.setQueue(this);
		size++;
	}

	@Override
	public void addLast(E element) {
		while (capacity <= size)
			shrink();
		super.addLast(element);
		element.setQueue(this);
		size++;
	}

	@Override
	public void clear() {
		super.clear();
		while (0 < size)
			shrink();
	}

	public void ensureCapacity(int capacity) {
		this.capacity = capacity < 1 ? 1 : capacity;
		while (size > capacity) {
			shrink();
		}
	}

	public int size() {
		return size;
	}

	public int getCapacity() {
		return capacity;
	}

	public void shrink() {
		E cached = getLast();
		cached.remove();
	}

	void addFirstInternal(E element) {
		super.addFirst(element);
	}

	void decrement() {
		size--;
	}
}
