package org.blendee.assist;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.blendee.sql.Binder;
import org.blendee.sql.Column;
import org.blendee.sql.MultiColumn;

class AnyColumn implements AssistColumn {

	private static final AssistColumn[] emptyColumns = {};

	private static final Object[] emptyValues = {};

	private final Statement statement;

	private final String expression;

	private final AssistColumn[] columns;

	private final Object[] values;

	AnyColumn(Statement statement, String expression, AssistColumn[] columns) {
		this.statement = statement;
		this.expression = Objects.requireNonNull(expression);
		this.columns = columns.clone();
		values = emptyValues;
	}

	AnyColumn(Statement statement, String expression, Object[] values) {
		this.statement = statement;
		this.expression = Objects.requireNonNull(expression);
		columns = emptyColumns;
		this.values = values.clone();
	}

	AnyColumn(Statement statement, Object value) {
		this.statement = statement;
		this.expression = "?";
		columns = emptyColumns;
		values = new Object[] { value };
	}

	AnyColumn(Statement statement, String expression, AssistColumn[] columns, Object[] values) {
		this.statement = statement;
		this.expression = Objects.requireNonNull(expression);
		this.columns = columns.clone();
		this.values = values.clone();
	}

	@Override
	public Binder[] values() {
		Binder[] binders = new Binder[values.length];

		BinderExtractor extractor = new BinderExtractor();

		for (int i = 0; i < values.length; i++) {
			binders[i] = extractor.extract(values[i]);
		}

		return binders;
	}

	@Override
	public Column column() {
		return new MultiColumn(expression, columns());
	}

	@Override
	public Statement statement() {
		return statement;
	}

	Column[] columns() {
		List<Column> list = Arrays.stream(columns).map(c -> c.column()).collect(Collectors.toList());
		return list.toArray(new Column[list.size()]);
	}
}
