package org.blendee.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.blendee.internal.U;
import org.blendee.jdbc.BlenConnection;
import org.blendee.jdbc.BlenResultSet;
import org.blendee.jdbc.BlenStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.Result;
import org.blendee.jdbc.ResultSetIterator;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObject;
import org.blendee.selector.AnchorOptimizerFactory;
import org.blendee.selector.Optimizer;
import org.blendee.selector.RuntimeOptimizer;
import org.blendee.selector.SimpleOptimizer;
import org.blendee.sql.Bindable;
import org.blendee.sql.Criteria;
import org.blendee.sql.Effector;
import org.blendee.sql.FromClause;
import org.blendee.sql.GroupByClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.QueryBuilder;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.SelectClause;
import org.blendee.sql.SelectCountClause;
import org.blendee.sql.SelectDistinctClause;
import org.blendee.support.Effectors;
import org.blendee.support.GroupByOfferFunction;
import org.blendee.support.GroupByQueryColumn;
import org.blendee.support.HavingQueryColumn;
import org.blendee.support.LogicalOperators;
import org.blendee.support.Many;
import org.blendee.support.NotUniqueException;
import org.blendee.support.OneToManyExecutor;
import org.blendee.support.OrderByOfferFunction;
import org.blendee.support.OrderByQueryColumn;
import org.blendee.support.Query;
import org.blendee.support.QueryColumn;
import org.blendee.support.QueryContext;
import org.blendee.support.QueryCriteriaContext;
import org.blendee.support.QueryRelationship;
import org.blendee.support.Row;
import org.blendee.support.SelectOffer;
import org.blendee.support.SelectOfferFunction;
import org.blendee.support.SelectOfferFunction.SelectOffers;
import org.blendee.support.SelectQueryColumn;
import org.blendee.support.Subquery;
import org.blendee.support.WhereQueryColumn;
import org.blendee.util.GenericManager.GenericRowIterator;

/**
 * {@link Row} の汎用実装クラスです。
 */
public class GenericQuery extends java.lang.Object implements Query {

	private static final QueryContext<MySelectQueryColumn> selectContext = (relationship, name) -> new MySelectQueryColumn(relationship, name);

	private static final QueryContext<MyGroupByQueryColumn> groupByContext = (relationship, name) -> new MyGroupByQueryColumn(relationship, name);

	private static final QueryContext<MyOrderByQueryColumn> orderByContext = (relationship, name) -> new MyOrderByQueryColumn(relationship, name);

	private static final QueryContext<WhereQueryColumn<WhereLogicalOperators>> whereContext = QueryContext.newWhereBuilder();

	private static final QueryContext<HavingQueryColumn<HavingLogicalOperators>> havingContext = QueryContext.newHavingBuilder();

	private final TablePath tablePath;

	private final GenericManager manager;

	/**
	 * WHERE 句 で使用する AND, OR です。
	 */
	public class WhereLogicalOperators implements LogicalOperators {

		private WhereLogicalOperators() {}

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final GenericRelationship<WhereQueryColumn<GenericQuery.WhereLogicalOperators>, Void> AND = new GenericRelationship<>(
			GenericQuery.this,
			whereContext,
			QueryCriteriaContext.WHERE_AND);

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final GenericRelationship<WhereQueryColumn<GenericQuery.WhereLogicalOperators>, Void> OR = new GenericRelationship<>(
			GenericQuery.this,
			whereContext,
			QueryCriteriaContext.WHERE_OR);
	}

	/**
	 * HAVING 句 で使用する AND, OR です。
	 */
	public class HavingLogicalOperators implements LogicalOperators {

		private HavingLogicalOperators() {}

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final GenericRelationship<HavingQueryColumn<GenericQuery.HavingLogicalOperators>, Void> AND = new GenericRelationship<>(
			GenericQuery.this,
			havingContext,
			QueryCriteriaContext.HAVING_AND);

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final GenericRelationship<HavingQueryColumn<GenericQuery.HavingLogicalOperators>, Void> OR = new GenericRelationship<>(
			GenericQuery.this,
			havingContext,
			QueryCriteriaContext.HAVING_OR);
	}

	private final WhereLogicalOperators whereOperators = new WhereLogicalOperators();

