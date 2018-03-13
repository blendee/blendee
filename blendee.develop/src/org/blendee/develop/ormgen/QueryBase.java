/*--*//*@formatter:off*//*--*/package /*++{0}.query++*//*--*/org.blendee.develop.ormgen/*--*/;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import /*++{0}.manager.{1}Manager.{1}Iterator++*//*--*/org.blendee.develop.ormgen.ManagerBase.IteratorBase/*--*/;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.TablePath;
import org.blendee.jdbc.Result;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.ResultSetIterator;
import org.blendee.orm.DataObject;
import org.blendee.selector.AnchorOptimizerFactory;
import org.blendee.selector.Optimizer;
import org.blendee.sql.Bindable;
import org.blendee.sql.Criteria;
import org.blendee.sql.Effector;
import org.blendee.sql.GroupByClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.support.OrderByQueryColumn;
import org.blendee.support.GroupByQueryColumn;
import org.blendee.support.SelectQueryColumn;
/*++{6}++*/
import org.blendee.support.LogicalOperators;
import org.blendee.support.NotUniqueException;
import org.blendee.support.OneToManyExecutor;
import org.blendee.support.GroupByOfferFunction;
import org.blendee.support.OrderByOfferFunction;
import org.blendee.support.Query;
import org.blendee.support.QueryColumn;
import org.blendee.support.QueryContext;
import org.blendee.support.QueryCriteriaContext;
import org.blendee.support.QueryHelper;
import org.blendee.support.Effectors;
import org.blendee.support.QueryRelationship;
import org.blendee.support.SelectOfferFunction;
import org.blendee.support.Subquery;
import org.blendee.support.WhereQueryColumn;
import org.blendee.support.HavingQueryColumn;

/**
 * 自動生成された '{'@link Query'}' の実装クラスです。
 * パッケージ名 {0}
 * テーブル名 {1}
 */
