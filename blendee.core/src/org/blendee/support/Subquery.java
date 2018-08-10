package org.blendee.support;

import org.blendee.sql.Column;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.SelectStatementBuilder;

/**
 * サブクエリとして使用する条件を作成するクラスです。
 * @author 千葉 哲嗣
 */
class Subquery {

	/**
	 * このサブクエリから、メインクエリで使用できる {@link Criteria} を生成します。
	 * @param builder {@link SelectStatementBuilder}
	 * @param notIn NOT IN の場合 true
	 * @param mainquery メインクエリ
	 * @return {@link Criteria} となったサブクエリ
	 */
	static Criteria createCriteria(SelectStatementBuilder builder, boolean notIn, QueryBuilder mainquery) {
		return CriteriaFactory
			//SQLDecoratorでサブクエリのSELECT句自体が変更されている場合を考慮し、SELECT句チェックを行わない
			.createSubqueryWithoutCheck(
				mainquery.getRootRealtionship().getPrimaryKeyColumns(),
				builder,
				notIn);
	}

	/**
	 * このサブクエリから、メインクエリで使用できる {@link Criteria} を生成します。
	 * @param builder {@link SelectStatementBuilder}
	 * @param notIn NOT IN の場合 true
	 * @param mainQueryColumn メインクエリ側のカラム
	 * @return {@link Criteria} となったサブクエリ
	 */
	static Criteria createCriteria(SelectStatementBuilder builder, boolean notIn, Column... mainQueryColumn) {
		return CriteriaFactory
			//SQLDecoratorでサブクエリのSELECT句自体が変更されている場合を考慮し、SELECT句チェックを行わない
			.createSubqueryWithoutCheck(
				mainQueryColumn,
				builder,
				notIn);
	}
}
