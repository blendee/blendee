package org.blendee.support;

/**
 * {@link Query} 内で定義される AND, OR 保持クラス用のインターフェイス
 * @param <T> AND, OR
 */
public interface LogicalOperators<T> {

	/**
	 * 条件生成のスタートに使用する演算子を返します。
	 * @return 初期用演算子
	 */
	T defaultOperator();
}
