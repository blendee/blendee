package org.blendee.support;

import org.blendee.sql.Condition;
import org.blendee.sql.ConditionFactory;

/**
 * 自動生成される QueryRelationship クラスのインスタンスが WHERE 句用の場合、そのタイプを表す列挙型です。
 * <br>
 * 内部使用を目的としています。
 *
 * @author 千葉 哲嗣
 */
public enum QueryConditionContext {

	/**
	 * 条件 AND 結合
	 */
	AND {

		@Override
		public void addCondition(QueryRelationship relationship, Condition condition) {
			Condition clause = relationship.getWhereClause();
			if (clause == null) {
				clause = ConditionFactory.createCondition();
				relationship.setWhereClause(clause);
			}

			clause.and(condition);
		}
	},

	/**
	 * 条件 OR 結合
	 */
	OR {

		@Override
		public void addCondition(QueryRelationship relationship, Condition condition) {
			Condition clause = relationship.getWhereClause();
			if (clause == null) {
				clause = ConditionFactory.createCondition();
				relationship.setWhereClause(clause);
			}

			clause.or(condition);
		}
	},

	/**
	 * WHERE 句以外
	 */
	NULL {

		@Override
		public void addCondition(QueryRelationship relationship, Condition condition) {
			throw new IllegalStateException();
		}
	};

	/**
	 * 検索条件作成時に、自身のタイプに合った振る舞いをします。
	 * <br>
	 * このメソッドは、内部使用を目的としていますので、直接使用しないでください。
	 *
	 * @param relationship 条件作成に必要な情報を持った {@link QueryRelationship}
	 * @param condition 新条件
	 */
	public abstract void addCondition(QueryRelationship relationship, Condition condition);
}
