package org.blendee.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.blendee.internal.U;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataObject;
import org.blendee.orm.QueryOption;
import org.blendee.selector.AnchorOptimizerFactory;
import org.blendee.selector.Optimizer;
import org.blendee.selector.RuntimeOptimizer;
import org.blendee.selector.SimpleOptimizer;
import org.blendee.sql.Bindable;
import org.blendee.sql.Criteria;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.support.AbstractOrderQueryColumn;
import org.blendee.support.AbstractSelectQueryColumn;
import org.blendee.support.LogicalOperators;
import org.blendee.support.Many;
import org.blendee.support.NotUniqueException;
import org.blendee.support.OneToManyExecutor;
import org.blendee.support.OrderByOfferFunction;
import org.blendee.support.Query;
import org.blendee.support.QueryColumn;
import org.blendee.support.QueryCriteriaContext;
import org.blendee.support.QueryContext;
import org.blendee.support.QueryOptions;
import org.blendee.support.QueryRelationship;
import org.blendee.support.Row;
import org.blendee.support.SelectOffer;
import org.blendee.support.SelectOfferFunction;
import org.blendee.support.SelectOfferFunction.SelectOffers;
import org.blendee.support.Subquery;
import org.blendee.support.WhereQueryColumn;
import org.blendee.util.GenericManager.GenericRowIterator;

/**
 * {@link Row} の汎用実装クラスです。
 */
public class GenericQuery extends java.lang.Object implements Query {

	private static final QueryContext<SelectQueryColumn> selectContext = (relationship, name) -> new SelectQueryColumn(relationship, name);

	private static final QueryContext<OrderByQueryColumn> orderByContext = (relationship, name) -> new OrderByQueryColumn(relationship, name);

	private static final QueryContext<WhereQueryColumn<GenericLogicalOperators>> whereContext = QueryContext.newBuilder();

	private final TablePath tablePath;

	private final GenericManager manager;

	/**
	 * WHERE 句 で使用する AND, OR です。
	 */
	public class GenericLogicalOperators implements LogicalOperators {

		private GenericLogicalOperators() {}

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final GenericRelationship<WhereQueryColumn<GenericQuery.GenericLogicalOperators>, Void> AND = new GenericRelationship<>(
			GenericQuery.this,
			whereContext,
			QueryCriteriaContext.AND);

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final GenericRelationship<WhereQueryColumn<GenericQuery.GenericLogicalOperators>, Void> OR = new GenericRelationship<>(
			GenericQuery.this,
			whereContext,
			QueryCriteriaContext.OR);
	}

	private final GenericLogicalOperators operators = new GenericLogicalOperators();

	/**
	 * この {@link Query} のテーブルを表す {@link QueryRelationship} を参照するためのインスタンスです。
	 */
	public final GenericRelationship<QueryColumn, Void> rel = new GenericRelationship<>(
		this,
		QueryContext.OTHER,
		QueryCriteriaContext.NULL);

	private Optimizer optimizer;

	private Criteria criteria;

	private OrderByClause orderByClause;

	/**
	 * ORDER BY 句用のカラムを選択するための {@link QueryRelationship} です。
	 */
	private final GenericRelationship<SelectQueryColumn, Void> select = new GenericRelationship<>(
		this,
		selectContext,
		QueryCriteriaContext.NULL);

	/**
	 * ORDER BY 句用のカラムを選択するための {@link QueryRelationship} です。
	 */
	private final GenericRelationship<OrderByQueryColumn, Void> orderBy = new GenericRelationship<>(
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
		SelectOfferFunction<GenericRelationship<SelectQueryColumn, Void>> function) {
		RuntimeOptimizer myOptimizer = new RuntimeOptimizer(tablePath);
		function.offer(select).get().forEach(c -> myOptimizer.add(c));
		optimizer = myOptimizer;
		return this;
	}

	/**
	 * WHERE 句を記述します。
	 * @param consumer
	 * @return この {@link Query}
	 */
	public GenericQuery WHERE(
		Consumer<GenericRelationship<WhereQueryColumn<GenericQuery.GenericLogicalOperators>, Void>> consumer) {
		consumer.accept(operators.AND);
		return this;
	}