	private final HavingLogicalOperators havingOperators = new HavingLogicalOperators();

	/**
	 * この {@link Query} のテーブルを表す {@link QueryRelationship} を参照するためのインスタンスです。
	 */
	public final GenericRelationship<QueryColumn, Void> rel = new GenericRelationship<>(
		this,
		QueryContext.OTHER,
		QueryCriteriaContext.NULL);

	private Optimizer optimizer;

	private SelectClause selectClause;

	private Criteria whereClause;

	private Criteria havingClause;

	private GroupByClause groupByClause;

	private OrderByClause orderByClause;

	private SelectOfferFunction<?> selectClauseFunction;

	private GroupByOfferFunction<?> groupByClauseFunction;

	private OrderByOfferFunction<?> orderByClauseFunction;

	private Consumer<?> whereClauseConsumer;

	private Consumer<?> havingClauseConsumer;

	private boolean rowMode = true;

	/**
	 * ORDER BY 句用のカラムを選択するための {@link QueryRelationship} です。
	 */
	private final GenericRelationship<MySelectQueryColumn, Void> select = new GenericRelationship<>(
		this,
		selectContext,
		QueryCriteriaContext.NULL);

	/**
	 * ORDER BY 句用のカラムを選択するための {@link QueryRelationship} です。
	 */
	private final GenericRelationship<MyGroupByQueryColumn, Void> groupBy = new GenericRelationship<>(
		this,
		groupByContext,
		QueryCriteriaContext.NULL);

	/**
	 * ORDER BY 句用のカラムを選択するための {@link QueryRelationship} です。
	 */
	private final GenericRelationship<MyOrderByQueryColumn, Void> orderBy = new GenericRelationship<>(
		this,
		orderByContext,
		QueryCriteriaContext.NULL);

	/**
	 * このクラスのインスタンスを生成します。<br>
	 * インスタンスは ID として、引数で渡された id を使用します。<br>
	 * フィールド定義の必要がなく、簡易に使用できますが、 ID は呼び出し側クラス内で一意である必要があります。
	 * @param id {@link Query} を使用するクラス内で一意の ID
	 * @param tablePath 検索対象テーブル
	 * @return このクラスのインスタンス
	 */
	public static GenericQuery of(String id, TablePath tablePath) {
		if (!U.presents(id))
			throw new IllegalArgumentException("id が空です");

		return new GenericQuery(tablePath, getUsing(new Throwable().getStackTrace()[1]), id);
	}

	/**
	 * 空のインスタンスを生成します。
	 * @param tablePath 検索対象テーブル
	 */
	public GenericQuery(TablePath tablePath) {
		this.tablePath = tablePath;
		manager = new GenericManager(tablePath);
	}

	/**
	 * このクラスのインスタンスを生成します。<br>
	 * このコンストラクタで生成されたインスタンス の SELECT 句で使用されるカラムは、 パラメータの {@link Optimizer} に依存します。
	 * @param optimizer SELECT 句を決定する
	 */
	public GenericQuery(Optimizer optimizer) {
		this(optimizer.getTablePath());
		this.optimizer = Objects.requireNonNull(optimizer);
	}

	private GenericQuery(TablePath tablePath, Class<?> using, String id) {
		this(tablePath);
		optimizer = ContextManager.get(AnchorOptimizerFactory.class).getInstance(
			id,
			tablePath,
			using);
	}

	/**
	 * SELECT 句を記述します。
	 * @param function
	 * @return この {@link Query}
	 */
	public GenericQuery SELECT(
		SelectOfferFunction<GenericRelationship<MySelectQueryColumn, Void>> function) {
		if (selectClauseFunction == function) return this;

		SelectOffers offers = function.offer(select);

		if (rowMode) {
			RuntimeOptimizer myOptimizer = new RuntimeOptimizer(tablePath);
			offers.get().forEach(c -> c.accept(myOptimizer));
			optimizer = myOptimizer;
		} else {
			SelectClause mySelectClause = new SelectClause();
			offers.get().forEach(c -> c.accept(mySelectClause));
			selectClause = mySelectClause;
		}

		selectClauseFunction = function;
		return this;
	}

