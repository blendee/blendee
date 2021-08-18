package org.blendee.assist;

import java.util.function.BiConsumer;

import org.blendee.sql.CriteriaFactory;

/**
 * ON 句
 * @param <L> LEFT
 * @param <R> RIGHT
 * @param <S> SelectStatement
 */
public class OnClause<L extends OnLeftClauseAssist<?>, R extends OnRightClauseAssist<?>, S extends SelectStatement> {

	private final JoinResource resource;

	private final L left;

	private final R right;

	private final S selectStatement;

	OnClause(JoinResource resource, L left, R right, S selectStatement) {
		this.resource = resource;
		this.left = left;
		this.right = right;
		this.selectStatement = selectStatement;
	}

	/**
	 * @param consumers {@link BiConsumer}
	 * @return {@link SelectStatement}
	 */
	@SafeVarargs
	public final S ON(BiConsumer<L, R>... consumers) {
		//二重に呼ばれた際の処置
		var current = CriteriaContext.getContextCriteria();
		try {
			var contextCriteria = new CriteriaFactory(selectStatement.getRuntimeId()).create();
			CriteriaContext.setContextCriteria(contextCriteria);

			for (var consumer : consumers) {
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

		return selectStatement;
	}
}
