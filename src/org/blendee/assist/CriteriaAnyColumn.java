package org.blendee.assist;

@SuppressWarnings("javadoc")
public class CriteriaAnyColumn<T extends LogicalOperators<?>> extends AnyColumn implements CriteriaAssistColumn<T> {

	public CriteriaAnyColumn(Statement statement, String expression, AssistColumn[] columns) {
		super(statement, expression, columns);
	}

	public CriteriaAnyColumn(Statement statement, String expression, Object[] values) {
		super(statement, expression, values);
	}

	public CriteriaAnyColumn(Statement statement, Object value) {
		super(statement, value);
	}

	public CriteriaAnyColumn(Statement statement, String expression, AssistColumn[] columns, Object[] values) {
		super(statement, expression, columns, values);
	}
}
