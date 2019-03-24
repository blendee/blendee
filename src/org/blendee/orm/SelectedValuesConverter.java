package org.blendee.orm;

import org.blendee.jdbc.Result;
import org.blendee.sql.Column;

/**
 * 検索結果から {@link SelectedValues} を生成する機能を定めたインターフェイスです。
 * @author 千葉 哲嗣
 */
@FunctionalInterface
public interface SelectedValuesConverter {

	/**
	 * 検索結果と検索時に指定したカラムから {@link SelectedValues} を生成します。
	 * @param result 検索結果
	 * @param columns 検索時に指定したカラム
	 * @return {@link SelectedValues}
	 */
	SelectedValues convert(Result result, Column[] columns);
}
