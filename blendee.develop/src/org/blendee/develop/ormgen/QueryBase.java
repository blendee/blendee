/*--*//*@formatter:off*//*--*/package /*++{0}.query++*//*--*/org.blendee.develop.ormgen/*--*/;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import /*++{0}.manager.{1}Manager.{1}Iterator++*//*--*/org.blendee.develop.ormgen.ManagerBase.IteratorBase/*--*/;
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
/*++{6}++*/
import org.blendee.support.LogicalOperators;
import org.blendee.support.NotUniqueException;
import org.blendee.support.OneToManyExecutor;
import org.blendee.support.OrderByOfferFunction;
import org.blendee.support.Query;
import org.blendee.support.QueryColumn;
import org.blendee.support.QueryContext;
import org.blendee.support.QueryCriteriaContext;
import org.blendee.support.QueryOptions;
import org.blendee.support.QueryRelationship;
import org.blendee.support.SelectOffer;
import org.blendee.support.SelectOfferFunction;
import org.blendee.support.SelectOfferFunction.SelectOffers;
import org.blendee.support.Subquery;
import org.blendee.support.WhereQueryColumn;

/**
 * 自動生成された '{'@link Query'}' の実装クラスです。
 * パッケージ名 {0}
 * テーブル名 {1}
 */
