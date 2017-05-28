package org.blendee.support;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.blendee.internal.U;
import org.blendee.orm.DataObject;
import org.blendee.sql.Relationship;

/**
 * {@link Iterator} としての性質を持った {@link BEntity} の集合を表します。
 *
 * @author 千葉 哲嗣
 *
 * @param <O> One {@link Many} の要素
 * @param <M> Many {@link Many} の要素を一とした場合の、一対多の多側の型連鎖
 */
public class Many<O extends BEntity, M> implements AutoCloseable, Iterable<One<O, M>>, Iterator<One<O, M>> {

	private final DataObjectManager manager;

	private final DataObject one;

	private final QueryRelationship selfAsMany;

	private final QueryRelationship nextMany;

	private final LinkedList<QueryRelationship> route;

	private DataObject prev;

	Many(
		DataObjectManager manager,
		DataObject one,
		QueryRelationship selfAsMany,
		LinkedList<QueryRelationship> route) {
		this.manager = manager;
		this.one = one;
		this.selfAsMany = selfAsMany;
		this.route = route;
		nextMany = getManyOf(selfAsMany);
	}

	@Override
	public One<O, M> next() {
		if (!manager.prepared()) throw new IllegalStateException("hasNext() が実行されていません");

		DataObject current = manager.current(selfAsMany.getRelationship());

		Many<BEntity, Object> next;
		if (nextMany == null) {
			manager.next();
			next = null;
		} else {
			next = new Many<>(manager, current, getManyOf(selfAsMany), route);
		}

		@SuppressWarnings("unchecked")
		One<O, M> result = (One<O, M>) new One<>(selfAsMany.createEntity(current), next);

		return result;
	}

	@Override
	public boolean hasNext() {
		if (!manager.prepared()) return false;

		Relationship key = selfAsMany.getRelationship();
		DataObject current = manager.current(key);

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

	@Override
	public Many<O, M> iterator() {
		return this;
	}

	/**
	 * Stream に変換します。
	 *
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

	private QueryRelationship getManyOf(QueryRelationship one) {
		QueryRelationship many = null;

		for (QueryRelationship relation : route) {
			if (relation.equals(one)) return many;
			many = relation;
		}

		throw new IllegalStateException();
	}
}
