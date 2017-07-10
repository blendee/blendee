package org.blendee.sql;

/**
 * 一部分の SQL 文の中から分離された文字定数部とその他の部分を受け取るインターフェイスです。
 * @author 千葉 哲嗣
 * @see SQLFragmentUtilities
 */
public interface SQLFragmentListener {

	/**
	 * 文字列以外の部分を受け取ります。
	 * @param fragment 文字列以外の部分
	 */
	void receiveSQLFragment(String fragment);

	/**
	 * ' を受け取ります。
	 * @param quote クォート
	 */
	void receiveQuote(String quote);

	/**
	 * SQL 文内の文字列部分を受け取ります。
	 * @param constants 文字列
	 */
	void receiveStringConstants(String constants);
}
