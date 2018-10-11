package org.blendee.selector;

import org.blendee.jdbc.TablePath;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.SelectClause;

/**
 * {@link Selector} が SQL 文生成時に SELECT 句を取得するための機能を定めたインターフェイスです。
 * @author 千葉 哲嗣
 * @see Selector#Selector(Optimizer)
 */
public interface Optimizer extends SelectedValuesConverter {

	/**
	 * このクラスのインスタンスが持つ対象となるテーブルです。
	 * @return 対象となるテーブル
	 */
	TablePath getTablePath();

	@SuppressWarnings("javadoc")
	RuntimeId getQueryId();

	/**
	 * 検索に使用する SELECT 句です。
	 * @return SELECT 句
	 */
	SelectClause getOptimizedSelectClause();
}
