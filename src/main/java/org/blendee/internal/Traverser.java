package org.blendee.internal;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 内部使用ユーティリティクラス
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public interface Traverser {

	/**
	 * 深さ優先探索
	 */
	TraverserOperator DEPTH_FIRST = new TraverserOperator() {

		@Override
		public void operate(Traverser traverser, Traversable[] traversables) {
			for (var traversable : traversables)
				operate(traverser, traversable);
		}
	};

	/**
	 * 幅優先探索
	 */
	TraverserOperator BREADTH_FIRST = new TraverserOperator() {

		@Override
		public void operate(final Traverser traverser, Traversable[] traversables) {
			final var list = new ArrayList<Traversable>();
			list.addAll(Arrays.asList(traversables));
			//探索が進むと要素が追加されるのでIteratorは使えない
			for (int i = 0; i < list.size(); i++) {
				var traversable = list.get(i);
				operate(new Traverser() {

					@Override
					public TraverserOperator getOperator() {
						return new TraverserOperator() {

							@Override
							public void operate(Traverser traverser, Traversable[] traversables) {
								list.addAll(Arrays.asList(traversables));
							}
						};
					}

					@Override
					public void execute(Traversable traversable) {
						traverser.execute(traversable);
					}
				}, traversable);
			}
		}
	};

	TraverserOperator getOperator();

	void execute(Traversable traversable);
}
