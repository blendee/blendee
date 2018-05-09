package org.blendee.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.blendee.jdbc.BlenConnection;
import org.blendee.jdbc.BlenResultSet;
import org.blendee.jdbc.BlenStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.jdbc.ResultSetIterator;
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
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.FromClause;
import org.blendee.sql.FromClause.JoinType;
import org.blendee.sql.GroupByClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.QueryBuilder;
import org.blendee.sql.QueryBuilder.UnionOperator;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.SelectClause;
import org.blendee.sql.SelectCountClause;
import org.blendee.sql.SelectDistinctClause;

/**
 * {@link Query} の内部処理を定義したヘルパークラスです。
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public class QueryHelper<S extends SelectQueryRelationship, G extends GroupByQueryRelationship, W extends WhereQueryRelationship, H extends HavingQueryRelationship, O extends OrderByQueryRelationship, L extends OnLeftQueryRelationship> {

	private final TablePath table;

	private final S select;

	private final G groupBy;

	private final O orderBy;

	private final LogicalOperators<W> whereOperators;

	private final LogicalOperators<H> havingOperators;

	private final LogicalOperators<L> left;

	private boolean rowMode = true;

	private Optimizer optimizer;

	private RuntimeOptimizer optimizerForSelect;

	private SelectClause selectClause;

	private Criteria whereClause;

	private GroupByClause groupByClause;

	private Criteria havingClause;

	private final List<UnionContainer> unions = new ArrayList<>();;

	private OrderByClause orderByClause;

	private FromClause fromClause;

	private List<JoinResource> joinResources = new ArrayList<>();

	private List<SQLDecorator> decorators = new ArrayList<>();

	private static final Map<Class<?>, Playbackable<?>> cache = new HashMap<>();

	private Playbackable<?> playbackable;

	public static void regist(Class<?> lambdaClass, Playbackable<?> playbackable) {
		Objects.requireNonNull(lambdaClass);
		Objects.requireNonNull(playbackable);
		synchronized (cache) {
			cache.put(lambdaClass, playbackable);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Playbackable<T> get(Class<?> lambdaClass) {
		synchronized (cache) {
			return (Playbackable<T>) cache.get(lambdaClass);
		}
	};

	public QueryHelper(
		TablePath table,
		S select,
		G groupBy,
		O orderBy,
		LogicalOperators<W> whereOperators,
		LogicalOperators<H> havingOperators,
		LogicalOperators<L> onOperators) {
		this.table = table;
		this.select = select;
		this.groupBy = groupBy;
		this.orderBy = orderBy;
		this.whereOperators = whereOperators;
		this.havingOperators = havingOperators;
		left = onOperators;
	}

	/**
	 * SELECT 句を記述します。
	 * @param function {@link SelectOfferFunction}
	 */
	public void SELECT(SelectOfferFunction<S> function) {
		Offers<ColumnExpression> offers = function.apply(select);

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

		Offers<ColumnExpression> offers = function.apply(select);

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
		function.apply(groupBy).get().forEach(o -> o.offer());
	}

	/**
	 * WHERE 句を記述します。
	 * @param consumers {@link Consumer}
	 */
	@SafeVarargs
	public final void WHERE(
		Consumer<W>... consumers) {
		try {
			for (Consumer<W> consumer : consumers) {
				Criteria contextCriteria = CriteriaFactory.create();
				QueryCriteriaContext.setContextCriteria(contextCriteria);

				consumer.accept(whereOperators.defaultOperator());

				whereClause().and(contextCriteria);
			}

		} finally {
			QueryCriteriaContext.removeContextCriteria();
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
		try {
			for (Consumer<H> consumer : consumers) {
				Criteria contextCriteria = CriteriaFactory.create();
				QueryCriteriaContext.setContextCriteria(contextCriteria);

				consumer.accept(havingOperators.defaultOperator());

				havingClause().and(contextCriteria);
			}
		} finally {
			QueryCriteriaContext.removeContextCriteria();
		}
	}

	/**
	 * ORDER BY 句を記述します。
	 * @param function {@link OrderByOfferFunction}
	 */
	public void ORDER_BY(
		OrderByOfferFunction<O> function) {
		function.apply(orderBy).get().forEach(o -> o.offer());
	}

	public <R extends OnRightQueryRelationship, Q extends Query> QueryOnClause<L, R, Q> INNER_JOIN(R rightJoint, Q query) {
		return joinInternal(JoinType.INNER_JOIN, rightJoint, query);
	}

	public <R extends OnRightQueryRelationship, Q extends Query> QueryOnClause<L, R, Q> LEFT_OUTER_JOIN(R rightJoint, Q query) {
		return joinInternal(JoinType.LEFT_OUTER_JOIN, rightJoint, query);
	}

	public <R extends OnRightQueryRelationship, Q extends Query> QueryOnClause<L, R, Q> RIGHT_OUTER_JOIN(R rightJoint, Q query) {
		return joinInternal(JoinType.RIGHT_OUTER_JOIN, rightJoint, query);
	}

	public <R extends OnRightQueryRelationship, Q extends Query> QueryOnClause<L, R, Q> FULL_OUTER_JOIN(R rightJoint, Q query) {
		return joinInternal(JoinType.FULL_OUTER_JOIN, rightJoint, query);
	}

	public void UNION(ComposedSQL query) {
		quitRowMode();
		unions.add(new UnionContainer(UnionOperator.UNION, query));
	}

	public void UNION_ALL(ComposedSQL query) {
		quitRowMode();
		unions.add(new UnionContainer(UnionOperator.UNION_ALL, query));
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
		try {
			Criteria criteria = CriteriaFactory.create();
			QueryCriteriaContext.setContextCriteria(criteria);

			consumer.accept(whereOperators.defaultOperator());

			return criteria;
		} finally {
			QueryCriteriaContext.removeContextCriteria();
		}
	}

	/**
	 * HAVING 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 */
	public Criteria createHavingCriteria(
		Consumer<H> consumer) {
		try {
			Criteria criteria = CriteriaFactory.create();
			QueryCriteriaContext.setContextCriteria(criteria);

			consumer.accept(havingOperators.defaultOperator());

			return criteria;
		} finally {
			QueryCriteriaContext.removeContextCriteria();
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
		if (!rowMode()) throw new IllegalStateException("集計モードでは実行できない処理です");
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
	 * @return FROM
	 */
	public FromClause getFromClause() {
		if (fromClause == null) fromClause = new FromClause(table);
		return fromClause;
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

	public void resetUnion() {
		unions.clear();
	}

	/**
	 * 現在保持している ORDER BY 句をリセットします。
	 */
	public void resetOrderBy() {
		orderByClause = null;
	}

	/**
	 * 現在保持している条件、並び順をリセットします。
	 */
	public void reset() {
		resetSelect();
		whereClause = null;
		havingClause = null;
		groupByClause = null;
		unions.clear();
		orderByClause = null;
		rowMode = true;
	}

	/**
	 * @param options 検索オプション
	 * @return {@link ComposedSQL}
	 */
	public ComposedSQL composeSQL() {
		if (rowMode) {
			return new DataAccessHelper().getSelector(
				getOptimizer(),
				whereClause,
				orderByClause,
				decorators()).composeSQL();
		}

		return buildBuilder();
	}

	/**
	 * @return {@link QueryBuilder}
	 */
	public Subquery toSubquery() {
		return new Subquery(buildBuilder());
	}

	public static class HelperExecutor implements Executor<DataObjectIterator, DataObject> {

		private final String sql;

		private final String countSQL;

		private final PreparedStatementComplementer complementer;

		private final Relationship relationship;

		private final Column[] selectedColumns;

		private final SelectedValuesConverter converter = new SimpleSelectedValuesConverter();

		private HelperExecutor(
			String sql,
			String countSQL,
			PreparedStatementComplementer complementer,
			Relationship relationship,
			Column[] selectedColumns) {
			this.sql = sql;
			this.countSQL = countSQL;
			this.complementer = complementer;
			this.relationship = relationship;
			this.selectedColumns = selectedColumns;
		}

		@Override
		public DataObjectIterator execute() {
			return DataAccessHelper.select(
				sql,
				complementer,
				relationship,
				selectedColumns,
				converter);
		}

		@Override
		public DataObject willUnique() {
			throw new UnsupportedOperationException();
		}

		@Override
		public DataObject fetch(String... primaryKeyMembers) {
			throw new UnsupportedOperationException();
		}

		@Override
		public DataObject fetch(Number... primaryKeyMembers) {
			throw new UnsupportedOperationException();
		}

		@Override
		public DataObject fetch(Bindable... primaryKeyMembers) {
			DataObject object;
			try {
				object = DataAccessHelper.getFirst(
					DataAccessHelper.select(
						sql,
						s -> {
							for (int i = 0; i < primaryKeyMembers.length; i++) {
								primaryKeyMembers[i].toBinder().bind(i + 1, s);
							}
						},
						relationship,
						selectedColumns,
						converter));
			} catch (DataObjectNotFoundException e) {
				return null;
			}

			return object;
		}

		@Override
		public int count() {
			BlenConnection connection = BlendeeManager.getConnection();
			try (BlenStatement statement = connection.getStatement(countSQL, complementer)) {
				try (BlenResultSet result = statement.executeQuery()) {
					result.next();
					return result.getInt(1);
				}
			}
		}
	}

	private static class HelperAggregator implements Aggregator {

		private final String sql;

		private final PreparedStatementComplementer complementer;

		private HelperAggregator(String sql, PreparedStatementComplementer complementer) {
			this.sql = sql;
			this.complementer = complementer;
		}

		/**
		 * @param consumer {@link Consumer}
		 */
		@Override
		public void aggregate(Consumer<BlenResultSet> consumer) {
			BlenConnection connection = BlendeeManager.getConnection();
			try (BlenStatement statement = connection.getStatement(sql, complementer)) {
				try (BlenResultSet result = statement.executeQuery()) {
					consumer.accept(result);
				}
			}
		}

		/**
		 * @param function {@link Function}
		 */
		@Override
		public <T> T aggregateAndGet(Function<BlenResultSet, T> function) {
			BlenConnection connection = BlendeeManager.getConnection();
			try (BlenStatement statement = connection.getStatement(sql, complementer)) {
				try (BlenResultSet result = statement.executeQuery()) {
					return function.apply(result);
				}
			}
		}

		/**
		 * @param options 検索オプション
		 * @return {@link ResultSetIterator}
		 */
		@Override
		public ResultSetIterator aggregate() {
			return new ResultSetIterator(sql, complementer);
		}
	}

	public Aggregator registAndGetAggregator() {
		ComposedSQL sql = buildBuilder();

		String sqlString = sql.sql();

		Playbackable<Aggregator> myPlaybackable = complementer -> {
			return new HelperAggregator(sqlString, complementer);
		};

		playbackable = myPlaybackable;

		return myPlaybackable.play(sql);
	}

	public HelperExecutor registAndGetExecutor() {
		Selector selector = new DataAccessHelper().getSelector(
			getOptimizer(),
			whereClause,
			orderByClause,
			decorators());
		ComposedSQL composedSQL = selector.composeSQL();

		String sql = composedSQL.sql();
		String coutSql = toCountSQL(sql);

		Column[] selectedColumns = selector.getSelectClause().getColumns();

		Relationship relationship = ContextManager.get(RelationshipFactory.class).getInstance(table);

		Playbackable<HelperExecutor> myPlaybackable = complementer -> {
			return new HelperExecutor(sql, coutSql, complementer, relationship, selectedColumns);
		};

		playbackable = myPlaybackable;

		return myPlaybackable.play(composedSQL);
	}

	public Aggregator getAggregator() {
		ComposedSQL sql = buildBuilder();
		return new HelperAggregator(sql.sql(), sql);
	}

	public Playbackable<?> getPlaybackable() {
		return playbackable;
	}

	public void setPlaybackable(Playbackable<?> playbackable) {
		this.playbackable = playbackable;
	}

	public QueryBuilder buildBuilder() {
		QueryBuilder builder = buildBuilderWithoutSelectColumnsSupply();

		//builder同士JOINしてもなおSELECT句が空の場合
		if (!builder.hasSelectColumns())
			builder.setSelectClause(getOptimizer().getOptimizedSelectClause());

		return builder;
	}

	public void joinTo(QueryBuilder builder, JoinType joinType, Criteria onCriteria) {
		builder.join(joinType, buildBuilderWithoutSelectColumnsSupply(), onCriteria);
	}

	private QueryBuilder buildBuilderWithoutSelectColumnsSupply() {
		QueryBuilder builder = new QueryBuilder(false, getFromClause());

		if (selectClause != null)
			builder.setSelectClause(selectClause);

		if (groupByClause != null) builder.setGroupByClause(groupByClause);
		if (whereClause != null) builder.setWhereClause(whereClause);
		if (havingClause != null) builder.setHavingClause(havingClause);

		unions.forEach(u -> builder.union(u.unionOperator, u.query));

		if (orderByClause != null) builder.setOrderByClause(orderByClause);

		builder.addDecorator(decorators());

		joinResources.forEach(r -> r.rightRoot.joinTo(builder, r.joinType, r.onCriteria));

		return builder;
	}

	private <R extends OnRightQueryRelationship, Q extends Query> QueryOnClause<L, R, Q> joinInternal(
		JoinType joinType,
		R rightJoint,
		Q query) {
		quitRowMode();

		JoinResource joinResource = new JoinResource();
		joinResource.rightRoot = rightJoint.getRoot();
		joinResource.joinType = joinType;

		joinResources.add(joinResource);

		return new QueryOnClause<>(
			joinResource,
			left.defaultOperator(),
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

	private static String toCountSQL(String sql) {
		int fromPosition = sql.indexOf("FROM");
		return "SELECT COUNT(*) " + sql.substring(fromPosition);
	}

	private static class UnionContainer {

		private final UnionOperator unionOperator;

		private final ComposedSQL query;

		private UnionContainer(UnionOperator unionOperator, ComposedSQL query) {
			this.unionOperator = unionOperator;
			this.query = query;
		}
	}
}
