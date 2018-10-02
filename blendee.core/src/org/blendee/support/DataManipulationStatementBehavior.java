package org.blendee.support;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.blendee.jdbc.TablePath;
import org.blendee.sql.Binder;
import org.blendee.sql.Column;
import org.blendee.sql.ComplementerValues;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.DeleteDMLBuilder;
import org.blendee.sql.SQLQueryBuilder;
import org.blendee.sql.UpdateDMLBuilder;

@SuppressWarnings("javadoc")
public abstract class DataManipulationStatementBehavior<I extends InsertRelationship, U extends UpdateRelationship, W extends WhereRelationship> implements DataManipulationStatement {

	private final TablePath table;

	private List<Column> insertColumns;

	private List<SetElement> setElements;

	private Criteria whereClause;

	private LogicalOperators<W> whereOperators;

	public DataManipulationStatementBehavior(TablePath table) {
		this.table = table;
	}

	public InsertStatementIntermediate INSERT(InsertOfferFunction<I> function) {
		function.apply(newInsert()).get().forEach(o -> o.offer());
		return new InsertStatementIntermediate(table, getInsertColumns());
	}

	public DataManipulator INSERT(InsertOfferFunction<I> function, SelectStatement select) {
		function.apply(newInsert()).get().forEach(o -> o.offer());

		SQLQueryBuilder builder = select.toSQLQueryBuilder();

		String sql = "INSERT INTO "
			+ table
			+ " ("
			+ insertColumns.stream().map(c -> c.getName()).collect(Collectors.joining(", "))
			+ ") "
			+ builder.sql();

		List<Binder> binders = new ComplementerValues(builder).binders();

		return new PlaybackDataManipulator(sql, binders);
	}

	public DataManipulator INSERT(SelectStatement select) {
		SQLQueryBuilder builder = select.toSQLQueryBuilder();

		String sql = "INSERT INTO "
			+ table
			+ " "
			+ builder.sql();

		List<Binder> binders = new ComplementerValues(builder).binders();

		return new PlaybackDataManipulator(sql, binders);
	}

	public UpdateStatementIntermediate<W> UPDATE(Consumer<U> consumer) {
		consumer.accept(newUpdate());
		return new UpdateStatementIntermediate<>(this);
	}

	@Override
	public void addInsertColumns(Column column) {
		getInsertColumns().add(column);
	}

	@Override
	public void addSetElement(SetElement element) {
		getSetElements().add(element);
	}

	protected abstract I newInsert();

	protected abstract U newUpdate();

	protected abstract LogicalOperators<W> newWhereOperators();

	@SafeVarargs
	final void WHERE(Consumer<W>... consumers) {
		//二重に呼ばれた際の処置
		Criteria current = CriteriaContext.getContextCriteria();
		try {
			for (Consumer<W> consumer : consumers) {
				Criteria contextCriteria = CriteriaFactory.create();
				CriteriaContext.setContextCriteria(contextCriteria);

				consumer.accept(whereOperators().defaultOperator());

				whereClause().and(contextCriteria);
			}
		} finally {
			if (current == null) {
				CriteriaContext.removeContextCriteria();
			} else {
				CriteriaContext.setContextCriteria(current);
			}
		}
	}

	DataManipulator createDeleteDataManipulator() {
		DeleteDMLBuilder builder = new DeleteDMLBuilder(table);
		builder.setCriteria(whereClause());

		List<Binder> binders = new ComplementerValues(builder).binders();

		return new PlaybackDataManipulator(builder.sql(), binders);
	}

	DataManipulator createUpdateDataManipulator() {
		UpdateDMLBuilder builder = new UpdateDMLBuilder(table);
		builder.setCriteria(whereClause());

		getSetElements().forEach(e -> {
			e.appendTo(builder);
		});

		List<Binder> binders = new ComplementerValues(builder).binders();

		return new PlaybackDataManipulator(builder.sql(), binders);
	}

	private LogicalOperators<W> whereOperators() {
		return whereOperators == null ? (whereOperators = newWhereOperators()) : whereOperators;
	}

	private List<Column> getInsertColumns() {
		if (insertColumns == null) insertColumns = new LinkedList<>();
		return insertColumns;
	}

	private List<SetElement> getSetElements() {
		if (setElements == null) setElements = new LinkedList<>();
		return setElements;
	}

	private Criteria whereClause() {
		if (whereClause == null) whereClause = CriteriaFactory.create();
		return whereClause;
	}
}
