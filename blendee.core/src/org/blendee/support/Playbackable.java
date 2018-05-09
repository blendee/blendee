package org.blendee.support;

import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.jdbc.PreparedStatementComplementer;

/**
 * {@link BlenPreparedStatement} にセットする値だけを変更して、何度でも実行可能な検索処理を表すインターフェイスです。
 * @author 千葉 哲嗣
 * @param <T> 検索実行結果の型
 */
@FunctionalInterface
public interface Playbackable<T> {

	/**
	 * 再現可能な検索処理を実行します。
	 * @param complementer {@link PreparedStatementComplementer}
	 * @return 実行結果オブジェクト
	 */
	T play(PreparedStatementComplementer complementer);
}