	/**
	 * SELECT 句を記述します。
	 * @param function
	 * @return この {@link Query}
	 */
	public GenericQuery SELECT_DISTINCT(
		SelectOfferFunction<GenericRelationship<MySelectQueryColumn, Void>> function) {
		if (selectClauseFunction == function) return this;

		quitRowMode();

		SelectOffers offers = function.offer(select);

		SelectDistinctClause mySelectClause = new SelectDistinctClause();
		offers.get().forEach(c -> c.accept(mySelectClause));
		selectClause = mySelectClause;

		selectClauseFunction = function;
		return this;
	}

	/**
	 * COUNT(*) を使用した SELECT 句を記述します。
	 * @return この {@link Query}
	 */
	public GenericQuery SELECT_COUNT() {
		quitRowMode();
		selectClause = new SelectCountClause();
		return this;
	}

	/**
	 * GROUP BY 句を記述します。
	 * @param function
	 * @return この {@link Query}
	 */
	public GenericQuery GROUP_BY(
		GroupByOfferFunction<GenericRelationship<MyGroupByQueryColumn, Void>> function) {
		if (groupByClauseFunction == function) return this;

		function.offer(groupBy);
		groupByClauseFunction = function;
		return this;
	}

	/**
	 * WHERE 句を記述します。
	 * @param consumer
	 * @return この {@link Query}
	 */
	public GenericQuery WHERE(
		Consumer<GenericRelationship<WhereQueryColumn<GenericQuery.WhereLogicalOperators>, Void>> consumer) {
		if (whereClauseConsumer == consumer) return this;

		consumer.accept(whereOperators.AND);
		whereClauseConsumer = consumer;
		return this;
	}

	/**
	 * HAVING 句を記述します。
	 * @param consumer
	 * @return この {@link Query}
	 */
	public GenericQuery HAVING(
		Consumer<GenericRelationship<HavingQueryColumn<GenericQuery.HavingLogicalOperators>, Void>> consumer) {
		if (havingClauseConsumer == consumer) return this;

		consumer.accept(havingOperators.AND);
		havingClauseConsumer = consumer;
		return this;
	}

	/**
	 * ORDER BY 句を記述します。
	 * @param function
	 * @return この {@link Query}
	 */
	public GenericQuery ORDER_BY(
		OrderByOfferFunction<GenericRelationship<MyOrderByQueryColumn, Void>> function) {
		if (orderByClauseFunction == function) return this;

		function.offer(orderBy);
		orderByClauseFunction = function;
		return this;
	}

	@Override
	public boolean hasWhereClause() {
		return whereClause != null && whereClause.isAvailable();
	}

