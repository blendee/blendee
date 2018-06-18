package org.blendee.support;

import org.blendee.sql.Column;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.QueryBuilder;

/**
 * 自動生成される {@link Query} で使用するサブクエリを表すクラスです。
 * @author 千葉 哲嗣
 */
public class Subquery {

	private final QueryBuilder builder;

	Subquery(QueryBuilder builder) {
		this.builder = builder;
	}

	/**
	 * このサブクエリから、メインクエリで使用できる {@link Criteria} を生成します。
	 * @param notIn NOT IN の場合 true
	 * @param mainquery メインクエリ
	 * @return {@link Criteria} となったサブクエリ
	 */
	public Criteria createCriteria(boolean notIn, Query mainquery) {
		return CriteriaFactory
			//SQLDecoratorでサブクエリのSELECT句自体が変更されている場合を考慮し、SELECT句チェックを行わない
			.createSubqueryWithoutCheck(
				mainquery.getRootRealtionship().getPrimaryKeyColumns(),
				builder,
				notIn);
	}

	/**
	 * このサブクエリから、メインクエリで使用できる {@link Criteria} を生成します。
	 * @param notIn NOT IN の場合 true
	 * @param mainQueryColumn メインクエリ側のカラム
	 * @return {@link Criteria} となったサブクエリ
	 */
	public Criteria createCriteria(boolean notIn, Column... mainQueryColumn) {
		return CriteriaFactory
			//SQLDecoratorでサブクエリのSELECT句自体が変更されている場合を考慮し、SELECT句チェックを行わない
			.createSubqueryWithoutCheck(
				mainQueryColumn,
				builder,
				notIn);
	}
}
