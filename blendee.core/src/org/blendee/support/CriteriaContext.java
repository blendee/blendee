package org.blendee.support;

import org.blendee.sql.Criteria;

/**
 * 自動生成される QueryRelationship クラスのインスタンスが WHERE 句、 HAVING 句用の場合、そのタイプを表す列挙型です。<br>
 * 内部使用を目的としています。
 * @author 千葉 哲嗣
 */
public enum CriteriaContext {

	/**
	 * 条件 AND 結合
	 */
	AND {

		@Override
		public void addCriteria(Criteria criteria) {
			getContextCriteria().and(criteria);
		}
	},

	/**
	 * 条件 OR 結合
	 */
	OR {

		@Override
		public void addCriteria(Criteria criteria) {
			getContextCriteria().or(criteria);
		}
	},

	/**
	 * 条件句以外
	 */
	NULL {

		@Override
		public void addCriteria(Criteria criteria) {
			throw new IllegalStateException();
		}
	};

	/**
	 * 検索条件作成時に、自身のタイプに合った振る舞いをします。<br>
	 * このメソッドは、内部使用を目的としていますので、直接使用しないでください。
	 * @param criteria 新条件
	 */
	public abstract void addCriteria(Criteria criteria);

	private static final ThreadLocal<Criteria> criteriaThreadLocal = new ThreadLocal<>();

	static void setContextCriteria(Criteria criteria) {
		criteriaThreadLocal.set(criteria);
	}

	static void removeContextCriteria() {
		criteriaThreadLocal.remove();
	}

	static Criteria getContextCriteria() {
		return criteriaThreadLocal.get();
	}
}
