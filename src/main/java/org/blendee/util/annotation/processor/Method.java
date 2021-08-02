package org.blendee.util.annotation.processor;

/**
 * @author 千葉 哲嗣
 */
public @interface Method {

	/**
	 * メソッド名
	 * @return メソッド名
	 */
	String name();

	/**
	 * メソッドの引数名
	 * @return メソッドの引数名
	 */
	String[] args();
}
