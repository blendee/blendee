package org.blendee.orm;

import org.blendee.selector.Selector;

/**
 * 検索用 SQL の作成プロセスに対する調整を行うためのインターフェイスです。
 * @author 千葉 哲嗣
 */
@FunctionalInterface
public interface QueryOption {

	/**
	 * {@link Selector} に対する操作を実行します。
	 * @param selector 対象となる {@link Selector}
	 */
	void process(Selector selector);
}
