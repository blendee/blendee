package org.blendee.assist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.blendee.jdbc.BConnection;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObject;
import org.blendee.orm.DataObjectIterator;
import org.blendee.orm.DataObjectNotFoundException;
import org.blendee.orm.SelectContext;
import org.blendee.orm.SelectedValuesConverter;
import org.blendee.orm.SimpleSelectContext;
import org.blendee.sql.Bindable;
import org.blendee.sql.Binder;
import org.blendee.sql.Column;
import org.blendee.sql.CombiningQuery;
import org.blendee.sql.ComplementerValues;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.FromClause;
import org.blendee.sql.FromClause.JoinType;
import org.blendee.sql.GroupByClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.SQL;
import org.blendee.sql.SQLQueryBuilder;
import org.blendee.sql.SQLQueryBuilder.CombineOperator;
import org.blendee.sql.SelectClause;
import org.blendee.sql.SelectCountClause;
import org.blendee.sql.SelectDistinctClause;
import org.blendee.sql.binder.NullBinder;

/**
 * {@link SelectStatement} の内部処理を定義したヘルパークラスです。
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
//@formatter:off
public abstract class SelectStatementBehavior<
	S extends SelectClauseAssist,
	LS extends ListSelectClauseAssist,
	G extends GroupByClauseAssist,
	LG extends ListGroupByClauseAssist,
	W extends WhereClauseAssist<?>,
	H extends HavingClauseAssist<?>,
	O extends OrderByClauseAssist,
	LO extends ListOrderByClauseAssist,
	L extends OnLeftClauseAssist<?>> {
//@formatter:on
	private final TablePath table;

	private final RuntimeId id;

	private CriteriaFactory factory;

	private boolean rowMode = true;

	private SelectContext selectContext;

	private SimpleSelectContext contextForSelect;

	private SelectClause selectClause;

	private Criteria whereClause;

	private GroupByClause groupByClause;

	private Criteria havingClause;

	private final List<CombiningQuery> combiningQueries = new ArrayList<>();

	private OrderByClause orderByClause;

	private FromClause fromClause;

	private final List<JoinResource> joinResources = new ArrayList<>();

	private final SQLDecorators decorators;

	private ComposedSQL sql;

	private boolean forSubquery;

	public SelectStatementBehavior(TablePath table, RuntimeId id, SQLDecorators decorators) {
		this.table = table;
		this.id = id;
		this.decorators = decorators;
	}

	SelectStatementBehavior(FromClause fromClause, SQLDecorators decorators) {
		this.fromClause = fromClause;
		id = fromClause.getRuntimeId();
		table = null;
		rowMode = false;
		this.decorators = decorators;
	}

	protected abstract S newSelect();

	protected abstract LS newListSelect();

	protected abstract G newGroupBy();

	protected abstract LG newListGroupBy();

	protected abstract O newOrderBy();

	protected abstract LO newListOrderBy();

	protected abstract LogicalOperators<W> newWhereOperators();

	protected abstract LogicalOperators<H> newHavingOperators();

	protected abstract LogicalOperators<L> newOnLeftOperators();

	private S select;

	private LS listSelect;

	private G groupBy;

	private LG listGroupBy;

	private O orderBy;

	private LO listOrderBy;

	private LogicalOperators<W> whereOperators;

	private LogicalOperators<H> havingOperators;

	private LogicalOperators<L> onLeftOperators;

	private S select() {
		return select == null ? (select = newSelect()) : select;
	}

	private LS listSelect() {
		return listSelect == null ? (listSelect = newListSelect()) : listSelect;
	}

	private G groupBy() {
		return groupBy == null ? (groupBy = newGroupBy()) : groupBy;
	}

	private LG listGroupBy() {
		return listGroupBy == null ? (listGroupBy = newListGroupBy()) : listGroupBy;
	}

	private O orderBy() {
		return orderBy == null ? (orderBy = newOrderBy()) : orderBy;
	}

	private LO listOrderBy() {
		return listOrderBy == null ? (listOrderBy = newListOrderBy()) : listOrderBy;
	}

	public LogicalOperators<W> whereOperators() {
		return whereOperators == null ? (whereOperators = newWhereOperators()) : whereOperators;
	}

	public LogicalOperators<H> havingOperators() {
		return havingOperators == null ? (havingOperators = newHavingOperators()) : havingOperators;
	}

	public LogicalOperators<L> onLeftOperators() {
		return onLeftOperators == null ? (onLeftOperators = newOnLeftOperators()) : onLeftOperators;
	}

	public void selectClause(Consumer<LS> consumer) {
		consumer.accept(listSelect());
	}

	public void groupByClause(Consumer<LG> consumer) {
		consumer.accept(listGroupBy());
	}

	public void orderByClause(Consumer<LO> consumer) {
		consumer.accept(listOrderBy());
	}

	/**
	 * SELECT 句を記述します。
	 * @param function {@link SelectOfferFunction}
	 */
	public void SELECT(SelectOfferFunction<S> function) {
		Offers<ColumnExpression> offers = function.apply(select());

		if (rowMode) {
			if (contextForSelect == null)
				contextForSelect = new SimpleSelectContext(table, id);

			offers.get().forEach(c -> c.accept(contextForSelect));

			selectContext = contextForSelect;
		}

		if (selectClause == null)
			selectClause = new SelectClause(id);

		offers.get().forEach(c -> c.accept(selectClause));
	}

	/**
	 * DISTINCT を使用した SELECT 句を記述します。
	 * @param function {@link SelectOfferFunction}
	 */
	public void SELECT_DISTINCT(
		SelectOfferFunction<S> function) {
		quitRowMode();

		Offers<ColumnExpression> offers = function.apply(select());

		SelectDistinctClause mySelectClause = new SelectDistinctClause(id);
		offers.get().forEach(c -> c.accept(mySelectClause));
		selectClause = mySelectClause;
	}

	/**
	 * COUNT(*) を使用した SELECT 句を記述します。
	 */
	public void SELECT_COUNT() {
		quitRowMode();
		selectClause = new SelectCountClause();
	}

	/**
	 * GROUP BY 句を記述します。
	 * @param function {@link GroupByOfferFunction}
	 */
	public void GROUP_BY(
		GroupByOfferFunction<G> function) {
		quitRowMode();
		function.apply(groupBy()).get().forEach(o -> o.add());
	}

	/**
	 * WHERE 句を記述します。
	 * @param consumers {@link Consumer}
	 */
	@SafeVarargs
	public final void WHERE(
		Consumer<W>... consumers) {
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

	/**
	 * HAVING 句を記述します。
	 * @param consumers {@link Consumer}
	 */
	@SafeVarargs
	public final void HAVING(
		Consumer<H>... consumers) {
		quitRowMode();

		//二重に呼ばれた際の処置
		Criteria current = CriteriaContext.getContextCriteria();
		try {
			for (Consumer<H> consumer : consumers) {
				Criteria contextCriteria = factory().create();
				CriteriaContext.setContextCriteria(contextCriteria);

				consumer.accept(havingOperators().defaultOperator());

				havingClause().and(contextCriteria);
			}
		} finally {
			if (current == null) {
				CriteriaContext.removeContextCriteria();
			} else {
				CriteriaContext.setContextCriteria(current);
			}
		}
	}

	/**
	 * ORDER BY 句を記述します。
	 * @param function {@link OrderByOfferFunction}
	 */
	public void ORDER_BY(
		OrderByOfferFunction<O> function) {
		function.apply(orderBy()).get().forEach(o -> o.add());
	}

	public <R extends OnRightClauseAssist<?>, Q extends SelectStatement> OnClause<L, R, Q> INNER_JOIN(RightTable<R> right, Q query) {
		return joinAndCreateOnClause(JoinType.INNER_JOIN, right, query);
	}

	public <R extends OnRightClauseAssist<?>, Q extends SelectStatement> OnClause<L, R, Q> LEFT_OUTER_JOIN(RightTable<R> right, Q query) {
		return joinAndCreateOnClause(JoinType.LEFT_OUTER_JOIN, right, query);
	}

	public <R extends OnRightClauseAssist<?>, Q extends SelectStatement> OnClause<L, R, Q> RIGHT_OUTER_JOIN(RightTable<R> right, Q query) {
		return joinAndCreateOnClause(JoinType.RIGHT_OUTER_JOIN, right, query);
	}

	public <R extends OnRightClauseAssist<?>, Q extends SelectStatement> OnClause<L, R, Q> FULL_OUTER_JOIN(RightTable<R> right, Q query) {
		return joinAndCreateOnClause(JoinType.FULL_OUTER_JOIN, right, query);
	}

	public <R extends OnRightClauseAssist<?>, Q extends SelectStatement> void CROSS_JOIN(RightTable<R> right) {
		joinInternal(JoinType.CROSS_JOIN, right).onCriteria = new CriteriaFactory(id).create();
	}

	public void UNION(SelectStatement select) {
		quitRowMode();
		select.quitRowMode();
		combiningQueries.add(new CombiningQuery(CombineOperator.UNION, select.composeSQL()));
	}

	public void UNION_ALL(SelectStatement select) {
		quitRowMode();
		select.quitRowMode();
		combiningQueries.add(new CombiningQuery(CombineOperator.UNION_ALL, select.composeSQL()));
	}

	public void INTERSECT(SelectStatement select) {
		quitRowMode();
		select.quitRowMode();
		combiningQueries.add(new CombiningQuery(CombineOperator.INTERSECT, select.composeSQL()));
	}

	public void INTERSECT_ALL(SelectStatement select) {
		quitRowMode();
		select.quitRowMode();
		combiningQueries.add(new CombiningQuery(CombineOperator.INTERSECT_ALL, select.composeSQL()));
	}

	public void EXCEPT(SelectStatement select) {
		quitRowMode();
		select.quitRowMode();
		combiningQueries.add(new CombiningQuery(CombineOperator.EXCEPT, select.composeSQL()));
	}

	public void EXCEPT_ALL(SelectStatement select) {
		quitRowMode();
		select.quitRowMode();
		combiningQueries.add(new CombiningQuery(CombineOperator.EXCEPT_ALL, select.composeSQL()));
	}

	public void and(Criteria whereClause) {
		whereClause().and(whereClause);
	}

	public void or(Criteria whereClause) {
		whereClause().or(whereClause);
	}

	/**
	 * WHERE 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 * @return 作成された {@link Criteria}
	 */
	public Criteria createWhereCriteria(
		Consumer<W> consumer) {
		//二重に呼ばれた際の処置
		Criteria current = CriteriaContext.getContextCriteria();
		try {
			Criteria criteria = factory().create();
			CriteriaContext.setContextCriteria(criteria);

			consumer.accept(whereOperators().defaultOperator());

			return criteria;
		} finally {
			if (current == null) {
				CriteriaContext.removeContextCriteria();
			} else {
				CriteriaContext.setContextCriteria(current);
			}
		}
	}

	/**
	 * HAVING 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 * @return 作成された {@link Criteria}
	 */
	public Criteria createHavingCriteria(
		Consumer<H> consumer) {
		//二重に呼ばれた際の処置
		Criteria current = CriteriaContext.getContextCriteria();
		try {
			Criteria criteria = factory().create();
			CriteriaContext.setContextCriteria(criteria);

			consumer.accept(havingOperators().defaultOperator());

			return criteria;
		} finally {
			if (current == null) {
				CriteriaContext.removeContextCriteria();
			} else {
				CriteriaContext.setContextCriteria(current);
			}
		}
	}

	public void quitRowMode() {
		rowMode = false;
	}

	public boolean rowMode() {
		return rowMode;
	}

	public void checkRowMode() {
		checkRowMode(rowMode);
	}

	public static void checkRowMode(boolean rowMode) {
		//集計モードでは実行できない処理です
		if (!rowMode) throw new IllegalStateException("This operation is deny in \"Row Mode\".");
	}

	public SelectContext getSelectContext() {
		if (selectContext != null) return selectContext;
		selectContext = new SimpleSelectContext(table, id);
		return selectContext;
	}

	public void setSelectContext(SelectContext selectContext) {
		this.selectContext = selectContext;
	}

	public SelectClause getSelectClause() {
		return selectClause;
	}

	/**
	 * @return WHERE
	 */
	public Criteria getWhereClause() {
		return whereClause;
	}

	public boolean hasWhereClause() {
		return whereClause != null && whereClause.isAvailable();
	}

	/**
	 * @return GROUP BY
	 */
	public GroupByClause getGroupByClause() {
		if (groupByClause == null) groupByClause = new GroupByClause(id);
		return groupByClause;
	}

	/**
	 * @param groupByClause GROUP BY
	 */
	public void setGroupByClause(GroupByClause groupByClause) {
		if (groupByClause != null)
			//既に GROUP BY 句がセットされています
			throw new IllegalStateException("GROUP BY clause already set");

		quitRowMode();
		this.groupByClause = groupByClause;
	}

	/**
	 * @return HAVING
	 */
	public Criteria getHavingClause() {
		return havingClause;
	}

	/**
	 * @return ORDER BY
	 */
	public OrderByClause getOrderByClause() {
		if (orderByClause == null) orderByClause = new OrderByClause(id);
		return orderByClause;
	}

	/**
	 * @param orderByClause ORDER BY
	 */
	public void setOrderByClause(OrderByClause orderByClause) {
		if (orderByClause != null)
			//既に ORDER BY 句がセットされています
			throw new IllegalStateException("ORDER BY clause already set");
		this.orderByClause = orderByClause;
	}

	/**
	 * 現在保持している WHERE 句をリセットします。
	 */
	public void resetWhere() {
		whereClause = null;
	}

	/**
	 * 現在保持している HAVING 句をリセットします。
	 */
	public void resetHaving() {
		havingClause = null;
	}

	/**
	 * 現在保持している SELECT 句をリセットします。
	 */
	public void resetSelect() {
		selectContext = null;
		contextForSelect = null;
		selectClause = null;
	}

	/**
	 * 現在保持している GROUP BY 句をリセットします。
	 */
	public void resetGroupBy() {
		groupByClause = null;
	}

	/**
	 * 現在保持している ORDER BY 句をリセットします。
	 */
	public void resetOrderBy() {
		orderByClause = null;
	}

	public void resetUnions() {
		combiningQueries.clear();
	}

	public void resetJoins() {
		joinResources.clear();
	}

	/**
	 * 現在保持している条件、並び順をリセットします。
	 */
	public void reset() {
		resetSelect();
		whereClause = null;
		havingClause = null;
		groupByClause = null;
		fromClause = null;
		joinResources.clear();
		combiningQueries.clear();
		orderByClause = null;
		rowMode = true;
		sql = null;
		forSubquery = false;
	}

	/**
	 * @return {@link ComposedSQL}
	 */
	public ComposedSQL composeSQL() {
		if (sql == null)
			sql = createComposedSQL();

		return sql;
	}

	public void forSubquery(boolean forSubquery) {
		this.forSubquery = forSubquery;
	}

	@Override
	public String toString() {
		//実行したことで影響を及ぼさないようにcreateComposedSQLを使用する
		return createComposedSQL().sql();
	}

	public static class PlaybackQuery implements Query<DataObjectIterator, DataObject> {

		private static final SelectedValuesConverter disabledSelectContext = (r, c) -> {
			throw new UnsupportedOperationException();
		};

		private final String rowSQL;

		private final String countSQL;

		private final String fetchSQL;

		private final String aggregationSQL;

		private final ComplementerValues values;

		private final Relationship relationship;

		private final Column[] selectedColumns;

		private final SelectedValuesConverter converter;

		private final boolean rowMode;

		private PlaybackQuery(
			String rowSQL,
			String countSQL,
			String fetchSQL,
			String aggregationSQL,
			ComplementerValues values,
			Relationship relationship,
			Column[] selectedColumns,
			SelectedValuesConverter converter,
			boolean rowMode) {
			this.rowSQL = rowSQL;
			this.countSQL = countSQL;
			this.fetchSQL = fetchSQL;
			this.aggregationSQL = aggregationSQL;
			this.values = values;
			this.relationship = relationship;
			this.selectedColumns = selectedColumns;
			this.converter = converter;
			this.rowMode = rowMode;
		}

		private PlaybackQuery(
			String rowSQL,
			String countSQL,
			String fetchSQL,
			String aggregationSQL,
			ComplementerValues values,
			Relationship relationship,
			Column[] selectedColumns,
			boolean rowMode) {
			this.rowSQL = rowSQL;
			this.countSQL = countSQL;
			this.fetchSQL = fetchSQL;
			this.aggregationSQL = aggregationSQL;
			this.values = values;
			this.relationship = relationship;
			this.selectedColumns = selectedColumns;
			converter = disabledSelectContext;
			this.rowMode = rowMode;
		}

		@Override
		public DataObjectIterator retrieve() {
			checkRowMode(rowMode);
			return DataAccessHelper.select(
				rowSQL,
				values,
				relationship,
				selectedColumns,
				converter);
		}

		@Override
		public Optional<DataObject> fetch(Bindable... primaryKeyMembers) {
			checkRowMode(rowMode);
			DataObject object;
			try {
				object = DataAccessHelper.getFirst(
					DataAccessHelper.select(
						fetchSQL,
						s -> {
							List<Binder> binders = values.binders();
							if (binders.size() > 0) {
								List<Bindable> bindables = new ArrayList<>();
								bindables.addAll(values.binders());
								bindables.addAll(Arrays.asList(primaryKeyMembers));
								for (int i = 0; i < bindables.size(); i++) {
									bindables.get(i).toBinder().bind(i + 1, s);
								}
							} else {
								for (int i = 0; i < primaryKeyMembers.length; i++) {
									primaryKeyMembers[i].toBinder().bind(i + 1, s);
								}
							}
						},
						relationship,
						selectedColumns,
						converter));
			} catch (DataObjectNotFoundException e) {
				return Optional.empty();
			}

			return Optional.ofNullable(object);
		}

		@Override
		public int count() {
			checkRowMode(rowMode);
			BConnection connection = BlendeeManager.getConnection();
			try (BStatement statement = connection.getStatement(countSQL, values)) {
				try (BResultSet result = statement.executeQuery()) {
					result.next();
					return result.getInt(1);
				}
			}
		}

		@Override
		public ComposedSQL countSQL() {
			checkRowMode(rowMode);
			return new ComposedSQL() {

				@Override
				public String sql() {
					return countSQL;
				}

				@Override
				public int complement(int done, BPreparedStatement statement) {
					return values.complement(done, statement);
				}
			};
		}

		@Override
		public boolean rowMode() {
			return rowMode;
		}

		@Override
		public String sql() {
			return rowMode ? rowSQL : aggregationSQL;
		}

		@Override
		public int complement(int done, BPreparedStatement statement) {
			return values.complement(done, statement);
		}

		@Override
		public PlaybackQuery reproduce(Object... placeHolderValues) {
			return new PlaybackQuery(
				rowSQL,
				countSQL,
				fetchSQL,
				aggregationSQL,
				values.reproduce(placeHolderValues),
				relationship,
				selectedColumns,
				converter,
				rowMode);
		}

		@Override
		public PlaybackQuery reproduce() {
			return new PlaybackQuery(
				rowSQL,
				countSQL,
				fetchSQL,
				aggregationSQL,
				values.reproduce(),
				relationship,
				selectedColumns,
				converter,
				rowMode);
		}

		@Override
		public Binder[] currentBinders() {
			return values.currentBinders();
		}

		@Override
		public ComposedSQL aggregateSQL() {
			return rowMode ? SQL.getInstance(aggregationSQL, values) : this;
		}
	}

	public PlaybackQuery query() {
		SQLQueryBuilder builder = buildBuilder();
		String aggregationSQL = builder.sql();

		if (!rowMode) {
			return new PlaybackQuery(
				null,
				null,
				null,
				aggregationSQL,
				ComplementerValues.of(builder),
				null,
				Column.EMPTY_ARRAY,
				false);
		}

		SelectContext context = getSelectContext();

		SQLQueryBuilder selector = new DataAccessHelper(id).buildSQLQueryBuilder(
			context,
			whereClause,
			orderByClause,
			decorators.decorators());

		selector.forSubquery(forSubquery);

		String sql = selector.sql();

		String countSQL;
		{
			SQLQueryBuilder myBuilder = new SQLQueryBuilder(new FromClause(context.tablePath(), id));
			myBuilder.setSelectClause(new SelectCountClause());
			if (whereClause != null) myBuilder.setWhereClause(whereClause);
			countSQL = myBuilder.sql();
		}

		String fetchSQL;
		{
			Criteria criteria = createFetchCriteria(table);
			SQLQueryBuilder fetchSelector = new DataAccessHelper(id).buildSQLQueryBuilder(
				context,
				whereClause == null ? criteria : whereClause.and(criteria),
				null,
				decorators.decorators());

			fetchSelector.forSubquery(forSubquery);

			fetchSQL = fetchSelector.sql();
		}

		return new PlaybackQuery(
			sql,
			countSQL,
			fetchSQL,
			aggregationSQL,
			ComplementerValues.of(selector),
			RelationshipFactory.getInstance().getInstance(table),
			selector.getSelectClause().getColumns(),
			context,
			true);
	}

	public SQLQueryBuilder buildBuilder() {
		SQLQueryBuilder builder = buildBuilderWithoutSelectColumnsSupply();

		//builder同士JOINしてもなおSELECT句が空の場合
		if (!builder.hasSelectColumns())
			builder.setSelectClause(getSelectContext().selectClause());

		return builder;
	}

	public void joinTo(SQLQueryBuilder builder, JoinType joinType, Criteria onCriteria) {
		builder.join(joinType, buildBuilderWithoutSelectColumnsSupply(), onCriteria);
	}

	void setSelectClause(SelectClause selectClause) {
		this.selectClause = selectClause;
	}

	private ComposedSQL createComposedSQL() {
		if (rowMode) {
			SQLQueryBuilder selector = new DataAccessHelper(id).buildSQLQueryBuilder(
				getSelectContext(),
				whereClause,
				orderByClause,
				decorators.decorators());

			selector.forSubquery(forSubquery);

			return selector;
		}

		return buildBuilder();
	}

	private SQLQueryBuilder buildBuilderWithoutSelectColumnsSupply() {
		SQLQueryBuilder builder = new SQLQueryBuilder(false, getFromClause());

		builder.forSubquery(forSubquery);

		if (selectClause != null)
			builder.setSelectClause(selectClause);

		if (groupByClause != null) builder.setGroupByClause(groupByClause);
		if (whereClause != null) builder.setWhereClause(whereClause);
		if (havingClause != null) builder.setHavingClause(havingClause);

		combiningQueries.forEach(u -> builder.combine(u.getCombineOperator(), u.getSQL()));

		if (orderByClause != null) builder.setOrderByClause(orderByClause);

		builder.addDecorator(decorators.decorators());

		joinResources.forEach(r -> r.rightRoot.joinTo(builder, r.joinType, r.onCriteria));

		return builder;
	}

	private FromClause getFromClause() {
		if (fromClause == null) fromClause = new FromClause(table, id);
		return fromClause;
	}

	private <R extends OnRightClauseAssist<?>, Q extends SelectStatement> JoinResource joinInternal(
		JoinType joinType,
		RightTable<R> right) {
		quitRowMode();

		JoinResource joinResource = new JoinResource();
		joinResource.rightRoot = right.getSelectStatement();
		joinResource.joinType = joinType;

		joinResources.add(joinResource);

		return joinResource;
	}

	private <R extends OnRightClauseAssist<?>, Q extends SelectStatement> OnClause<L, R, Q> joinAndCreateOnClause(
		JoinType joinType,
		RightTable<R> right,
		Q query) {
		JoinResource joinResource = joinInternal(joinType, right);

		return new OnClause<>(
			joinResource,
			onLeftOperators().defaultOperator(),
			right.joint(),
			query);
	}

	private Criteria whereClause() {
		if (whereClause == null) whereClause = factory().create();
		return whereClause;
	}

	private Criteria havingClause() {
		if (havingClause == null) havingClause = factory().create();
		return havingClause;
	}

	private CriteriaFactory factory() {
		if (factory == null) factory = new CriteriaFactory(id);
		return factory;
	}

	private Criteria createFetchCriteria(TablePath tablePath) {
		CriteriaFactory factory = factory();
		Criteria criteria = factory.create();

		for (Column column : RelationshipFactory.getInstance().getInstance(tablePath).getPrimaryKeyColumns()) {
			criteria.and(factory.create(column, new NullBinder(column.getColumnMetadata().getType())));
		}

		return criteria;
	}
}
