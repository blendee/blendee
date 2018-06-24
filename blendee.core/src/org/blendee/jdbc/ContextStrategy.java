package org.blendee.jdbc;

/**
 * {@link ContextManager} で扱われるインスタンスの管理方法を表すインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface ContextStrategy {

	/**
	 * クラスをもとに、そのクラスのインスタンスを生成、返します。
	 * @param contextName コンテキスト名
	 * @param <T> 対象となる型
	 * @param clazz 対象となるクラス
	 * @return そのクラスのインスタンス
	 */
	<T> T getManagedInstance(String contextName, Class<T> clazz);

	/**
	 * contextName で管理しているインスタンスを解放します。
	 * @param contextName コンテキスト名
	 */
	void release(String contextName);
}
