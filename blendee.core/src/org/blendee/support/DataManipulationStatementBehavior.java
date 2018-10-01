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

@SuppressWarnings("javadoc")
public abstract class DataManipulationStatementBehavior<I extends InsertRelationship, W extends WhereRelationship> implements DataManipulationStatement {

	private final TablePath table;

	private List<Column> insertColumns;

	private Criteria whereClause;

	private LogicalOperators<W> whereOperators;

	public DataManipulationStatementBehavior(TablePath table) {
		this.table = table;
	}

	public InsertStatementIntermediate INSERT(InsertOfferFunction<I> function) {
		function.apply(newInsert()).get().forEach(o -> o.offer());
		return new InsertStatementIntermediate(table, insertColumns());
	}

	public DataManipulator INSERT(InsertOfferFunction<I> function, SelectStatement select) {
		function.apply(newInsert()).get().forEach(o -> o.offer());

		SQLQueryBuilder builder = select.toSQLQueryBuilder();

		String sql = "INSERT INTO "
			+ table
			+ " ("
			+ String.join(", ", insertColumns.stream().map(c -> c.getName()).collect(Collectors.toList()))
			+ ") "
			+ builder.sql();

		List<Binder> binders = new ComplementerValues(builder).binders();

		return new DataManipulator(sql, binders.toArray(new Binder[binders.size()]));
	}

	public DataManipulator INSERT(SelectStatement select) {
		SQLQueryBuilder builder = select.toSQLQueryBuilder();

		String sql = "INSERT INTO "
			+ table
			+ " "
			+ builder.sql();

		List<Binder> binders = new ComplementerValues(builder).binders();

		return new DataManipulator(sql, binders.toArray(new Binder[binders.size()]));
	}

	@Override
	public void addInsertColumns(Column column) {
		insertColumns().add(column);
	}

	@SafeVarargs
	public final DataManipulator DELETE(Consumer<W>... consumers) {
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

		DeleteDMLBuilder builder = new DeleteDMLBuilder(table);
		builder.setCriteria(whereClause());

		List<Binder> binders = new ComplementerValues(builder).binders();

		return new DataManipulator(builder.sql(), binders.toArray(new Binder[binders.size()]));
	}

	public LogicalOperators<W> whereOperators() {
		return whereOperators == null ? (whereOperators = newWhereOperators()) : whereOperators;
	}

	protected abstract I newInsert();

	protected abstract LogicalOperators<W> newWhereOperators();

	private List<Column> insertColumns() {
		if (insertColumns == null) insertColumns = new LinkedList<>();
		return insertColumns;
	}

	private Criteria whereClause() {
		if (whereClause == null) whereClause = CriteriaFactory.create();
		return whereClause;
	}
}
