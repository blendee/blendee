package org.blendee.support;

import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;

/**
 * 自動生成される QueryRelationship クラスのインスタンスが WHERE 句用の場合、そのタイプを表す列挙型です。<br>
 * 内部使用を目的としています。
 * @author 千葉 哲嗣
 */
public enum QueryCriteriaContext {

	/**
	 * 条件 AND 結合
	 */
	AND {

		@Override
		public void addCriteria(QueryRelationship relationship, Criteria criteria) {
			Criteria clause = relationship.getWhereClause();
			if (clause == null) {
				clause = CriteriaFactory.create();
				relationship.setWhereClause(clause);
			}

			clause.and(criteria);
		}
	},

	/**
	 * 条件 OR 結合
	 */
	OR {

		@Override
		public void addCriteria(QueryRelationship relationship, Criteria criteria) {
			Criteria clause = relationship.getWhereClause();
			if (clause == null) {
				clause = CriteriaFactory.create();
				relationship.setWhereClause(clause);
			}

			clause.or(criteria);
		}
	},

	/**
	 * 条件句以外
	 */
	NULL {

		@Override
		public void addCriteria(QueryRelationship relationship, Criteria criteria) {
			throw new IllegalStateException();
		}
	};

	/**
	 * 検索条件作成時に、自身のタイプに合った振る舞いをします。<br>
	 * このメソッドは、内部使用を目的としていますので、直接使用しないでください。
	 * @param relationship 条件作成に必要な情報を持った {@link QueryRelationship}
	 * @param criteria 新条件
	 */
	public abstract void addCriteria(QueryRelationship relationship, Criteria criteria);
}
