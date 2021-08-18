package org.blendee.assist;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.blendee.internal.U;
import org.blendee.jdbc.AutoCloseableIterator;
import org.blendee.orm.DataObject;

/**
 * {@link Iterator} としての性質を持った {@link Row} の集合を表します。
 * @author 千葉 哲嗣
 * @param <O> One {@link Many} の要素
 * @param <M> Many {@link Many} の要素を一とした場合の、一対多の多側の型連鎖
 */
public class Many<O extends Row, M> implements Iterable<One<O, M>>, AutoCloseableIterator<One<O, M>> {

	private final DataObjectManager manager;

	private final DataObject one;

	private final OneToManyBehavior selfAsMany;

	private final OneToManyBehavior nextMany;

	private final List<OneToManyBehavior> route;

	private DataObject prev;

	Many(
		DataObjectManager manager,
		DataObject one,
		OneToManyBehavior selfAsMany,
		List<OneToManyBehavior> route) {
		this.manager = manager;
		this.one = one;
		this.selfAsMany = selfAsMany;
		this.route = route;
		nextMany = getManyOf(selfAsMany);
	}

	@Override
	public One<O, M> next() {
		//hasNext() が実行されていません
		if (!manager.prepared()) throw new IllegalStateException("Do hasNext() first.");

		var current = manager.current(selfAsMany.getRelationship());

		Many<Row, Object> next;
		if (nextMany == null) {
			manager.next();
			next = null;
		} else {
			next = new Many<>(manager, current, getManyOf(selfAsMany), route);
		}

		@SuppressWarnings("unchecked")
		var result = (One<O, M>) new One<>(selfAsMany.createRow(current), next);

		return result;
	}

	@Override
	public boolean hasNext() {
		if (!manager.prepared()) return false;

		var key = selfAsMany.getRelationship();
		var current = manager.current(key);

		while (prev != null && current != null && prev.getPrimaryKey().equals(current.getPrimaryKey())) {
			manager.next();

			if (!manager.prepared()) return false;

			current = manager.current(key);
		}

		prev = current;

		if (one != null) {
			DataObject currentOne = manager.current(one.getRelationship());

			return one.getPrimaryKey().equals(currentOne.getPrimaryKey());
		}

		return true;
	}

	/**
	 * Stream に変換します。
	 * @return {@link Stream}
	 */
	public Stream<One<O, M>> stream() {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED), false);
	}

	@Override
	public void close() {
		manager.close();
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private OneToManyBehavior getManyOf(OneToManyBehavior one) {
		OneToManyBehavior many = null;

		for (OneToManyBehavior relation : route) {
			if (relation.equals(one)) return many;
			many = relation;
		}

		throw new IllegalStateException();
	}
}
