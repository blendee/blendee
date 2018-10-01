package org.blendee.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.blendee.jdbc.BConnection;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObject;
import org.blendee.orm.DataObjectIterator;
import org.blendee.orm.DataObjectNotFoundException;
import org.blendee.selector.Optimizer;
import org.blendee.selector.RuntimeOptimizer;
import org.blendee.selector.SelectedValuesConverter;
import org.blendee.selector.Selector;
import org.blendee.selector.SimpleOptimizer;
import org.blendee.selector.SimpleSelectedValuesConverter;
import org.blendee.sql.Bindable;
import org.blendee.sql.Column;
import org.blendee.sql.ComplementerValues;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.FromClause;
import org.blendee.sql.FromClause.JoinType;
import org.blendee.sql.GroupByClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.SelectClause;
import org.blendee.sql.SelectCountClause;
import org.blendee.sql.SelectDistinctClause;
import org.blendee.sql.SQLQueryBuilder;
import org.blendee.sql.SQLQueryBuilder.UnionOperator;
import org.blendee.sql.Union;
import org.blendee.sql.binder.NullBinder;

/**
 * {@link SelectStatement} の内部処理を定義したヘルパークラスです。
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public abstract class SelectStatementBehavior<S extends SelectRelationship, G extends GroupByRelationship, W extends WhereRelationship, H extends HavingRelationship, O extends OrderByRelationship, L extends OnLeftRelationship> {

	private final TablePath table;

	private boolean rowMode = true;

	private Optimizer optimizer;

	private RuntimeOptimizer optimizerForSelect;

	private SelectClause selectClause;

	private Criteria whereClause;

	private GroupByClause groupByClause;

	private Criteria havingClause;

	private final List<Union> unions = new ArrayList<>();

	private OrderByClause orderByClause;

	private FromClause fromClause;

	private final List<JoinResource> joinResources = new ArrayList<>();

	private final List<SQLDecorator> decorators = new ArrayList<>();

	private ComposedSQL sql;

	private boolean forSubquery;

	public SelectStatementBehavior(TablePath table) {
		this.table = table;
	}

	SelectStatementBehavior(FromClause fromClause) {
		this.fromClause = fromClause;
		table = null;
		rowMode = false;
	}

	protected abstract S newSelect();

	protected abstract G newGroupBy();

	protected abstract O newOrderBy();

	protected abstract LogicalOperators<W> newWhereOperators();

	protected abstract LogicalOperators<H> newHavingOperators();

	protected abstract LogicalOperators<L> newOnLeftOperators();

	private S select;

	private G groupBy;

	private O orderBy;

	private LogicalOperators<W> whereOperators;

	private LogicalOperators<H> havingOperators;

	private LogicalOperators<L> onLeftOperators;

	private S select() {
		return select == null ? (select = newSelect()) : select;
	}

	private G groupBy() {
		return groupBy == null ? (groupBy = newGroupBy()) : groupBy;
	}

	private O orderBy() {
		return orderBy == null ? (orderBy = newOrderBy()) : orderBy;
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

	/**
	 * SELECT 句を記述します。
	 * @param function {@link SelectOfferFunction}
	 */
	public void SELECT(SelectOfferFunction<S> function) {
		Offers<ColumnExpression> offers = function.apply(select());

		if (rowMode) {
			if (optimizerForSelect == null)
				optimizerForSelect = new RuntimeOptimizer(table);

			offers.get().forEach(c -> c.accept(optimizerForSelect));

			optimizer = optimizerForSelect;
		}

		if (selectClause == null)
			selectClause = new SelectClause();

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

		SelectDistinctClause mySelectClause = new SelectDistinctClause();
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
		function.apply(groupBy()).get().forEach(o -> o.offer());
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

	/**
	 * HAVING 句を記述します。
	 * @param consumer {@link Consumer}
	 */
	@SafeVarargs
	public final void HAVING(
		Consumer<H>... consumers) {
		quitRowMode();

		//二重に呼ばれた際の処置
		Criteria current = CriteriaContext.getContextCriteria();
		try {
			for (Consumer<H> consumer : consumers) {
				Criteria contextCriteria = CriteriaFactory.create();
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
		function.apply(orderBy()).get().forEach(o -> o.offer());
	}

	public <R extends OnRightRelationship, Q extends SelectStatement> OnClause<L, R, Q> INNER_JOIN(RightTable<R> right, Q query) {
		return joinInternal(JoinType.INNER_JOIN, right, query);
	}

	public <R extends OnRightRelationship, Q extends SelectStatement> OnClause<L, R, Q> LEFT_OUTER_JOIN(RightTable<R> right, Q query) {
		return joinInternal(JoinType.LEFT_OUTER_JOIN, right, query);
	}

	public <R extends OnRightRelationship, Q extends SelectStatement> OnClause<L, R, Q> RIGHT_OUTER_JOIN(RightTable<R> right, Q query) {
		return joinInternal(JoinType.RIGHT_OUTER_JOIN, right, query);
	}

	public <R extends OnRightRelationship, Q extends SelectStatement> OnClause<L, R, Q> FULL_OUTER_JOIN(RightTable<R> right, Q query) {
		return joinInternal(JoinType.FULL_OUTER_JOIN, right, query);
	}

	public void UNION(ComposedSQL query) {
		quitRowMode();
		unions.add(new Union(UnionOperator.UNION, query));
	}

	public void UNION_ALL(ComposedSQL query) {
		quitRowMode();
		unions.add(new Union(UnionOperator.UNION_ALL, query));
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
	 */
	public Criteria createWhereCriteria(
		Consumer<W> consumer) {
		//二重に呼ばれた際の処置
		Criteria current = CriteriaContext.getContextCriteria();
		try {
			Criteria criteria = CriteriaFactory.create();
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
	 */
	public Criteria createHavingCriteria(
		Consumer<H> consumer) {
		//二重に呼ばれた際の処置
		Criteria current = CriteriaContext.getContextCriteria();
		try {
			Criteria criteria = CriteriaFactory.create();
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

	public void apply(SQLDecorator[] decorators) {
		for (SQLDecorator decorator : decorators) {
			this.decorators.add(decorator);
		}
	}

	public SQLDecorator[] decorators() {
		if (decorators.size() == 0) return SQLDecorator.EMPTY_ARRAY;
		return decorators.toArray(new SQLDecorator[decorators.size()]);
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
		if (!rowMode) throw new IllegalStateException("集計モードでは実行できない処理です");
	}

	public Optimizer getOptimizer() {
		if (optimizer != null) return optimizer;
		optimizer = new SimpleOptimizer(table);
		return optimizer;
	}

	public void setOptimizer(Optimizer optimizer) {
		this.optimizer = optimizer;
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
		if (groupByClause == null) groupByClause = new GroupByClause();
		return groupByClause;
	}

	/**
	 * @param groupByClause GROUP BY
	 */
	public void setGroupByClause(GroupByClause groupByClause) {
		if (groupByClause != null)
			throw new IllegalStateException("既に GROUP BY 句がセットされています");

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
		if (orderByClause == null) orderByClause = new OrderByClause();
		return orderByClause;
	}

	/**
	 * @param orderByClause ORDER BY
	 */
	public void setOrderByClause(OrderByClause orderByClause) {
		if (orderByClause != null)
			throw new IllegalStateException("既に ORDER BY 句がセットされています");
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
		optimizer = null;
		optimizerForSelect = null;
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
		unions.clear();
	}

	public void resetJoins() {
		joinResources.clear();
	}

	public void resetDecorators() {
		decorators.clear();
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
		unions.clear();
		orderByClause = null;
		decorators.clear();
		rowMode = true;
		sql = null;
		forSubquery = false;
	}

	/**
	 * @param options 検索オプション
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

		private final String sql;

		private final String countSQL;

		private final String fetchSQL;

		private final ComplementerValues values;

		private final Relationship relationship;

		private final Column[] selectedColumns;

		private final SelectedValuesConverter converter = new SimpleSelectedValuesConverter();

		private final boolean rowMode;

		private PlaybackQuery(
			String sql,
			String countSQL,
			String fetchSQL,
			ComplementerValues values,
			Relationship relationship,
			Column[] selectedColumns,
			boolean rowMode) {
			this.sql = sql;
			this.countSQL = countSQL;
			this.fetchSQL = fetchSQL;
			this.values = values;
			this.relationship = relationship;
			this.selectedColumns = selectedColumns;
			this.rowMode = rowMode;
		}

		@Override
		public DataObjectIterator execute() {
			checkRowMode(rowMode);
			return DataAccessHelper.select(
				sql,
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
							for (int i = 0; i < primaryKeyMembers.length; i++) {
								primaryKeyMembers[i].toBinder().bind(i + 1, s);
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
		public ComposedSQL toCountSQL() {
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
			return sql;
		}

		@Override
		public int complement(int done, BPreparedStatement statement) {
			return values.complement(done, statement);
		}

		@Override
		public PlaybackQuery reproduce(Object... placeHolderValues) {
			return new PlaybackQuery(
				sql,
				countSQL,
				fetchSQL,
				values.reproduce(placeHolderValues),
				relationship,
				selectedColumns,
				rowMode);
		}
	}

	public PlaybackQuery query() {
		if (rowMode) {
			Optimizer optimizer = getOptimizer();

			Selector selector = new DataAccessHelper().getSelector(
				optimizer,
				whereClause,
				orderByClause,
				decorators());

			selector.forSubquery(forSubquery);

			ComposedSQL composedSQL = selector.composeSQL();

			String sql = composedSQL.sql();

			String countSQL;
			{
				SQLQueryBuilder builder = new SQLQueryBuilder(new FromClause(optimizer.getTablePath()));
				builder.setSelectClause(new SelectCountClause());
				if (whereClause != null) builder.setWhereClause(whereClause);
				countSQL = builder.sql();
			}

			String fetchSQL;
			{
				Selector fetchSelector = new DataAccessHelper().getSelector(
					optimizer,
					createFetchCriteria(table),
					null,
					decorators());

				fetchSelector.forSubquery(forSubquery);

				fetchSQL = fetchSelector.composeSQL().sql();
			}

			return new PlaybackQuery(
				sql,
				countSQL,
				fetchSQL,
				new ComplementerValues(composedSQL),
				ContextManager.get(RelationshipFactory.class).getInstance(table),
				selector.getSelectClause().getColumns(),
				rowMode);
		}

		SQLQueryBuilder builder = buildBuilder();

		String sql = builder.sql();

		return new PlaybackQuery(
			sql,
			null,
			null,
			new ComplementerValues(builder),
			null,
			Column.EMPTY_ARRAY,
			rowMode);
	}

	public SQLQueryBuilder buildBuilder() {
		SQLQueryBuilder builder = buildBuilderWithoutSelectColumnsSupply();

		//builder同士JOINしてもなおSELECT句が空の場合
		if (!builder.hasSelectColumns())
			builder.setSelectClause(getOptimizer().getOptimizedSelectClause());

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
			Selector selector = new DataAccessHelper().getSelector(
				getOptimizer(),
				whereClause,
				orderByClause,
				decorators());

			selector.forSubquery(forSubquery);

			return selector.composeSQL();
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

		unions.forEach(u -> builder.union(u.getUnionOperator(), u.getSQL()));

		if (orderByClause != null) builder.setOrderByClause(orderByClause);

		builder.addDecorator(decorators());

		joinResources.forEach(r -> r.rightRoot.joinTo(builder, r.joinType, r.onCriteria));

		return builder;
	}

	private FromClause getFromClause() {
		if (fromClause == null) fromClause = new FromClause(table);
		return fromClause;
	}

	private <R extends OnRightRelationship, Q extends SelectStatement> OnClause<L, R, Q> joinInternal(
		JoinType joinType,
		RightTable<R> right,
		Q query) {
		quitRowMode();

		R rightJoint = right.joint();

		JoinResource joinResource = new JoinResource();
		joinResource.rightRoot = rightJoint.getSelectStatement();
		joinResource.joinType = joinType;

		joinResources.add(joinResource);

		return new OnClause<>(
			joinResource,
			onLeftOperators().defaultOperator(),
			rightJoint,
			query);
	}

	private Criteria whereClause() {
		if (whereClause == null) whereClause = CriteriaFactory.create();
		return whereClause;
	}

	private Criteria havingClause() {
		if (havingClause == null) havingClause = CriteriaFactory.create();
		return havingClause;
	}

	private static Criteria createFetchCriteria(TablePath tablePath) {
		Criteria criteria = CriteriaFactory.create();

		for (Column column : ContextManager.get(RelationshipFactory.class).getInstance(tablePath).getPrimaryKeyColumns()) {
			criteria.and(CriteriaFactory.create(column, new NullBinder(column.getColumnMetadata().getType())));
		}

		return criteria;
	}
}
