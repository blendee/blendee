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
public class OnClause<L extends OnLeftRelationship, R extends OnRightRelationship, Q extends SelectStatement> {

	private final JoinResource resource;

	private final L left;

	private final R right;

	private final Q queryBuilder;

	OnClause(JoinResource resource, L left, R right, Q queryBuilder) {
		this.resource = resource;
		this.left = left;
		this.right = right;
		this.queryBuilder = queryBuilder;
	}

	/**
	 * @param consumers {@link BiConsumer}
	 * @return {@link SelectStatement}
	 */
	@SafeVarargs
	public final Q ON(BiConsumer<L, R>... consumers) {
		//二重に呼ばれた際の処置
		Criteria current = CriteriaContext.getContextCriteria();
		try {
			Criteria contextCriteria = new CriteriaFactory(queryBuilder.getQueryId()).create();
			CriteriaContext.setContextCriteria(contextCriteria);

			for (BiConsumer<L, R> consumer : consumers) {
				consumer.accept(left, right);
			}

			resource.onCriteria = contextCriteria;
		} finally {
			if (current == null) {
				CriteriaContext.removeContextCriteria();
			} else {
				CriteriaContext.setContextCriteria(current);
			}
		}

		return queryBuilder;
	}
}
