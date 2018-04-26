/*--*//*@formatter:off*//*--*/package /*++{0}.query++*//*--*/org.blendee.develop.ormgen/*--*/;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import /*++{0}.manager.{1}Manager.{1}Iterator++*//*--*/org.blendee.develop.ormgen.ManagerBase.IteratorBase/*--*/;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.TablePath;
import org.blendee.jdbc.BlenResultSet;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.ResultSetIterator;
import org.blendee.orm.DataObject;
import org.blendee.selector.AnchorOptimizerFactory;
import org.blendee.selector.Optimizer;
import org.blendee.sql.Bindable;
import org.blendee.sql.Criteria;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.GroupByClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.QueryBuilder;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.FromClause.JoinType;
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
import org.blendee.support.QueryOnClause;
import org.blendee.support.SQLDecorators;
import org.blendee.support.QueryRelationship;
import org.blendee.support.SelectQueryRelationship;
import org.blendee.support.WhereQueryRelationship;
import org.blendee.support.GroupByQueryRelationship;
import org.blendee.support.HavingQueryRelationship;
import org.blendee.support.OrderByQueryRelationship;
import org.blendee.support.OnLeftQueryRelationship;
import org.blendee.support.OnRightQueryRelationship;
import org.blendee.support.SelectOfferFunction;
import org.blendee.support.Subquery;
import org.blendee.support.WhereQueryColumn;
import org.blendee.support.HavingQueryColumn;
import org.blendee.support.OnLeftQueryColumn;
import org.blendee.support.OnRightQueryColumn;

/**
 * 自動生成された '{'@link Query'}' の実装クラスです。
 * パッケージ名 {0}
 * テーブル名 {1}
 */
