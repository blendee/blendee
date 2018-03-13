package org.blendee.support;

/**
 * {@link Query} 内で定義される AND, OR 保持クラス用のインターフェイス
 * @param <T> AND, OR
 */
public interface LogicalOperators<T> {

	/**
	 * @return AND
	 */
	T AND();

	/**
	 * @return OR
	 */
	T OR();
}
