package org.blendee.assist;

public interface ColumnMaker {

	default AssistColumn column(String expression, AssistColumn... columns) {
		return col(expression, columns);
	}

	default AssistColumn column(String expression, Object... values) {
		return col(expression, values);
	}

	default AssistColumn column(Object value) {
		return col(value);
	}

	default AssistColumn column(String expression, Vargs<AssistColumn> columns, Object... values) {
		return col(expression, columns.get(), values);
	}

	default AssistColumn col(String expression, AssistColumn... columns) {
		return new AnyColumn(statement(), expression, columns);
	}

	default AssistColumn col(String expression, Object... values) {
		return new AnyColumn(statement(), expression, values);
	}

	default AssistColumn col(Object value) {
		return new AnyColumn(statement(), value);
	}

	default AssistColumn col(String expression, Vargs<AssistColumn> columns, Object... values) {
		return new AnyColumn(statement(), expression, columns.get(), values);
	}

	Statement statement();
}