public class /*++{1}Query++*//*--*/QueryBase/*--*/
	extends /*++{2}++*//*--*/Object/*--*/
	implements Query /*++'++*/{/*++'++*/

	private static final QueryContext<SelectQCol> selectContext = (relationship, name) -> new SelectQCol(relationship, name);

	private static final QueryContext<GroupByQCol> groupByContext = (relationship, name) -> new GroupByQCol(relationship, name);

	private static final QueryContext<OrderByQCol> orderByContext = (relationship, name) -> new OrderByQCol(relationship, name);

	private static final QueryContext<WhereQueryColumn<WhereLogicalOperators>> whereContext =  QueryContext.newWhereBuilder();

	private static final QueryContext<HavingQueryColumn<HavingLogicalOperators>> havingContext =  QueryContext.newHavingBuilder();

	private static final QueryContext<OnLeftQueryColumn<OnLeftLogicalOperators>> onLeftContext =  QueryContext.newOnLeftBuilder();

	private static final QueryContext<OnRightQueryColumn<OnRightLogicalOperators>> onRightContext =  QueryContext.newOnRightBuilder();

	private final /*++{0}.manager.{1}Manager++*//*--*/ManagerBase/*--*/ manager = new /*++{0}.manager.{1}Manager()++*//*--*/ManagerBase()/*--*/;

	/**
	 * WHERE 句 で使用する AND, OR です。
	 */
	public class WhereLogicalOperators implements LogicalOperators<WhereQRel> /*++'++*/{/*++'++*/

		private WhereLogicalOperators() /*++'++*/{}/*++'++*/

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
		 */
		public final WhereQRel AND = new WhereQRel(
				/*++{1}Query++*//*--*/QueryBase/*--*/.this,
				whereContext,
				QueryCriteriaContext.AND);

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
		 */
		public final WhereQRel OR = new WhereQRel(
				/*++{1}Query++*//*--*/QueryBase/*--*/.this,
				whereContext,
				QueryCriteriaContext.OR);

		@Override
		public WhereQRel defaultOperator() /*++'++*/{/*++'++*/
			return AND;
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	/**
	 * HAVING 句 で使用する AND, OR です。
	 */
	public class HavingLogicalOperators implements LogicalOperators<HavingQRel> /*++'++*/{/*++'++*/

		private HavingLogicalOperators() /*++'++*/{}/*++'++*/

		/**
		 * HAVING 句に AND 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
		 */
		public final HavingQRel AND =
			new HavingQRel(
				/*++{1}Query++*//*--*/QueryBase/*--*/.this,
				havingContext,
				QueryCriteriaContext.AND);

		/**
		 * HAVING 句に OR 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
		 */
		public final HavingQRel OR = new HavingQRel(
				/*++{1}Query++*//*--*/QueryBase/*--*/.this,
				havingContext,
				QueryCriteriaContext.OR);

		@Override
		public HavingQRel defaultOperator() /*++'++*/{/*++'++*/
			return AND;
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	/**
	 * ON 句 (LEFT) で使用する AND, OR です。
	 */
	public class OnLeftLogicalOperators implements LogicalOperators<OnLeftQRel> /*++'++*/{/*++'++*/

		private OnLeftLogicalOperators() /*++'++*/{}/*++'++*/

		/**
		 * ON 句に AND 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
		 */
		public final OnLeftQRel AND =
			new OnLeftQRel(
				/*++{1}Query++*//*--*/QueryBase/*--*/.this,
				onLeftContext,
				QueryCriteriaContext.AND);

		/**
		 * ON 句に OR 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
		 */
		public final OnLeftQRel OR =
			new OnLeftQRel(
				/*++{1}Query++*//*--*/QueryBase/*--*/.this,
				onLeftContext,
				QueryCriteriaContext.OR);

		@Override
		public OnLeftQRel defaultOperator() /*++'++*/{/*++'++*/
			return AND;
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/


	/**
	 * ON 句 (RIGHT) で使用する AND, OR です。
	 */
	public class OnRightLogicalOperators implements LogicalOperators<OnRightQRel> /*++'++*/{/*++'++*/

		private OnRightLogicalOperators() /*++'++*/{}/*++'++*/

		/**
		 * ON 句に AND 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
		 */
		public final OnRightQRel AND =
			new OnRightQRel(
				/*++{1}Query++*//*--*/QueryBase/*--*/.this,
				onRightContext,
				QueryCriteriaContext.AND);

		/**
		 * ON 句に OR 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
		 */
		public final OnRightQRel OR =
			new OnRightQRel(
				/*++{1}Query++*//*--*/QueryBase/*--*/.this,
				onRightContext,
				QueryCriteriaContext.OR);

		@Override
		public OnRightQRel defaultOperator() /*++'++*/{/*++'++*/
			return AND;
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	private final WhereLogicalOperators whereOperators = new WhereLogicalOperators();

	private final HavingLogicalOperators havingOperators = new HavingLogicalOperators();

	private final OnLeftLogicalOperators onLeftOperators = new OnLeftLogicalOperators();

	private final OnRightLogicalOperators onRightOperators = new OnRightLogicalOperators();

	/**
	 * この '{'@link Query'}' のテーブルを表す '{'@link QueryRelationship'}' を参照するためのインスタンスです。
	 */
	public final ExtQRel<QueryColumn, Void> rel = new ExtQRel<>(
			this,
			QueryContext.OTHER,
			QueryCriteriaContext.NULL);

	/**
	 * 他の '{'@link Query'}' に JOIN するための接続ポイントです。
	 */
	public final OnRightQRel joint =  onRightOperators.AND;

	/**
	 * SELECT 句用のカラムを選択するための '{'@link QueryRelationship'}' です。
	 */
	private final SelectQRel select = new SelectQRel(
			this,
			selectContext,
			QueryCriteriaContext.NULL);

	/**
	 * GROUP BY 句用のカラムを選択するための '{'@link QueryRelationship'}' です。
	 */
	private final GroupByQRel groupBy = new GroupByQRel(
			this,
			groupByContext,
			QueryCriteriaContext.NULL);

	/**
	 * ORDER BY 句用のカラムを選択するための '{'@link QueryRelationship'}' です。
	 */
	private final OrderByQRel orderBy = new OrderByQRel(
			this,
			orderByContext,
			QueryCriteriaContext.NULL);

	private final QueryHelper<SelectQRel, GroupByQRel, WhereQRel, HavingQRel, OrderByQRel, OnLeftQRel> helper = new QueryHelper<>(
		/*++{0}.row.{1}++*//*--*/RowBase/*--*/.$TABLE,
		select,
		groupBy,
		orderBy,
		whereOperators,
		havingOperators,
		onLeftOperators);

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
		SelectOfferFunction<SelectQRel> function) /*++'++*/{/*++'++*/
		helper.SELECT(function);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * DISTINCT を使用した SELECT 句を記述します。
	 * @param function
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ SELECT_DISTINCT(
		SelectOfferFunction<SelectQRel> function) /*++'++*/{/*++'++*/
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
		GroupByOfferFunction<GroupByQRel> function) /*++'++*/{/*++'++*/
		helper.GROUP_BY(function);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * WHERE 句を記述します。
	 * @param consumers
	 * @return この '{'@link Query'}'
	 */
	@SafeVarargs
	public final /*++{1}Query++*//*--*/QueryBase/*--*/ WHERE(
		Consumer<WhereQRel>... consumers) /*++'++*/{/*++'++*/
		helper.WHERE(consumers);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * WHERE 句で使用できる '{'@link  Criteria'}' を作成します。
	 * @param consumer '{'@link Consumer'}'
	 * @return '{'@link Criteria'}'
	 */
	public Criteria createWhereCriteria(
		Consumer<WhereQRel> consumer) /*++'++*/{/*++'++*/
		return helper.createWhereCriteria(consumer);
	/*++'++*/}/*++'++*/

	/**
	 * HAVING 句を記述します。
	 * @param consumers
	 * @return この '{'@link Query'}'
	 */
	@SafeVarargs
	public final /*++{1}Query++*//*--*/QueryBase/*--*/ HAVING(
		Consumer<HavingQRel>... consumers) /*++'++*/{/*++'++*/
		helper.HAVING(consumers);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * HAVING 句で使用できる '{'@link  Criteria'}' を作成します。
	 * @param consumer '{'@link Consumer'}'
	 * @return '{'@link Criteria'}'
	 */
	public Criteria createHavingCriteria(
		Consumer<HavingQRel> consumer) /*++'++*/{/*++'++*/
		return helper.createHavingCriteria(consumer);
	/*++'++*/}/*++'++*/

	/**
	 * このクエリに INNER JOIN で別テーブルを結合します。
	 * @param rightJoint 別クエリの '{'@link OnRightQueryRelationship'}'
	 * @return ON
	 */
	public <R extends OnRightQueryRelationship> QueryOnClause<OnLeftQRel, R, /*++{1}Query++*//*--*/QueryBase/*--*/> INNER_JOIN(R rightJoint) /*++'++*/{/*++'++*/
		return helper.INNER_JOIN(rightJoint, this);
	/*++'++*/}/*++'++*/

	/**
	 * このクエリに LEFT OUTER JOIN で別テーブルを結合します。
	 * @param rightJoint 別クエリの '{'@link OnRightQueryRelationship'}'
	 * @return ON
	 */
	public <R extends OnRightQueryRelationship> QueryOnClause<OnLeftQRel, R, /*++{1}Query++*//*--*/QueryBase/*--*/> LEFT_OUTER_JOIN(R rightJoint) /*++'++*/{/*++'++*/
		return helper.LEFT_OUTER_JOIN(rightJoint, this);
	/*++'++*/}/*++'++*/

	/**
	 * このクエリに RIGHT OUTER JOIN で別テーブルを結合します。
	 * @param rightJoint 別クエリの '{'@link OnRightQueryRelationship'}'
	 * @return ON
	 */
	public <R extends OnRightQueryRelationship> QueryOnClause<OnLeftQRel, R, /*++{1}Query++*//*--*/QueryBase/*--*/> RIGHT_OUTER_JOIN(R rightJoint) /*++'++*/{/*++'++*/
		return helper.RIGHT_OUTER_JOIN(rightJoint, this);
	/*++'++*/}/*++'++*/

	/**
	 * このクエリに FULL OUTER JOIN で別テーブルを結合します。
	 * @param rightJoint 別クエリの '{'@link OnRightQueryRelationship'}'
	 * @return ON
	 */
	public <R extends OnRightQueryRelationship> QueryOnClause<OnLeftQRel, R, /*++{1}Query++*//*--*/QueryBase/*--*/> FULL_OUTER_JOIN(R rightJoint) /*++'++*/{/*++'++*/
		return helper.FULL_OUTER_JOIN(rightJoint, this);
	/*++'++*/}/*++'++*/

	/**
	 * UNION するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param query UNION 対象
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ UNION(ComposedSQL query) /*++'++*/{/*++'++*/
		helper.UNION(query);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * UNION ALL するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param query UNION ALL 対象
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ UNION_ALL(ComposedSQL query) /*++'++*/{/*++'++*/
		helper.UNION_ALL(query);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * ORDER BY 句を記述します。
	 * @param function
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ ORDER_BY(
		OrderByOfferFunction<OrderByQRel> function) /*++'++*/{/*++'++*/
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
		helper.and(criteria);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現時点の WHERE 句に新たな条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria OR 結合する新条件
	 * @return '{'@link Query'}' 自身
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ or(Criteria criteria) /*++'++*/{/*++'++*/
		helper.or(criteria);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 生成された SQL 文を加工する '{'SQLDecorator'}' を設定します。
	 * @param decorators '{'@link SQLDecorator'}'
	 * @return '{'@link Query'}' 自身
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ apply(SQLDecorator... decorators) /*++'++*/{/*++'++*/
		helper.apply(decorators);
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
	public LogicalOperators<?> getOnLeftLogicalOperators() /*++'++*/{/*++'++*/
		return onLeftOperators;
	/*++'++*/}/*++'++*/

	@Override
	public LogicalOperators<?> getOnRightLogicalOperators() /*++'++*/{/*++'++*/
		return onRightOperators;
	/*++'++*/}/*++'++*/

	@Override
	public SQLDecorator[] decorators() /*++'++*/{/*++'++*/
		return helper.decorators();
	/*++'++*/}/*++'++*/

	@Override
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ execute() /*++'++*/{/*++'++*/
		helper.checkRowMode();
		return manager.select(
			helper.getOptimizer(),
			helper.getWhereClause(),
			helper.getOrderByClause(),
			helper.decorators());
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> willUnique() /*++'++*/{/*++'++*/
		return getUnique(execute());
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> fetch(String... primaryKeyMembers) /*++'++*/{/*++'++*/
		helper.checkRowMode();
		return manager.select(helper.getOptimizer(), SQLDecorators.of(helper.decorators()), primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> fetch(Number... primaryKeyMembers) /*++'++*/{/*++'++*/
		helper.checkRowMode();
		return manager.select(helper.getOptimizer(), SQLDecorators.of(helper.decorators()), primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{0}.row.{1}++*//*--*/RowBase/*--*/> fetch(Bindable... primaryKeyMembers) /*++'++*/{/*++'++*/
		helper.checkRowMode();
		return manager.select(helper.getOptimizer(), SQLDecorators.of(helper.decorators()), primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public void aggregate(Consumer<BlenResultSet> consumer) /*++'++*/{/*++'++*/
		helper.aggregate(consumer);
	/*++'++*/}/*++'++*/

	@Override
	public <T> T aggregateAndGet(Function<BlenResultSet, T> function) /*++'++*/{/*++'++*/
		return helper.aggregateAndGet(function);
	/*++'++*/}/*++'++*/

	@Override
	public ResultSetIterator aggregate() /*++'++*/{/*++'++*/
		return helper.aggregate();
	/*++'++*/}/*++'++*/

	@Override
	public int count() /*++'++*/{/*++'++*/
		helper.checkRowMode();
		return manager.count(helper.getWhereClause());
	/*++'++*/}/*++'++*/

	@Override
	public ComposedSQL composeSQL() /*++'++*/{/*++'++*/
		return helper.composeSQL();
	/*++'++*/}/*++'++*/

	@Override
	public void joinTo(QueryBuilder builder, JoinType joinType, Criteria onCriteria) /*++'++*/{/*++'++*/
		helper.joinTo(builder, joinType, onCriteria);
	/*++'++*/}/*++'++*/

	@Override
	public Subquery toSubquery() /*++'++*/{/*++'++*/
		return helper.toSubquery();
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

		private O2MExecutor(QueryRelationship self, SQLDecorator[] decorators) /*++'++*/{/*++'++*/
			super(self, decorators);
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
	 * 条件として使用できるカラムを内包しており、それらを使用して検索 SQL を生成可能にします。
	 * @param <T> 使用されるカラムのタイプにあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class QRel<T, M> implements QueryRelationship /*++'++*/{/*++'++*/

		private final /*++{1}Query++*//*--*/QueryBase/*--*/ query$;

		private final QueryCriteriaContext context$;

		private final QueryRelationship parent$;

		private final String fkName$;

		private final TablePath path$;

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
		 * @param builder$ builder
		 * @param parent$ parent
		 * @param fkName$ fkName
		 * @param path$ path
		 */
		public QRel(
			QueryContext<T> builder$,
			QueryRelationship parent$,
			String fkName$,
			TablePath path$) /*++'++*/{/*++'++*/
			query$ = null;
			context$ = null;
			this.parent$ = parent$;
			this.fkName$ = fkName$;
			this.path$ = path$;

/*++{4}++*/
/*==ColumnPart2==*/this./*++{0}++*//*--*/columnName/*--*/ = builder$.buildQueryColumn(
				this, /*++{2}.row.{1}++*//*--*/RowBase/*--*/./*++{0}++*//*--*/columnName/*--*/);
/*==ColumnPart2==*/
		/*++'++*/}/*++'++*/

		private QRel(
			/*++{1}Query++*//*--*/QueryBase/*--*/ query$,
			QueryContext<T> builder$,
			QueryCriteriaContext context$) /*++'++*/{/*++'++*/
			this.query$ = query$;
			this.context$ = context$;
			parent$ = null;
			fkName$ = null;
			path$ = /*++{0}.row.{1}++*//*--*/RowBase/*--*/.$TABLE;

			/*--*/columnName = null;/*--*/
/*++{4}++*/
		/*++'++*/}/*++'++*/

		/**
		 * この '{'@link QueryRelationship'}' が表すテーブルの Row を一とし、多をもつ検索結果を生成する '{'@link OneToManyExecutor'}' を返します。
		 * @return 自動生成された '{'@link OneToManyExecutor'}' のサブクラス
		 */
		public O2MExecutor<M> intercept() /*++'++*/{/*++'++*/
			if (query$ != null) throw new IllegalStateException(path$.getSchemaName() + " から直接使用することはできません");
			if (!getRoot().rowMode()) throw new IllegalStateException("集計モードでは実行できない処理です");
			return new O2MExecutor<>(this, getRoot().decorators());
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
		public Criteria getWhereClause() /*++'++*/{/*++'++*/
			if (query$ == null) return parent$.getWhereClause();
			return query$.helper.getWhereClause();
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
	 * 自動生成された '{'@link QueryRelationship'}' の実装クラスです。<br>
	 * 条件として使用できるカラムと、参照しているテーブルを内包しており、それらを使用して検索 SQL を生成可能にします。
	 * @param <T> 使用されるカラムのタイプにあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class ExtQRel<T, M> extends QRel<T, M> /*++'++*/{/*++'++*/

		/*--?--*/private final QueryContext<T> builder$;/*--?--*/

		/*--?--*/private final TablePath root$;/*--?--*/

		/**
		 * 直接使用しないでください。
		 * @param builder$ builder
		 * @param parent$ parent
		 * @param fkName$ fkName
		 * @param path$ path
		 * @param root$ root
		 */
		public ExtQRel(
			QueryContext<T> builder$,
			QueryRelationship parent$,
			String fkName$,
			TablePath path$,
			TablePath root$) /*++'++*/{/*++'++*/
			super(builder$, parent$, fkName$, path$);
			/*--?--*/this.builder$ = builder$;/*--?--*/
			/*--?--*/this.root$ = root$;/*--?--*/
		/*++'++*/}/*++'++*/

		private ExtQRel(
			/*++{1}Query++*//*--*/QueryBase/*--*/ query$,
			QueryContext<T> builder$,
			QueryCriteriaContext context$) /*++'++*/{/*++'++*/
			super(query$, builder$, context$);
			/*--?--*/this.builder$ = builder$;/*--?--*/
			/*--?--*/root$ = null;/*--?--*/
		/*++'++*/}/*++'++*/

/*++{5}++*/
/*==RelationshipPart==*/
		/**
		 * 参照先テーブル名 {0}
		 * 外部キー名 {1}
		 * @return {0} relationship
		 */
		public /*++{5}.query.{0}Query.++*/ExtQRel<T, /*++{4}++*//*--*/Object/*--*/> /*--*/relationshipName/*--*//*++{2}++*/() /*++'++*/{/*++'++*/
			if (root$ != null) /*++'++*/{/*++'++*/
				return new /*++{5}.query.{0}Query.++*/ExtQRel<>(
					builder$, this, /*++{5}.row.{3}++*//*--*/RowBase/*--*/./*++{0}++*/$/*++{1}++*/, /*++{5}.row.{0}++*//*--*/RowBase/*--*/.$TABLE, root$);
			/*++'++*/}/*++'++*/

			return new /*++{5}.query.{0}Query.++*/ExtQRel<>(
				builder$, this, /*++{5}.row.{3}++*//*--*/RowBase/*--*/./*++{0}++*/$/*++{1}++*/, /*++{5}.row.{0}++*//*--*/RowBase/*--*/.$TABLE, super.path$);
		/*++'++*/}/*++'++*/
/*==RelationshipPart==*/
	/*++'++*/}/*++'++*/

	/**
	 * SELECT 句用
	 */
	public static class SelectQRel extends ExtQRel<SelectQCol, Void> implements SelectQueryRelationship /*++'++*/{/*++'++*/

		private SelectQRel(
			/*++{1}Query++*//*--*/QueryBase/*--*/ query$,
			QueryContext<SelectQCol> builder$,
			QueryCriteriaContext context$) /*++'++*/{/*++'++*/
			super(query$, builder$, context$);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	/**
	 * WHERE 句用
	 */
	public static class WhereQRel extends ExtQRel<WhereQueryColumn<WhereLogicalOperators>, Void> implements WhereQueryRelationship /*++'++*/{/*++'++*/

		private WhereQRel(
			/*++{1}Query++*//*--*/QueryBase/*--*/ query$,
			QueryContext<WhereQueryColumn<WhereLogicalOperators>> builder$,
			QueryCriteriaContext context$) /*++'++*/{/*++'++*/
			super(query$, builder$, context$);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	/**
	 * GROUB BY 句用
	 */
	public static class GroupByQRel extends ExtQRel<GroupByQCol, Void> implements GroupByQueryRelationship /*++'++*/{/*++'++*/

		private GroupByQRel(
			/*++{1}Query++*//*--*/QueryBase/*--*/ query$,
			QueryContext<GroupByQCol> builder$,
			QueryCriteriaContext context$) /*++'++*/{/*++'++*/
			super(query$, builder$, context$);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	/**
	 * HAVING 句用
	 */
	public static class HavingQRel extends ExtQRel<HavingQueryColumn<HavingLogicalOperators>, Void> implements HavingQueryRelationship /*++'++*/{/*++'++*/

		private HavingQRel(
			/*++{1}Query++*//*--*/QueryBase/*--*/ query$,
			QueryContext<HavingQueryColumn<HavingLogicalOperators>> builder$,
			QueryCriteriaContext context$) /*++'++*/{/*++'++*/
			super(query$, builder$, context$);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByQRel extends ExtQRel<OrderByQCol, Void> implements OrderByQueryRelationship /*++'++*/{/*++'++*/

		private OrderByQRel(
			/*++{1}Query++*//*--*/QueryBase/*--*/ query$,
			QueryContext<OrderByQCol> builder$,
			QueryCriteriaContext context$) /*++'++*/{/*++'++*/
			super(query$, builder$, context$);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	/**
	 * ON 句 (LEFT) 用
	 */
	public static class OnLeftQRel extends ExtQRel<OnLeftQueryColumn<OnLeftLogicalOperators>, Void> implements OnLeftQueryRelationship /*++'++*/{/*++'++*/

		private OnLeftQRel(
			/*++{1}Query++*//*--*/QueryBase/*--*/ query$,
			QueryContext<OnLeftQueryColumn<OnLeftLogicalOperators>> builder$,
			QueryCriteriaContext context$) /*++'++*/{/*++'++*/
			super(query$, builder$, context$);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	/**
	 * ON 句 (RIGHT) 用
	 */
	public static class OnRightQRel extends QRel<OnRightQueryColumn<OnRightLogicalOperators>, Void> implements OnRightQueryRelationship /*++'++*/{/*++'++*/

		private OnRightQRel(
			/*++{1}Query++*//*--*/QueryBase/*--*/ query$,
			QueryContext<OnRightQueryColumn<OnRightLogicalOperators>> builder$,
			QueryCriteriaContext context$) /*++'++*/{/*++'++*/
			super(query$, builder$, context$);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	/**
	 * SELECT 句用
	 */
	public static class SelectQCol
		extends SelectQueryColumn<SelectQRel> /*++'++*/{/*++'++*/

		private SelectQCol(QueryRelationship relationship, String name) /*++'++*/{/*++'++*/
			super(relationship, name);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	/**
	 * GROUP BY 句用
	 */
	public static class GroupByQCol
		extends GroupByQueryColumn<QRel<
			GroupByQCol,
			Void>> /*++'++*/{/*++'++*/

		private GroupByQCol(QueryRelationship relationship, String name) /*++'++*/{/*++'++*/
			super(relationship, name);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByQCol
		extends OrderByQueryColumn<QRel<
			OrderByQCol,
			Void>> /*++'++*/{/*++'++*/

		private OrderByQCol(QueryRelationship relationship, String name) /*++'++*/{/*++'++*/
			super(relationship, name);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/
/*++'++*/}/*++'++*/
