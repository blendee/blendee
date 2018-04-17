package org.blendee.support;

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
	 * @param mainquery メインクエリ
	 * @return {@link Criteria} となったサブクエリ
	 */
	public Criteria createCriteria(Query mainquery) {
		return CriteriaFactory
			//EffectorでSELECT句自体が変更されている場合を考慮し、SELECT句チェックを行わない
			.createSubqueryWithoutCheck(
				mainquery.getRootRealtionship().getPrimaryKeyColumns(),
				builder);
	}
}