public class /*++{1}Query++*//*--*/QueryBase/*--*/
	extends /*++{2}++*//*--*/Object/*--*/
	implements Query /*++'++*/{/*++'++*/

	private static final QueryContext<MySelectQueryColumn> selectContext = (relationship, name) -> new MySelectQueryColumn(relationship, name);

	private static final QueryContext<MyGroupByQueryColumn> groupByContext = (relationship, name) -> new MyGroupByQueryColumn(relationship, name);

	private static final QueryContext<MyOrderByQueryColumn> orderByContext = (relationship, name) -> new MyOrderByQueryColumn(relationship, name);

	private static final QueryContext<WhereQueryColumn<WhereLogicalOperators>> whereContext =  QueryContext.newWhereBuilder();

	private static final QueryContext<HavingQueryColumn<HavingLogicalOperators>> havingContext =  QueryContext.newHavingBuilder();

	private final /*++{0}.manager.{1}Manager++*//*--*/ManagerBase/*--*/ manager = new /*++{0}.manager.{1}Manager()++*//*--*/ManagerBase()/*--*/;

	/**
	 * WHERE 句 で使用する AND, OR です。
	 */
	public class WhereLogicalOperators implements LogicalOperators<MyQueryRelationship<WhereQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/.WhereLogicalOperators>, Void>> /*++'++*/{/*++'++*/

		private WhereLogicalOperators() /*++'++*/{}/*++'++*/

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
		 */
		public final MyQueryRelationship<WhereQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/.WhereLogicalOperators>, Void> AND =
			new MyQueryRelationship<>(
				/*++{1}Query++*//*--*/QueryBase/*--*/.this,
				whereContext,
				QueryCriteriaContext.WHERE_AND);

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
		 */
		public final MyQueryRelationship<WhereQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/.WhereLogicalOperators>, Void> OR =
			new MyQueryRelationship<>(
				/*++{1}Query++*//*--*/QueryBase/*--*/.this,
				whereContext,
				QueryCriteriaContext.WHERE_OR);

		@Override
		public MyQueryRelationship<WhereQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/.WhereLogicalOperators>, Void> AND() /*++'++*/{/*++'++*/
			return AND;
		/*++'++*/}/*++'++*/

		@Override
		public MyQueryRelationship<WhereQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/.WhereLogicalOperators>, Void> OR() /*++'++*/{/*++'++*/
			return OR;
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	/**
	 * HAVING 句 で使用する AND, OR です。
	 */
	public class HavingLogicalOperators implements LogicalOperators<MyQueryRelationship<HavingQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/.HavingLogicalOperators>, Void>> /*++'++*/{/*++'++*/

		private HavingLogicalOperators() /*++'++*/{}/*++'++*/

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
		 */
		public final MyQueryRelationship<HavingQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/.HavingLogicalOperators>, Void> AND =
			new MyQueryRelationship<>(
				/*++{1}Query++*//*--*/QueryBase/*--*/.this,
				havingContext,
				QueryCriteriaContext.HAVING_AND);

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
		 */
		public final MyQueryRelationship<HavingQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/.HavingLogicalOperators>, Void> OR =
			new MyQueryRelationship<>(
				/*++{1}Query++*//*--*/QueryBase/*--*/.this,
				havingContext,
				QueryCriteriaContext.HAVING_OR);

		@Override
		public MyQueryRelationship<HavingQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/.HavingLogicalOperators>, Void> AND() /*++'++*/{/*++'++*/
			return AND;
		/*++'++*/}/*++'++*/

		@Override
		public MyQueryRelationship<HavingQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/.HavingLogicalOperators>, Void> OR() /*++'++*/{/*++'++*/
			return OR;
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	private final WhereLogicalOperators whereOperators = new WhereLogicalOperators();

	private final HavingLogicalOperators havingOperators = new HavingLogicalOperators();

	/**
	 * この '{'@link Query'}' のテーブルを表す '{'@link QueryRelationship'}' を参照するためのインスタンスです。
	 */
	public final MyQueryRelationship<QueryColumn, Void> rel =
		new MyQueryRelationship<>(
			this,
			QueryContext.OTHER,
			QueryCriteriaContext.NULL);

	/**
	 * SELECT 句用のカラムを選択するための '{'@link QueryRelationship'}' です。
	 */
	private final MyQueryRelationship<MySelectQueryColumn, Void> select =
		new MyQueryRelationship<>(
			this,
			selectContext,
			QueryCriteriaContext.NULL);

	/**
	 * GROUP BY 句用のカラムを選択するための '{'@link QueryRelationship'}' です。
	 */
	private final MyQueryRelationship<MyGroupByQueryColumn, Void> groupBy =
		new MyQueryRelationship<>(
			this,
			groupByContext,
			QueryCriteriaContext.NULL);

	/**
	 * ORDER BY 句用のカラムを選択するための '{'@link QueryRelationship'}' です。
	 */
	private final MyQueryRelationship<MyOrderByQueryColumn, Void> orderBy =
		new MyQueryRelationship<>(
			this,
			orderByContext,
			QueryCriteriaContext.NULL);

	private final QueryHelper<
		MyQueryRelationship<MySelectQueryColumn, Void>,
		MyQueryRelationship<MyGroupByQueryColumn, Void>,
		MyQueryRelationship<WhereQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/.WhereLogicalOperators>, Void>,
		MyQueryRelationship<HavingQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/.HavingLogicalOperators>, Void>,
		MyQueryRelationship<MyOrderByQueryColumn, Void>> helper = new QueryHelper<>(
		/*++{0}.row.{1}++*//*--*/RowBase/*--*/.$TABLE,
		select,
		groupBy,
		orderBy,
		whereOperators,
		havingOperators);

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
		helper.setOptimizer(Objects.requireNonNull(optimizer));
	/*++'++*/}/*++'++*/

	private /*++{1}Query++*//*--*/QueryBase/*--*/(Class<?> using, String id) /*++'++*/{/*++'++*/
		helper.setOptimizer(
			ContextManager.get(AnchorOptimizerFactory.class).getInstance(
				id,
				/*++{0}.row.{1}++*//*--*/RowBase/*--*/.$TABLE,
				using));
	/*++'++*/}/*++'++*/

	/**
	 * SELECT 句を記述します。
	 * @param function
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ SELECT(
		SelectOfferFunction<MyQueryRelationship<MySelectQueryColumn, Void>> function) /*++'++*/{/*++'++*/
		helper.SELECT(function);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * DISTINCT を使用した SELECT 句を記述します。
	 * @param function
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ SELECT_DISTINCT(
		SelectOfferFunction<MyQueryRelationship<MySelectQueryColumn, Void>> function) /*++'++*/{/*++'++*/
		helper.SELECT_DISTINCT(function);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * COUNT(*) を使用した SELECT 句を記述します。
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ SELECT_COUNT() /*++'++*/{/*++'++*/
		helper.SELECT_COUNT();
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * GROUP BY 句を記述します。
	 * @param function
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ GROUP_BY(
		GroupByOfferFunction<MyQueryRelationship<MyGroupByQueryColumn, Void>> function) /*++'++*/{/*++'++*/
		helper.GROUP_BY(function);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * WHERE 句を記述します。
	 * @param consumer
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ WHERE(
		Consumer<MyQueryRelationship<WhereQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/.WhereLogicalOperators>, Void>> consumer) /*++'++*/{/*++'++*/
		helper.WHERE(consumer);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * HAVING 句を記述します。
	 * @param consumer
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ HAVING(
		Consumer<MyQueryRelationship<HavingQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/.HavingLogicalOperators>, Void>> consumer) /*++'++*/{/*++'++*/
		helper.HAVING(consumer);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * ORDER BY 句を記述します。
	 * @param function
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ ORDER_BY(
		OrderByOfferFunction<MyQueryRelationship<MyOrderByQueryColumn, Void>> function) /*++'++*/{/*++'++*/
		helper.ORDER_BY(function);
		return this;
	/*++'++*/}/*++'++*/

	@Override
	public boolean hasWhereClause() /*++'++*/{/*++'++*/
		return helper.hasWhereClause();
	/*++'++*/}/*++'++*/

	/**
	 * 新規に GROUP BY 句をセットします。
	 * @param clause 新 ORDER BY 句
	 * @return '{'@link Query'}' 自身
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ groupBy(GroupByClause clause) /*++'++*/{/*++'++*/
		helper.setGroupByClause(clause);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 新規に ORDER BY 句をセットします。
	 * @param clause 新 ORDER BY 句
	 * @return '{'@link Query'}' 自身
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ orderBy(OrderByClause clause) /*++'++*/{/*++'++*/
		helper.setOrderByClause(clause);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現時点の WHERE 句に新たな条件を AND 結合します。<br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria AND 結合する新条件
	 * @return '{'@link Query'}' 自身
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ and(Criteria criteria) /*++'++*/{/*++'++*/
		QueryCriteriaContext.WHERE_AND.addCriteria(whereOperators.AND, criteria);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現時点の WHERE 句に新たな条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria OR 結合する新条件
	 * @return '{'@link Query'}' 自身
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ or(Criteria criteria) /*++'++*/{/*++'++*/
		QueryCriteriaContext.WHERE_OR.addCriteria(whereOperators.OR, criteria);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現時点の WHERE 句に新たなサブクエリ条件を AND 結合します。<br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 * @param subquery AND 結合するサブクエリ条件
	 * @return '{'@link Query'}' 自身
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ and(Subquery subquery) /*++'++*/{/*++'++*/
		QueryCriteriaContext.WHERE_AND.addCriteria(whereOperators.AND, subquery.createCriteria(this));
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現時点の WHERE 句に新たなサブクエリ条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param subquery OR 結合するサブクエリ条件
	 * @return '{'@link Query'}' 自身
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ or(Subquery subquery) /*++'++*/{/*++'++*/
		QueryCriteriaContext.WHERE_OR.addCriteria(whereOperators.OR, subquery.createCriteria(this));
		return this;
	/*++'++*/}/*++'++*/

	@Override
	public Relationship getRootRealtionship() /*++'++*/{/*++'++*/
		return ContextManager.get(RelationshipFactory.class).getInstance(manager.getTablePath());
	/*++'++*/}/*++'++*/

	@Override
	public LogicalOperators<?> getWhereLogicalOperators() /*++'++*/{/*++'++*/
		return whereOperators;
	/*++'++*/}/*++'++*/

	@Override
	public LogicalOperators<?> getHavingLogicalOperators() /*++'++*/{/*++'++*/
		return havingOperators;
	/*++'++*/}/*++'++*/

	@Override
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ execute() /*++'++*/{/*++'++*/
		helper.checkRowMode();
		return manager.select(helper.getOptimizer(), helper.getWhereClause(), helper.getOrderByClause());
	/*++'++*/}/*++'++*/

	@Override
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ execute(Effector... options) /*++'++*/{/*++'++*/
		helper.checkRowMode();
		return manager.select(helper.getOptimizer(), helper.getWhereClause(), helper.getOrderByClause(), options);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> willUnique() /*++'++*/{/*++'++*/
		return getUnique(execute());
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> willUnique(Effector... options) /*++'++*/{/*++'++*/
		return getUnique(execute(options));
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> fetch(String... primaryKeyMembers) /*++'++*/{/*++'++*/
		helper.checkRowMode();
		return manager.select(helper.getOptimizer(), primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> fetch(Number... primaryKeyMembers) /*++'++*/{/*++'++*/
		helper.checkRowMode();
		return manager.select(helper.getOptimizer(), primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> fetch(Bindable... primaryKeyMembers) /*++'++*/{/*++'++*/
		helper.checkRowMode();
		return manager.select(helper.getOptimizer(), primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> fetch(Effectors options, String... primaryKeyMembers) /*++'++*/{/*++'++*/
		helper.checkRowMode();
		return manager.select(helper.getOptimizer(), options, primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> fetch(Effectors options, Number... primaryKeyMembers) /*++'++*/{/*++'++*/
		helper.checkRowMode();
		return manager.select(helper.getOptimizer(), options, primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> fetch(Effectors options, Bindable... primaryKeyMembers) /*++'++*/{/*++'++*/
		helper.checkRowMode();
		return manager.select(helper.getOptimizer(), options, primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public void aggregate(Consumer<Result> consumer) /*++'++*/{/*++'++*/
		helper.aggregate(consumer);
	/*++'++*/}/*++'++*/

	@Override
	public void aggregate(Effectors options, Consumer<Result> consumer) /*++'++*/{/*++'++*/
		helper.aggregate(options, consumer);
	/*++'++*/}/*++'++*/

	@Override
	public ResultSetIterator aggregate(Effector... options) /*++'++*/{/*++'++*/
		return helper.aggregate(options);
	/*++'++*/}/*++'++*/

	@Override
	public int count() /*++'++*/{/*++'++*/
		helper.checkRowMode();
		return manager.count(helper.getWhereClause());
	/*++'++*/}/*++'++*/

	@Override
	public Criteria getCriteria() /*++'++*/{/*++'++*/
		return helper.getWhereClause().replicate();
	/*++'++*/}/*++'++*/

	@Override
	public ComposedSQL composeSQL(Effector... options) /*++'++*/{/*++'++*/
		return helper.composeSQL(options);
	/*++'++*/}/*++'++*/

	/**
	 * 現在保持している WHERE 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ resetWhere() /*++'++*/{/*++'++*/
		helper.resetWhere();
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現在保持している HAVING 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ resetHaving() /*++'++*/{/*++'++*/
		helper.resetHaving();
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現在保持している SELECT 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ resetSelect() /*++'++*/{/*++'++*/
		helper.resetSelect();
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現在保持している GROUP BY 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ resetGroupBy() /*++'++*/{/*++'++*/
		helper.resetGroupBy();
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現在保持している ORDER BY 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ resetOrderBy() /*++'++*/{/*++'++*/
		helper.resetOrderBy();
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現在保持している条件、並び順をリセットします。
	 * @return このインスタンス
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ reset() /*++'++*/{/*++'++*/
		helper.reset();
		return this;
	/*++'++*/}/*++'++*/

	@Override
	public void quitRowMode() /*++'++*/{/*++'++*/
		helper.quitRowMode();
	/*++'++*/}/*++'++*/

	@Override
	public boolean rowMode() /*++'++*/{/*++'++*/
		return helper.rowMode();
	/*++'++*/}/*++'++*/

	/**
	 * 自動生成された '{'@link OneToManyExecutor'}' の実装クラスです。
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class O2MExecutor<M>
		extends OneToManyExecutor</*++{0}.row.{1}++*//*--*/RowBase/*--*/, M> /*++'++*/{/*++'++*/

		private O2MExecutor(QueryRelationship self) /*++'++*/{/*++'++*/
			super(self);
		/*++'++*/}/*++'++*/
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
	public static class MyQueryRelationship<T, M> implements QueryRelationship /*++'++*/{/*++'++*/

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
		public MyQueryRelationship(
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

		private MyQueryRelationship(
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
		public /*++{5}.query.{0}Query.++*/MyQueryRelationship<T, /*++{4}++*//*--*/Object/*--*/> /*--*/relationshipName/*--*//*++{2}++*/() /*++'++*/{/*++'++*/
			if (root$ != null) /*++'++*/{/*++'++*/
				return new /*++{5}.query.{0}Query.++*/MyQueryRelationship<>(
					builder$, this, /*++{5}.row.{3}++*//*--*/RowBase/*--*/./*++{0}++*/$/*++{1}++*/, /*++{5}.row.{0}++*//*--*/RowBase/*--*/.$TABLE, root$);
			/*++'++*/}/*++'++*/

			return new /*++{5}.query.{0}Query.++*/MyQueryRelationship<>(
				builder$, this, /*++{5}.row.{3}++*//*--*/RowBase/*--*/./*++{0}++*/$/*++{1}++*/, /*++{5}.row.{0}++*//*--*/RowBase/*--*/.$TABLE, path$);
		/*++'++*/}/*++'++*/
/*==RelationshipPart==*/

		/**
		 * この '{'@link QueryRelationship'}' が表すテーブルの Row を一とし、多をもつ検索結果を生成する '{'@link OneToManyExecutor'}' を返します。
		 * @return 自動生成された '{'@link OneToManyExecutor'}' のサブクラス
		 */
		public O2MExecutor<M> intercept() /*++'++*/{/*++'++*/
			if (query$ != null) throw new IllegalStateException(path$.getSchemaName() + " から直接使用することはできません");
			if (!getRoot().rowMode()) throw new IllegalStateException("集計モードでは実行できない処理です");
			return new O2MExecutor<>(this);
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
			if (query$ != null) return query$.helper.getOptimizer();
			return null;
		/*++'++*/}/*++'++*/

		@Override
		public GroupByClause getGroupByClause() /*++'++*/{/*++'++*/
			if (query$ == null) return parent$.getGroupByClause();
			return query$.helper.getGroupByClause();
		/*++'++*/}/*++'++*/

		@Override
		public OrderByClause getOrderByClause() /*++'++*/{/*++'++*/
			if (query$ == null) return parent$.getOrderByClause();
			return query$.helper.getOrderByClause();
		/*++'++*/}/*++'++*/

		@Override
		public void setWhereClause(Criteria criteria) /*++'++*/{/*++'++*/
			if (query$ == null) /*++'++*/{/*++'++*/
				parent$.setWhereClause(criteria);
				return;
			/*++'++*/}/*++'++*/

			query$.helper.setWhereClause(criteria);
		/*++'++*/}/*++'++*/

		@Override
		public Criteria getWhereClause() /*++'++*/{/*++'++*/
			if (query$ == null) return parent$.getWhereClause();
			return query$.helper.getWhereClause();
		/*++'++*/}/*++'++*/

		@Override
		public void setHavingClause(Criteria criteria) /*++'++*/{/*++'++*/
			if (query$ == null) /*++'++*/{/*++'++*/
				parent$.setHavingClause(criteria);
				return;
			/*++'++*/}/*++'++*/

			query$.helper.setHavingClause(criteria);
		/*++'++*/}/*++'++*/

		@Override
		public Criteria getHavingClause() /*++'++*/{/*++'++*/
			if (query$ == null) return parent$.getHavingClause();
			return query$.helper.getHavingClause();
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
	public static class MySelectQueryColumn
		extends SelectQueryColumn<MyQueryRelationship<
			MySelectQueryColumn,
			Void>> /*++'++*/{/*++'++*/

		private MySelectQueryColumn(QueryRelationship relationship, String name) /*++'++*/{/*++'++*/
			super(relationship, name);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	/**
	 * GROUP BY 句用
	 */
	public static class MyGroupByQueryColumn
		extends GroupByQueryColumn<MyQueryRelationship<
			MyGroupByQueryColumn,
			Void>> /*++'++*/{/*++'++*/

		private MyGroupByQueryColumn(QueryRelationship relationship, String name) /*++'++*/{/*++'++*/
			super(relationship, name);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	/**
	 * ORDER BY 句用
	 */
	public static class MyOrderByQueryColumn
		extends OrderByQueryColumn<MyQueryRelationship<
			MyOrderByQueryColumn,
			Void>> /*++'++*/{/*++'++*/

		private MyOrderByQueryColumn(QueryRelationship relationship, String name) /*++'++*/{/*++'++*/
			super(relationship, name);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/
/*++'++*/}/*++'++*/
