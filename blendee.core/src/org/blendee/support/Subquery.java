package org.blendee.support;

import org.blendee.sql.Column;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.SQLQueryBuilder;

/**
 * サブクエリとして使用する条件を作成するクラスです。
 * @author 千葉 哲嗣
 */
class Subquery {

	/**
	 * このサブクエリから、メインクエリで使用できる {@link Criteria} を生成します。
	 * @param main {@link RuntimeId}
	 * @param builder {@link SQLQueryBuilder}
	 * @param notIn NOT IN の場合 true
	 * @param mainQueryColumn メインクエリ側のカラム
	 * @return {@link Criteria} となったサブクエリ
	 */
	static Criteria createCriteria(RuntimeId main, SQLQueryBuilder builder, boolean notIn, Column... mainQueryColumn) {
		return new CriteriaFactory(main)
			//SQLDecoratorでサブクエリのSELECT句自体が変更されている場合を考慮し、SELECT句チェックを行わない
			.createSubqueryWithoutCheck(
				mainQueryColumn,
				builder,
				notIn);
	}
}
