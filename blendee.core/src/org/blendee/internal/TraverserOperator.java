package org.blendee.internal;

/**
 * 内部使用ユーティリティクラス
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public abstract class TraverserOperator {

	public static void operate(Traverser traverser, Traversable traversable) {
		traverser.execute(traversable);
		traverser.getOperator().operate(traverser, traversable.getSubNode().getTraversables());
	}

	public abstract void operate(Traverser traverser, Traversable[] traversables);

	@Override
	public String toString() {
		return U.toString(this);
	}
}
