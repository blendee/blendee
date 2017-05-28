package org.blendee.jdbc;

/**
 * 内部使用ユーティリティクラス
 *
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public interface Transaction {

	void commit();

	void rollback();
}