	/**
	 * 新規に GROUP BY 句をセットします。
	 * @param clause 新 ORDER BY 句
	 * @return {@link Query} 自身
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public GenericQuery groupBy(GroupByClause clause) {
		quitRowMode();
		if (groupByClause != null)
			throw new IllegalStateException("既に GROUP BY 句がセットされています");
		groupByClause = clause;
		return this;
	}

	/**
	 * 新規に ORDER BY 句をセットします。
	 * @param clause 新 ORDER BY 句
	 * @return {@link Query} 自身
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public GenericQuery orderBy(OrderByClause clause) {
		if (orderByClause != null)
			throw new IllegalStateException("既に ORDER BY 句がセットされています");
		orderByClause = clause;
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たな条件を AND 結合します。<br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria AND 結合する新条件
	 * @return {@link Query} 自身
	 */
	public GenericQuery and(Criteria criteria) {
		QueryCriteriaContext.WHERE_AND.addCriteria(whereOperators.AND, criteria);
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たな条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria OR 結合する新条件
	 * @return {@link Query} 自身
	 */
	public GenericQuery or(Criteria criteria) {
		QueryCriteriaContext.WHERE_OR.addCriteria(whereOperators.OR, criteria);
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たなサブクエリ条件を AND 結合します。<br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 * @param subquery AND 結合するサブクエリ条件
	 * @return {@link Query} 自身
	 */
	public GenericQuery and(Subquery subquery) {
		QueryCriteriaContext.WHERE_AND.addCriteria(whereOperators.AND, subquery.createCriteria(this));
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たなサブクエリ条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param subquery OR 結合するサブクエリ条件
	 * @return {@link Query} 自身
	 */
	public GenericQuery or(Subquery subquery) {
		QueryCriteriaContext.WHERE_OR.addCriteria(whereOperators.OR, subquery.createCriteria(this));
		return this;
	}

	@Override
	public Relationship getRootRealtionship() {
		return ContextManager.get(RelationshipFactory.class).getInstance(manager.getTablePath());
	}

	@Override
	public LogicalOperators getWhereLogicalOperators() {
		return whereOperators;
	}

	@Override
	public LogicalOperators getHavingLogicalOperators() {
		return havingOperators;
	}

	@Override
	public GenericRowIterator execute() {
		return manager.select(getOptimizer(), whereClause, orderByClause);
	}

	@Override
	public GenericRowIterator execute(Effector... options) {
		return manager.select(getOptimizer(), whereClause, orderByClause, options);
	}

	@Override
	public Optional<GenericRow> willUnique() {
		return getUnique(execute());
	}

	@Override
	public Optional<GenericRow> willUnique(Effector... options) {
		return getUnique(execute(options));
	}

	@Override
	public Optional<GenericRow> fetch(String... primaryKeyMembers) {
		checkRowMode();
		return manager.select(getOptimizer(), primaryKeyMembers);
	}

	@Override
	public Optional<GenericRow> fetch(Number... primaryKeyMembers) {
		checkRowMode();
		return manager.select(getOptimizer(), primaryKeyMembers);
	}

	@Override
	public Optional<GenericRow> fetch(Bindable... primaryKeyMembers) {
		checkRowMode();
		return manager.select(getOptimizer(), primaryKeyMembers);
	}

	@Override
	public Optional<GenericRow> fetch(Effectors options, String... primaryKeyMembers) {
		checkRowMode();
		return manager.select(getOptimizer(), options, primaryKeyMembers);
	}

	@Override
	public Optional<GenericRow> fetch(Effectors options, Number... primaryKeyMembers) {
		checkRowMode();
		return manager.select(getOptimizer(), options, primaryKeyMembers);
	}

	@Override
	public Optional<GenericRow> fetch(Effectors options, Bindable... primaryKeyMembers) {
		checkRowMode();
		return manager.select(getOptimizer(), options, primaryKeyMembers);
	}

	@Override
	public void aggregate(Consumer<Result> consumer) {
		ComposedSQL sql = aggregateInternal(null);
		BlenConnection connection = ContextManager.get(BlendeeManager.class).getConnection();
		try (BlenStatement statement = connection.getStatement(sql)) {
			try (BlenResultSet result = statement.executeQuery()) {
				consumer.accept(result);
			}
		}

	}

	@Override
	public void aggregate(Effectors options, Consumer<Result> consumer) {
		ComposedSQL sql = aggregateInternal(options.get());
		BlenConnection connection = ContextManager.get(BlendeeManager.class).getConnection();
		try (BlenStatement statement = connection.getStatement(sql)) {
			try (BlenResultSet result = statement.executeQuery()) {
				consumer.accept(result);
			}

		}

	}

	@Override
	public ResultSetIterator aggregate(Effector... options) {
		ComposedSQL sql = aggregateInternal(options);
		return new ResultSetIterator(sql);
	}

	@Override
	public int count() {
		checkRowMode();
		return manager.count(whereClause);
	}

	@Override
	public Criteria getCriteria() {
		return whereClause.replicate();
	}

	@Override
	public void quitRowMode() {
		rowMode = false;
	}

	@Override
	public boolean rowMode() {
		return rowMode;
	}

	@Override
	public ComposedSQL composeSQL(Effector... options) {
		if (rowMode) {
			return new DataAccessHelper().getSelector(
				getOptimizer(),
				whereClause,
				orderByClause,
				options).composeSQL();
		}

		return aggregateInternal(null);
	}

	/**
	 * 現在保持している条件をリセットします。
	 * @return このインスタンス
	 */
	public GenericQuery resetWhere() {
		whereClause = null;
		whereClauseConsumer = null;
		return this;
	}

	/**
	 * 現在保持している HAVING 句をリセットします。
	 * @return このインスタンス
	 */
	public GenericQuery resetHaving() {
		havingClause = null;
		havingClauseConsumer = null;
		return this;
	}

	/**
	 * 現在保持している SELECT 句をリセットします。
	 * @return このインスタンス
	 */
	public GenericQuery resetSelect() {
		optimizer = null;
		selectClauseFunction = null;
		return this;
	}

	/**
	 * 現在保持している GROUP BY 句をリセットします。
	 * @return このインスタンス
	 */
	public GenericQuery resetGroupBy() {
		groupByClause = null;
		groupByClauseFunction = null;
		return this;
	}

	/**
	 * 現在保持している並び順をリセットします。
	 * @return このインスタンス
	 */
	public GenericQuery resetOrder() {
		orderByClause = null;
		orderByClauseFunction = null;
		return this;
	}

	/**
	 * 現在保持している条件、並び順をリセットします。
	 * @return このインスタンス
	 */
	public GenericQuery reset() {
		optimizer = null;
		selectClause = null;
		whereClause = null;
		havingClause = null;
		groupByClause = null;
		orderByClause = null;
		selectClauseFunction = null;
		groupByClauseFunction = null;
		orderByClauseFunction = null;
		whereClauseConsumer = null;
		havingClauseConsumer = null;
		rowMode = true;
		return this;
	}

	/**
	 * {@link OneToManyExecutor} の実装クラスです。
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class GenericExecutor<M>
		extends OneToManyExecutor<GenericRow, M> {

		private GenericExecutor(QueryRelationship parent) {
			super(parent);
		}
	}

	private Optimizer getOptimizer() {
		if (optimizer != null) return optimizer;
		optimizer = new SimpleOptimizer(tablePath);
		return optimizer;
	}

	private ComposedSQL aggregateInternal(Effector[] effectors) {
		QueryBuilder builder = new QueryBuilder(new FromClause(tablePath));

		builder.setSelectClause(selectClause);
		if (groupByClause != null) builder.setGroupByClause(groupByClause);
		if (whereClause != null) builder.setWhereClause(whereClause);
		if (havingClause != null) builder.setHavingClause(havingClause);
		if (orderByClause != null) builder.setOrderByClause(orderByClause);

		builder.addEffector(effectors);

		return builder;
	}

	private void checkRowMode() {
		if (!rowMode()) throw new IllegalStateException("集計モードでは実行できない処理です");
	}

	private static Class<?> getUsing(StackTraceElement element) {
		try {
			return Class.forName(element.getClassName());
		} catch (Exception e) {
			throw new IllegalStateException(e.toString());
		}
	}

	private static Optional<GenericRow> getUnique(GenericRowIterator iterator) {
		if (!iterator.hasNext()) return Optional.empty();

		GenericRow row = iterator.next();

		if (iterator.hasNext()) throw new NotUniqueException();

		return Optional.of(row);
	}

	/**
	 * {@link QueryRelationship} の実装クラスです。<br>
	 * 条件として使用できるカラムと、参照しているテーブルを内包しており、それらを使用して検索 SQL を生成可能にします。
	 * @param <T> 使用されるカラムのタイプにあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class GenericRelationship<T, M>
		implements QueryRelationship, SelectOffer {

		private final GenericQuery query;

		private final QueryCriteriaContext context;

		private final QueryRelationship parent;

		private final String fkName;

		private final TablePath path;

		private final TablePath root;

		private final GenericManager manager;

		private final QueryContext<T> builder;

		/**
		 * 直接使用しないでください。
		 * @param builder
		 * @param parent
		 * @param fkName
		 * @param path
		 * @param root
		 */
		public GenericRelationship(
			QueryContext<T> builder,
			QueryRelationship parent,
			String fkName,
			TablePath path,
			TablePath root) {
			query = null;
			context = null;
			this.parent = parent;
			this.fkName = fkName;
			this.path = path;
			manager = new GenericManager(path);
			this.builder = builder;
			this.root = root;
		}

		private GenericRelationship(
			GenericQuery query,
			QueryContext<T> builder,
			QueryCriteriaContext context) {
			this.query = query;
			this.context = context;
			parent = null;
			fkName = null;
			path = null;
			manager = new GenericManager(query.tablePath);
			this.builder = builder;
			root = null;
		}

		private TablePath path() {
			if (path == null) return query.tablePath;
			return path;
		}

		private TablePath root() {
			if (root == null) return path();
			return root;
		}

		/**
		 * @param name カラム名
		 * @return 使用されるカラムのタイプにあった型
		 */
		public T c(String name) {
			return builder.buildQueryColumn(this, name);
		}

		/**
		 * @param fkName 外部キー名
		 * @return 参照先の {@link GenericRelationship}
		 */
		public GenericRelationship<T, Many<GenericRow, M>> r(String fkName) {
			return new GenericRelationship<T, Many<GenericRow, M>>(builder, this, fkName, path(), root());
		}

		/**
		 * この {@link QueryRelationship} が表すテーブルの Row を一とし、多をもつ検索結果を生成する {@link OneToManyExecutor} を返します。
		 * @return 自動生成された {@link OneToManyExecutor} のサブクラス
		 */
		public GenericExecutor<M> intercept() {
			if (query != null) throw new IllegalStateException(path().getSchemaName() + " から直接使用することはできません");
			if (!getRoot().rowMode()) throw new IllegalStateException("集計モードでは実行できない処理です");
			return new GenericExecutor<>(this);
		}

		@Override
		public QueryCriteriaContext getContext() {
			if (context == null) return parent.getContext();

			return context;
		}

		@Override
		public Relationship getRelationship() {
			if (parent != null) {
				return parent.getRelationship().find(fkName);
			}

			return ContextManager.get(RelationshipFactory.class).getInstance(query.manager.getTablePath());
		}

		@Override
		public Optimizer getOptimizer() {
			if (query != null) return query.getOptimizer();
			return null;
		}

		@Override
		public GroupByClause getGroupByClause() {
			if (query == null) return parent.getGroupByClause();

			GroupByClause clause = query.groupByClause;
			if (clause == null) {
				clause = new GroupByClause();
				query.groupByClause = clause;
			}

			return clause;
		}

		@Override
		public OrderByClause getOrderByClause() {
			if (query == null) return parent.getOrderByClause();

			OrderByClause clause = query.orderByClause;
			if (clause == null) {
				clause = new OrderByClause();
				query.orderByClause = clause;
			}

			return clause;
		}

		@Override
		public void setWhereClause(Criteria criteria) {
			if (query == null) {
				parent.setWhereClause(criteria);
				return;
			}

			query.whereClause = criteria;
		}

		@Override
		public Criteria getWhereClause() {
			if (query == null) return parent.getWhereClause();

			return query.whereClause;
		}

		@Override
		public void setHavingClause(Criteria criteria) {
			if (query == null) {
				parent.setHavingClause(criteria);
				return;
			}

			query.havingClause = criteria;
		}

		@Override
		public Criteria getHavingClause() {
			if (query == null) return parent.getHavingClause();
			return query.havingClause;
		}

		@Override
		public QueryRelationship getParent() {
			return parent;
		}

		@Override
		public TablePath getTablePath() {
			return path();
		}

		@Override
		public Query getRoot() {
			if (query != null) return query;
			return parent.getRoot();
		}

		@Override
		public GenericRow createRow(DataObject data) {
			return manager.createRow(data);
		}

		@Override
		public void accept(SelectOffers offers) {
			offers.add(getRelationship().getColumns());
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof QueryRelationship)) return false;
			return getRelationship()
				.equals(((QueryRelationship) o).getRelationship());
		}

		@Override
		public int hashCode() {
			return getRelationship().hashCode();
		}
	}

	/**
	 * SELECT 句用
	 */
	public static class MySelectQueryColumn
		extends SelectQueryColumn<GenericRelationship<MySelectQueryColumn, Void>> {

		private MySelectQueryColumn(QueryRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * GROUP BY 句用
	 */
	public static class MyGroupByQueryColumn
		extends GroupByQueryColumn<GenericRelationship<MyGroupByQueryColumn, Void>> {

		private MyGroupByQueryColumn(QueryRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * ORDER BY 句用
	 */
	public static class MyOrderByQueryColumn
		extends OrderByQueryColumn<GenericRelationship<MyOrderByQueryColumn, Void>> {

		private MyOrderByQueryColumn(QueryRelationship relationship, String name) {
			super(relationship, name);
		}
	}
}