	/**
	 * ORDER BY 句を記述します。
	 * @param function
	 * @return この {@link Query}
	 */
	public GenericQuery ORDER_BY(
		OrderByOfferFunction<GenericRelationship<OrderByQueryColumn, Void>> function) {
		function.offer(orderBy);
		return this;
	}

	@Override
	public boolean hasCriteria() {
		return criteria != null && criteria.isAvailable();
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
		QueryCriteriaContext.AND.addCriteria(operators.AND, criteria);
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たな条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria OR 結合する新条件
	 * @return {@link Query} 自身
	 */
	public GenericQuery or(Criteria criteria) {
		QueryCriteriaContext.OR.addCriteria(operators.OR, criteria);
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たなサブクエリ条件を AND 結合します。<br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 * @param subquery AND 結合するサブクエリ条件
	 * @return {@link Query} 自身
	 */
	public GenericQuery and(Subquery subquery) {
		QueryCriteriaContext.AND.addCriteria(operators.AND, subquery.createCriteria(this));
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たなサブクエリ条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param subquery OR 結合するサブクエリ条件
	 * @return {@link Query} 自身
	 */
	public GenericQuery or(Subquery subquery) {
		QueryCriteriaContext.OR.addCriteria(operators.OR, subquery.createCriteria(this));
		return this;
	}

	@Override
	public Relationship getRootRealtionship() {
		return ContextManager.get(RelationshipFactory.class).getInstance(manager.getTablePath());
	}

	@Override
	public LogicalOperators getLogicalOperators() {
		return operators;
	}

	@Override
	public GenericRowIterator execute() {
		return manager.select(getOptimizer(), criteria, orderByClause);
	}

	@Override
	public GenericRowIterator execute(QueryOption... options) {
		return manager.select(getOptimizer(), criteria, orderByClause, options);
	}

	@Override
	public Optional<GenericRow> willUnique() {
		return getUnique(execute());
	}

	@Override
	public Optional<GenericRow> willUnique(QueryOption... options) {
		return getUnique(execute(options));
	}

	@Override
	public Optional<GenericRow> fetch(String... primaryKeyMembers) {
		return manager.select(getOptimizer(), primaryKeyMembers);
	}

	@Override
	public Optional<GenericRow> fetch(Number... primaryKeyMembers) {
		return manager.select(getOptimizer(), primaryKeyMembers);
	}

	@Override
	public Optional<GenericRow> fetch(Bindable... primaryKeyMembers) {
		return manager.select(getOptimizer(), primaryKeyMembers);
	}

	@Override
	public Optional<GenericRow> fetch(QueryOptions options, String... primaryKeyMembers) {
		return manager.select(getOptimizer(), options, primaryKeyMembers);
	}

	@Override
	public Optional<GenericRow> fetch(QueryOptions options, Number... primaryKeyMembers) {
		return manager.select(getOptimizer(), options, primaryKeyMembers);
	}

	@Override
	public Optional<GenericRow> fetch(QueryOptions options, Bindable... primaryKeyMembers) {
		return manager.select(getOptimizer(), options, primaryKeyMembers);
	}

	@Override
	public int count() {
		return manager.count(criteria);
	}

	@Override
	public Criteria getCriteria() {
		return criteria.replicate();
	}

	/**
	 * 現在保持している条件をリセットします。
	 * @return このインスタンス
	 */
	public GenericQuery resetCriteria() {
		criteria = null;
		return this;
	}

	/**
	 * 現在保持している並び順をリセットします。
	 * @return このインスタンス
	 */
	public GenericQuery resetOrder() {
		orderByClause = null;
		return this;
	}

	/**
	 * 現在保持している条件、並び順をリセットします。
	 * @return このインスタンス
	 */
	public GenericQuery reset() {
		criteria = null;
		orderByClause = null;
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

			query.criteria = criteria;
		}

		@Override
		public Criteria getWhereClause() {
			if (query == null) return parent.getWhereClause();

			return query.criteria;
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
	public static class SelectQueryColumn
		extends AbstractSelectQueryColumn<GenericRelationship<SelectQueryColumn, Void>> {

		private SelectQueryColumn(QueryRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByQueryColumn
		extends AbstractOrderQueryColumn<GenericRelationship<OrderByQueryColumn, Void>> {

		private OrderByQueryColumn(QueryRelationship relationship, String name) {
			super(relationship, name);
		}
	}
}
