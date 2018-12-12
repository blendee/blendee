package org.blendee.assist;

import java.util.Objects;
import java.util.function.Consumer;

import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.RuntimeId;

@SuppressWarnings("javadoc")
public class Paren {

	public static <R> void execute(RuntimeId id, CriteriaContext context, Consumer<R> consumer, R relationship) {
		Criteria current = CriteriaContext.getContextCriteria();

		Objects.requireNonNull(current);

		Criteria contextCriteria = new CriteriaFactory(id).create();
		CriteriaContext.setContextCriteria(contextCriteria);

		consumer.accept(relationship);

		CriteriaContext.setContextCriteria(current);
		context.addCriteria(contextCriteria);
	}
}
