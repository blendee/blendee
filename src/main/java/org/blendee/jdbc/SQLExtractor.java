package org.blendee.jdbc;

/**
 * デバッグ等のために Blendee で発行される SQL を抜き出す仕組みを提供する方法を定めたインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface SQLExtractor {

	/**
	 * 
	 * @param base 元となる {@link BConnection}
	 * @param logger SQL 出力先
	 * @return {@link SQLLogger} に SQL を出力する機能を備えた {@link BConnection}
	 */
	BConnection newLoggingConnection(BConnection base, SQLLogger logger);
}
