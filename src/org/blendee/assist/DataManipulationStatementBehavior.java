package org.blendee.assist;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.blendee.jdbc.TablePath;
import org.blendee.sql.Binder;
import org.blendee.sql.Column;
import org.blendee.sql.ComplementerValues;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.DeleteDMLBuilder;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.SQLQueryBuilder;
import org.blendee.sql.UpdateDMLBuilder;

@SuppressWarnings("javadoc")
//@formatter:off
public abstract class DataManipulationStatementBehavior<
	I extends InsertClauseAssist,
	LI extends ListInsertClauseAssist,
	U extends UpdateClauseAssist,
	LU extends ListUpdateClauseAssist<W>,
	W extends WhereClauseAssist<?>> implements DataManipulationStatement {
//@formatter:on

	private final TablePath table;

	private final RuntimeId id;

	private final SQLDecorators decorators;

	private List<Column> insertColumns;

	private List<SetElement> setElements;

	private Criteria whereClause;

	private LogicalOperators<W> whereOperators;

	private CriteriaFactory factory;

	private boolean forSubquery;

	public DataManipulationStatementBehavior(TablePath table, RuntimeId id, SQLDecorators decorators) {
		this.table = table;
		this.id = id;
		this.decorators = decorators;
	}

	public DataManipulator insertStatement(Function<LI, DataManipulator> function) {
		return function.apply(listInsert());
	}

	public DataManipulator updateStatement(Function<LU, DataManipulator> function) {
		return function.apply(listUpdate());
	}

	public InsertStatementIntermediate INSERT() {
		return new InsertStatementIntermediate(table, decorators, getInsertColumns());
	}

	public InsertStatementIntermediate INSERT(InsertOfferFunction<I> function) {
		function.apply(insert()).get().forEach(o -> o.offer());
		return INSERT();
	}

	public DataManipulator INSERT(InsertOfferFunction<I> function, SelectStatement select) {
		function.apply(insert()).get().forEach(o -> o.offer());

		SQLQueryBuilder builder = select.toSQLQueryBuilder();

		String sql = buildInsertStatement(builder);

		List<Binder> binders = new ComplementerValues(builder).binders();

		return new PlaybackDataManipulator(sql, binders);
	}

	public DataManipulator INSERT(SelectStatement select) {
		SQLQueryBuilder builder = select.toSQLQueryBuilder();

		String sql = buildInsertStatement(builder);

		List<Binder> binders = new ComplementerValues(builder).binders();

		return new PlaybackDataManipulator(sql, binders);
	}

	public UpdateStatementIntermediate<W> UPDATE() {
		return new UpdateStatementIntermediate<>(this);
	}

	public UpdateStatementIntermediate<W> UPDATE(Consumer<U> consumer) {
		consumer.accept(update());
		return UPDATE();
	}

	public DeleteStatementIntermediate<W> DELETE() {
		return new DeleteStatementIntermediate<>(this);
	}

	@Override
	public void addInsertColumns(Column column) {
		getInsertColumns().add(column);
	}

	@Override
	public void addSetElement(SetElement element) {
		getSetElements().add(element);
	}

	@Override
	public RuntimeId getRuntimeId() {
		return id;
	}

	@Override
	public void forSubquery(boolean forSubquery) {
		this.forSubquery = forSubquery;
	};

	@Override
	public LogicalOperators<?> getWhereLogicalOperators() {
		return whereOperators();
	}

	public void resetInsert() {
		insertColumns = null;
	}

	public void resetUpdate() {
		setElements = null;
		resetWhere();
	}

	public void resetDelete() {
		resetWhere();
	}

	private void resetWhere() {
		whereClause = null;
		whereOperators = null;
	}

	public void reset() {
		resetInsert();
		resetUpdate();
		resetWhere();
		forSubquery = false;
	}

	protected abstract I newInsert();

	protected abstract LI newListInsert();

	protected abstract U newUpdate();

	protected abstract LU newListUpdate();

	protected abstract LogicalOperators<W> newWhereOperators();

	private I insert;

	private LI listInsert;

	private U update;

	private LU listUpdate;

	private I insert() {
		return insert == null ? (insert = newInsert()) : insert;
	}

	private LI listInsert() {
		return listInsert == null ? (listInsert = newListInsert()) : listInsert;
	}

	private U update() {
		return update == null ? (update = newUpdate()) : update;
	}

	private LU listUpdate() {
		return listUpdate == null ? (listUpdate = newListUpdate()) : listUpdate;
	}

	@SafeVarargs
	final void WHERE(Consumer<W>... consumers) {
		//二重に呼ばれた際の処置
		Criteria current = CriteriaContext.getContextCriteria();
		try {
			for (Consumer<W> consumer : consumers) {
				Criteria contextCriteria = factory().create();
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
		DeleteDMLBuilder builder = new DeleteDMLBuilder(table, id, forSubquery);

		builder.addDecorator(decorators.decorators());
		builder.setCriteria(whereClause());

		List<Binder> binders = new ComplementerValues(builder).binders();

		return new PlaybackDataManipulator(builder.sql(), binders);
	}

	DataManipulator createUpdateDataManipulator() {
		UpdateDMLBuilder builder = new UpdateDMLBuilder(table, id, forSubquery);

		builder.addDecorator(decorators.decorators());
		builder.setCriteria(whereClause());

		getSetElements().forEach(e -> {
			e.appendTo(builder);
		});

		List<Binder> binders = new ComplementerValues(builder).binders();

		return new PlaybackDataManipulator(builder.sql(), binders);
	}

	private String buildInsertStatement(SQLQueryBuilder builder) {
		String columnsClause = insertColumns.size() > 0
			? " ("
				+ insertColumns.stream().map(c -> c.getName()).collect(Collectors.joining(", "))
				+ ")"
			: "";

		String sql = "INSERT INTO "
			+ table
			+ columnsClause
			+ " "
			+ builder.sql();

		for (SQLDecorator decorator : decorators.decorators()) {
			sql = decorator.decorate(sql);
		}

		return sql;
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
		if (whereClause == null) whereClause = factory().create();
		return whereClause;
	}

	private CriteriaFactory factory() {
		if (factory == null) factory = new CriteriaFactory(id);
		return factory;
	}
}
