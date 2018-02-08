package org.blendee.jdbc;

/**
 * commit, rollback ができることのみを定義したインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface Committable {

	/**
	 * commit
	 */
	void commit();

	/**
	 * rollback
	 */
	void rollback();
}
