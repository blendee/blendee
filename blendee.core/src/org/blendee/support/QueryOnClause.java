package org.blendee.support;

import java.util.function.BiConsumer;

import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;

/**
 * ON 句
 * @param <L> LEFT
 * @param <R> RIGHT
 * @param <Q> Query
 */
public class QueryOnClause<L extends OnLeftQueryRelationship, R extends OnRightQueryRelationship, Q extends Query> {

	private final JoinResource resource;

	private final L left;

	private final R right;

	private final Q query;

	QueryOnClause(JoinResource resource, L left, R right, Q query) {
		this.resource = resource;
		this.left = left;
		this.right = right;
		this.query = query;
	}

	/**
	 * @param consumers {@link BiConsumer}
	 * @return {@link Query}
	 */
	@SafeVarargs
	public final Q ON(BiConsumer<L, R>... consumers) {
		//二重に呼ばれた際の処置
		Criteria current = QueryCriteriaContext.getContextCriteria();
		try {
			Criteria contextCriteria = CriteriaFactory.create();
			QueryCriteriaContext.setContextCriteria(contextCriteria);

			for (BiConsumer<L, R> consumer : consumers) {
				consumer.accept(left, right);
			}

			resource.onCriteria = contextCriteria;
		} finally {
			if (current == null) {
				QueryCriteriaContext.removeContextCriteria();
			} else {
				QueryCriteriaContext.setContextCriteria(current);
			}
		}

		return query;
	}
}
