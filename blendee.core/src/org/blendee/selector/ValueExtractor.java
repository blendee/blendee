package org.blendee.selector;

import org.blendee.jdbc.BResult;
import org.blendee.sql.Binder;

/**
 * 検索結果からどの型の値でも取得可能とするためのインターフェイスです。
 *
 * @author 千葉 哲嗣
 * @see ValueExtractors#selectValueExtractor(Class)
 */
public interface ValueExtractor {

	/**
	 * 検索結果から、指定された位置の値を何らかのオブジェクトとして返します。
	 *
	 * @param result 検索結果
	 * @param columnIndex 検索結果の位置
	 * @return 値
	 */
	Object extract(BResult result, int columnIndex);

	/**
	 * {@link #extract(BResult, int)} で返された値から、対応する {@link Binder} を返します。
	 *
	 * @param value 検索結果の値
	 * @return 対応する {@link Binder}
	 */
	Binder extractAsBinder(Object value);
}
