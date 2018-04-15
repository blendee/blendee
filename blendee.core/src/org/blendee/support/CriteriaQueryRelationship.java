package org.blendee.support;

import org.blendee.sql.Criteria;
import org.blendee.sql.Relationship;

/**
 * 自動生成される ConcreteQueryRelationship の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface CriteriaQueryRelationship {

	/**
	 * この句に条件を追加します。
	 * @param criteria 追加条件
	 */
	default void with(Criteria criteria) {
		getContext().addCriteria(criteria);
	}

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return QueryRelationship が WHERE 句用の場合、そのタイプに応じた {@link QueryCriteriaContext}
	 */
	QueryCriteriaContext getContext();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスの大元の {@link Query}
	 */
	Query getRoot();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスが表す {@link Relationship}
	 */
	Relationship getRelationship();
}
