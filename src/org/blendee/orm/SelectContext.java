package org.blendee.orm;

import org.blendee.jdbc.TablePath;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.SelectClause;

/**
 * 検索結果から {@link SelectedValues} を生成する機能を定めたインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface SelectContext extends SelectedValuesConverter {

	/**
	 * このクラスのインスタンスが持つ対象となるテーブルです。
	 * @return 対象となるテーブル
	 */
	TablePath tablePath();

	/**
	 * このクラスのインスタンスが持つ {@link RuntimeId} です。
	 * @return {@link RuntimeId}
	 */
	RuntimeId runtimeId();

	/**
	 * 検索に使用する SELECT 句です。
	 * @return SELECT 句
	 */
	SelectClause selectClause();
}
