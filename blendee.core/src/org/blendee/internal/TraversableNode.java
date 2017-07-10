package org.blendee.internal;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 内部使用ユーティリティクラス
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public class TraversableNode {

	private final Collection<Traversable> traversables;

	public TraversableNode() {
		traversables = newCollection();
	}

	public final void add(Traversable traversable) {
		traversables.add(traversable);
	}

	public final Traversable[] getTraversables() {
		return traversables.toArray(new Traversable[traversables.size()]);
	}

	public final void clear() {
		traversables.clear();
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	protected Collection<Traversable> newCollection() {
		return new LinkedList<>();
	}
}