public class /*++{1}Query++*//*--*/QueryBase/*--*/
	extends /*++{2}++*//*--*/Object/*--*/
	implements Query /*++'++*/{/*++'++*/

	private static final QueryContext<SelectQueryColumn> selectContext = (relationship, name) -> new SelectQueryColumn(relationship, name);

	private static final QueryContext<OrderByQueryColumn> orderByContext = (relationship, name) -> new OrderByQueryColumn(relationship, name);

	private static final QueryContext<WhereQueryColumn<WhereLogicalOperators>> whereContext =  QueryContext.newBuilder();

	private final /*++{0}.manager.{1}Manager++*//*--*/ManagerBase/*--*/ manager = new /*++{0}.manager.{1}Manager()++*//*--*/ManagerBase()/*--*/;

	/**
	 * WHERE 句 で使用する AND, OR です。
	 */
	public class WhereLogicalOperators implements LogicalOperators /*++'++*/{/*++'++*/

		private WhereLogicalOperators() /*++'++*/{}/*++'++*/

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
		 */
		public final QRelationship<WhereQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/.WhereLogicalOperators>, Void> AND =
			new QRelationship<>(
				/*++{1}Query++*//*--*/QueryBase/*--*/.this,
				whereContext,
				QueryCriteriaContext.AND);

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
		 */
		public final QRelationship<WhereQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/.WhereLogicalOperators>, Void> OR =
			new QRelationship<>(
				/*++{1}Query++*//*--*/QueryBase/*--*/.this,
				whereContext,
				QueryCriteriaContext.OR);
	/*++'++*/}/*++'++*/

	private final WhereLogicalOperators operators = new WhereLogicalOperators();

	/**
	 * この '{'@link Query'}' のテーブルを表す '{'@link QueryRelationship'}' を参照するためのインスタンスです。
	 */
	public final QRelationship<QueryColumn, Void> rel =
		new QRelationship<>(
			this,
			QueryContext.OTHER,
			QueryCriteriaContext.NULL);

	private Optimizer optimizer;

	private Criteria criteria;

	private OrderByClause orderByClause;

	private SelectOfferFunction<?> selectClauseFunction;

	private OrderByOfferFunction<?> orderByClauseFunction;

	/**
	 * SELECT 句用のカラムを選択するための '{'@link QueryRelationship'}' です。
	 */
	private final QRelationship<SelectQueryColumn, Void> select =
		new QRelationship<>(
			this,
			selectContext,
			QueryCriteriaContext.NULL);

	/**
	 * ORDER BY 句用のカラムを選択するための '{'@link QueryRelationship'}' です。
	 */
	private final QRelationship<OrderByQueryColumn, Void> orderBy =
		new QRelationship<>(
			this,
			orderByContext,
			QueryCriteriaContext.NULL);

	/**
	 * このクラスのインスタンスを生成します。<br>
	 * インスタンスは ID として、引数で渡された id を使用します。<br>
	 * フィールド定義の必要がなく、簡易に使用できますが、 ID は呼び出し側クラス内で一意である必要があります。
	 * @param id '{'@link Query'}' を使用するクラス内で一意の ID
	 * @return このクラスのインスタンス
	 */
	public static /*++{1}Query++*//*--*/QueryBase/*--*/ of(String id) /*++'++*/{/*++'++*/
		if (id == null || id.equals(""))
			throw new IllegalArgumentException("id が空です");

		return new /*++{1}Query++*//*--*/QueryBase/*--*/(getUsing(new Throwable().getStackTrace()[1]), id);
	/*++'++*/}/*++'++*/

	/**
	 * 空のインスタンスを生成します。
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/() /*++'++*/{}/*++'++*/

	/**
	 * このクラスのインスタンスを生成します。<br>
	 * このコンストラクタで生成されたインスタンス の SELECT 句で使用されるカラムは、 パラメータの '{'@link Optimizer'}' に依存します。
	 * @param optimizer SELECT 句を決定する
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/(Optimizer optimizer) /*++'++*/{/*++'++*/
		this.optimizer = Objects.requireNonNull(optimizer);
	/*++'++*/}/*++'++*/

	private /*++{1}Query++*//*--*/QueryBase/*--*/(Class<?> using, String id) /*++'++*/{/*++'++*/
		optimizer = ContextManager.get(AnchorOptimizerFactory.class).getInstance(
			id,
			/*++{0}.row.{1}++*//*--*/RowBase/*--*/.$TABLE,
			using);
	/*++'++*/}/*++'++*/

	/**
	 * SELECT 句を記述します。
	 * @param function
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ SELECT(
		SelectOfferFunction<QRelationship<SelectQueryColumn, Void>> function) /*++'++*/{/*++'++*/
		if (selectClauseFunction == function) return this;

		RuntimeOptimizer myOptimizer = new RuntimeOptimizer(/*++{0}.row.{1}++*//*--*/RowBase/*--*/.$TABLE);
		function.offer(select).get().forEach(c -> myOptimizer.add(c));
		optimizer = myOptimizer;
		selectClauseFunction = function;
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * WHERE 句を記述します。
	 * @param consumer
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ WHERE(
		Consumer<QRelationship<WhereQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/.WhereLogicalOperators>, Void>> consumer) /*++'++*/{/*++'++*/
		consumer.accept(operators.AND);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * ORDER BY 句を記述します。
	 * @param function
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ ORDER_BY(
		OrderByOfferFunction<QRelationship<OrderByQueryColumn, Void>> function) /*++'++*/{/*++'++*/
		if (orderByClauseFunction == function) return this;

		function.offer(orderBy);
		orderByClauseFunction = function;
		return this;
	/*++'++*/}/*++'++*/

	@Override
	public boolean hasCriteria() /*++'++*/{/*++'++*/
		return criteria != null && criteria.isAvailable();
	/*++'++*/}/*++'++*/

	/**
	 * 新規に ORDER BY 句をセットします。
	 * @param clause 新 ORDER BY 句
	 * @return '{'@link Query'}' 自身
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ orderBy(OrderByClause clause) /*++'++*/{/*++'++*/
		if (orderByClause != null)
			throw new IllegalStateException("既に ORDER BY 句がセットされています");
		orderByClause = clause;
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現時点の WHERE 句に新たな条件を AND 結合します。<br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria AND 結合する新条件
	 * @return '{'@link Query'}' 自身
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ and(Criteria criteria) /*++'++*/{/*++'++*/
		QueryCriteriaContext.AND.addCriteria(operators.AND, criteria);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現時点の WHERE 句に新たな条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria OR 結合する新条件
	 * @return '{'@link Query'}' 自身
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ or(Criteria criteria) /*++'++*/{/*++'++*/
		QueryCriteriaContext.OR.addCriteria(operators.OR, criteria);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現時点の WHERE 句に新たなサブクエリ条件を AND 結合します。<br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 * @param subquery AND 結合するサブクエリ条件
	 * @return '{'@link Query'}' 自身
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ and(Subquery subquery) /*++'++*/{/*++'++*/
		QueryCriteriaContext.AND.addCriteria(operators.AND, subquery.createCriteria(this));
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現時点の WHERE 句に新たなサブクエリ条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param subquery OR 結合するサブクエリ条件
	 * @return '{'@link Query'}' 自身
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ or(Subquery subquery) /*++'++*/{/*++'++*/
		QueryCriteriaContext.OR.addCriteria(operators.OR, subquery.createCriteria(this));
		return this;
	/*++'++*/}/*++'++*/

	@Override
	public Relationship getRootRealtionship() /*++'++*/{/*++'++*/
		return ContextManager.get(RelationshipFactory.class).getInstance(manager.getTablePath());
	/*++'++*/}/*++'++*/

	@Override
	public LogicalOperators getLogicalOperators() /*++'++*/{/*++'++*/
		return operators;
	/*++'++*/}/*++'++*/

	@Override
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ execute() /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), criteria, orderByClause);
	/*++'++*/}/*++'++*/

	@Override
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ execute(QueryOption... options) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), criteria, orderByClause, options);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> willUnique() /*++'++*/{/*++'++*/
		return getUnique(execute());
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> willUnique(QueryOption... options) /*++'++*/{/*++'++*/
		return getUnique(execute(options));
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> fetch(String... primaryKeyMembers) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> fetch(Number... primaryKeyMembers) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> fetch(Bindable... primaryKeyMembers) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> fetch(QueryOptions options, String... primaryKeyMembers) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), options, primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> fetch(QueryOptions options, Number... primaryKeyMembers) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), options, primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> fetch(QueryOptions options, Bindable... primaryKeyMembers) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), options, primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public int count() /*++'++*/{/*++'++*/
		return manager.count(criteria);
	/*++'++*/}/*++'++*/

	@Override
	public Criteria getCriteria() /*++'++*/{/*++'++*/
		return criteria.replicate();
	/*++'++*/}/*++'++*/

	/**
	 * 現在保持している WHERE 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ resetCriteria() /*++'++*/{/*++'++*/
		criteria = null;
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現在保持している SELECT 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ resetSelect() /*++'++*/{/*++'++*/
		optimizer = null;
		selectClauseFunction = null;
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現在保持しているORDER BY 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ resetOrder() /*++'++*/{/*++'++*/
		orderByClause = null;
		orderByClauseFunction = null;
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現在保持している条件、並び順をリセットします。
	 * @return このインスタンス
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ reset() /*++'++*/{/*++'++*/
		criteria = null;
		optimizer = null;
		orderByClause = null;
		selectClauseFunction = null;
		orderByClauseFunction = null;
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 自動生成された '{'@link OneToManyExecutor'}' の実装クラスです。
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class O2MExecutor<M>
		extends OneToManyExecutor</*++{0}.row.{1}++*//*--*/RowBase/*--*/, M> /*++'++*/{/*++'++*/

		private O2MExecutor(QueryRelationship parent) /*++'++*/{/*++'++*/
			super(parent);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	private Optimizer getOptimizer() /*++'++*/{/*++'++*/
		if (optimizer != null) return optimizer;
		optimizer = new SimpleOptimizer(/*++{0}.row.{1}++*//*--*/RowBase/*--*/.$TABLE);
		return optimizer;
	/*++'++*/}/*++'++*/

	private static Class<?> getUsing(StackTraceElement element) /*++'++*/{/*++'++*/
		try /*++'++*/{/*++'++*/
			return Class.forName(element.getClassName());
		/*++'++*/}/*++'++*/ catch (Exception e) /*++'++*/{/*++'++*/
			throw new IllegalStateException(e.toString());
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	private static Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> getUnique(/*++{1}Iterator++*//*--*/IteratorBase/*--*/ iterator) /*++'++*/{/*++'++*/
		if (!iterator.hasNext()) return Optional.empty();

		/*++{0}.row.{1}++*//*--*/RowBase/*--*/ row = iterator.next();

		if (iterator.hasNext()) throw new NotUniqueException();

		return Optional.of(row);
	/*++'++*/}/*++'++*/

	/**
	 * 自動生成された '{'@link QueryRelationship'}' の実装クラスです。<br>
	 * 条件として使用できるカラムと、参照しているテーブルを内包しており、それらを使用して検索 SQL を生成可能にします。
	 * @param <T> 使用されるカラムのタイプにあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class QRelationship<T, M>
		implements QueryRelationship, SelectOffer /*++'++*/{/*++'++*/

		private final /*++{1}Query++*//*--*/QueryBase/*--*/ query$;

		private final QueryCriteriaContext context$;

		/*--?--*/private final QueryContext<T> builder$;/*--?--*/

		private final QueryRelationship parent$;

		private final String fkName$;

		private final TablePath path$;

		/*--?--*/private final TablePath root$;/*--?--*/

		private final /*++{0}.manager.{1}Manager++*//*--*/ManagerBase/*--*/ manager$ = new /*++{0}.manager.{1}Manager()++*//*--*/ManagerBase()/*--*/;

/*++{3}++*/
/*==ColumnPart1==*/
		/**
		 * 項目名 {0}
		 */
		public final T /*++{0}++*//*--*/columnName/*--*/;

/*==ColumnPart1==*/

		/**
		 * 直接使用しないでください。
		 * @param builder$
		 * @param parent$
		 * @param fkName$
		 * @param path$
		 * @param root$
		 */
		public QRelationship(
			QueryContext<T> builder$,
			QueryRelationship parent$,
			String fkName$,
			TablePath path$,
			TablePath root$) /*++'++*/{/*++'++*/
			query$ = null;
			/*--?--*/this.builder$ = builder$;/*--?--*/
			context$ = null;
			this.parent$ = parent$;
			this.fkName$ = fkName$;
			this.path$ = path$;
			/*--?--*/this.root$ = root$;/*--?--*/

/*++{4}++*/
/*==ColumnPart2==*/this./*++{0}++*//*--*/columnName/*--*/ = builder$.buildQueryColumn(
				this, /*++{2}.row.{1}++*//*--*/RowBase/*--*/./*++{0}++*//*--*/columnName/*--*/);
/*==ColumnPart2==*/
		/*++'++*/}/*++'++*/

		private QRelationship(
			/*++{1}Query++*//*--*/QueryBase/*--*/ query$,
			QueryContext<T> builder$,
			QueryCriteriaContext context$) /*++'++*/{/*++'++*/
			this.query$ = query$;
			/*--?--*/this.builder$ = builder$;/*--?--*/
			this.context$ = context$;
			parent$ = null;
			fkName$ = null;
			path$ = /*++{0}.row.{1}++*//*--*/RowBase/*--*/.$TABLE;
			/*--?--*/root$ = null;/*--?--*/

			/*--*/columnName = null;/*--*/
/*++{4}++*/
		/*++'++*/}/*++'++*/

/*++{5}++*/
/*==RelationshipPart==*/
		/**
		 * 参照先テーブル名 {0}
		 * 外部キー名 {1}
		 * @return {0} relationship
		 */
		public /*++{5}.query.{0}Query.++*/QRelationship<T, /*++{4}++*//*--*/Object/*--*/> /*--*/relationshipName/*--*//*++{2}++*/() /*++'++*/{/*++'++*/
			if (root$ != null) /*++'++*/{/*++'++*/
				return new /*++{5}.query.{0}Query.++*/QRelationship<>(
					builder$, this, /*++{5}.row.{3}++*//*--*/RowBase/*--*/./*++{0}++*/$/*++{1}++*/, /*++{5}.row.{0}++*//*--*/RowBase/*--*/.$TABLE, root$);
			/*++'++*/}/*++'++*/

			return new /*++{5}.query.{0}Query.++*/QRelationship<>(
				builder$, this, /*++{5}.row.{3}++*//*--*/RowBase/*--*/./*++{0}++*/$/*++{1}++*/, /*++{5}.row.{0}++*//*--*/RowBase/*--*/.$TABLE, path$);
		/*++'++*/}/*++'++*/
/*==RelationshipPart==*/

		/**
		 * この '{'@link QueryRelationship'}' が表すテーブルの Row を一とし、多をもつ検索結果を生成する '{'@link OneToManyExecutor'}' を返します。
		 * @return 自動生成された '{'@link OneToManyExecutor'}' のサブクラス
		 */
		public O2MExecutor<M> intercept() /*++'++*/{/*++'++*/
			if (query$ != null) throw new IllegalStateException(path$.getSchemaName() + " から直接使用することはできません");
			return new O2MExecutor/*--*/<>(this);
		/*++'++*/}/*++'++*/

		@Override
		public QueryCriteriaContext getContext() /*++'++*/{/*++'++*/
			if (context$ == null) return parent$.getContext();

			return context$;
		/*++'++*/}/*++'++*/

		@Override
		public Relationship getRelationship() /*++'++*/{/*++'++*/
			if (parent$ != null) /*++'++*/{/*++'++*/
				return parent$.getRelationship().find(fkName$);
			/*++'++*/}/*++'++*/

			return ContextManager.get(RelationshipFactory.class).getInstance(query$.manager.getTablePath());
		/*++'++*/}/*++'++*/

		@Override
		public Optimizer getOptimizer() /*++'++*/{/*++'++*/
			if (query$ != null) return query$.getOptimizer();
			return null;
		/*++'++*/}/*++'++*/

		@Override
		public OrderByClause getOrderByClause() /*++'++*/{/*++'++*/
			if (query$ == null) return parent$.getOrderByClause();

			OrderByClause clause = query$.orderByClause;
			if (clause == null) /*++'++*/{/*++'++*/
				clause = new OrderByClause();
				query$.orderByClause = clause;
			/*++'++*/}/*++'++*/

			return clause;
		/*++'++*/}/*++'++*/

		@Override
		public void setWhereClause(Criteria criteria) /*++'++*/{/*++'++*/
			if (query$ == null) /*++'++*/{/*++'++*/
				parent$.setWhereClause(criteria);
				return;
			/*++'++*/}/*++'++*/

			query$.criteria = criteria;
		/*++'++*/}/*++'++*/

		@Override
		public Criteria getWhereClause() /*++'++*/{/*++'++*/
			if (query$ == null) return parent$.getWhereClause();

			return query$.criteria;
		/*++'++*/}/*++'++*/

		@Override
		public QueryRelationship getParent() /*++'++*/{/*++'++*/
			return parent$;
		/*++'++*/}/*++'++*/

		@Override
		public TablePath getTablePath() /*++'++*/{/*++'++*/
			return path$;
		/*++'++*/}/*++'++*/

		@Override
		public Query getRoot() /*++'++*/{/*++'++*/
			if (query$ != null) return query$;
			return parent$.getRoot();
		/*++'++*/}/*++'++*/

		@Override
		public /*++{0}.row.{1}++*//*--*/RowBase/*--*/ createRow(DataObject data) /*++'++*/{/*++'++*/
			return manager$.createRow(data);
		/*++'++*/}/*++'++*/

		@Override
		public void accept(SelectOffers offers) /*++'++*/{/*++'++*/
			offers.add(getRelationship().getColumns());
		/*++'++*/}/*++'++*/

		@Override
		public boolean equals(Object o) /*++'++*/{/*++'++*/
			if (!(o instanceof QueryRelationship)) return false;
			return getRelationship()
				.equals(((QueryRelationship) o).getRelationship());
		/*++'++*/}/*++'++*/

		@Override
		public int hashCode() /*++'++*/{/*++'++*/
			return getRelationship().hashCode();
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	/**
	 * SELECT 句用
	 */
	public static class SelectQueryColumn
		extends AbstractSelectQueryColumn<QRelationship<
			SelectQueryColumn,
			Void>> /*++'++*/{/*++'++*/

		private SelectQueryColumn(QueryRelationship relationship, String name) /*++'++*/{/*++'++*/
			super(relationship, name);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByQueryColumn
		extends AbstractOrderQueryColumn<QRelationship<
			OrderByQueryColumn,
			Void>> /*++'++*/{/*++'++*/

		private OrderByQueryColumn(QueryRelationship relationship, String name) /*++'++*/{/*++'++*/
			super(relationship, name);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/
/*++'++*/}/*++'++*/
