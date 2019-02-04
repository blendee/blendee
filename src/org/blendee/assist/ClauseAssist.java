package org.blendee.assist;

public interface ClauseAssist {

	default AssistColumn of(String expression, AssistColumn... columns) {
		return new AnyColumn(statement(), expression, columns);
	}

	default AssistColumn of(String expression, Object... values) {
		return new AnyColumn(statement(), expression, values);
	}

	default AssistColumn of(Object value) {
		return new AnyColumn(statement(), value);
	}

	default AssistColumn of(String expression, Vargs<AssistColumn> columns, Object... values) {
		return new AnyColumn(statement(), expression, columns.get(), values);
	}

	Statement statement();
}
