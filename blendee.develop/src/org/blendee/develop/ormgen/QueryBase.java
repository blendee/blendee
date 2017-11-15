/*--*//*@formatter:off*//*--*/package /*++{0}++*//*--*/org.blendee.develop.ormgen/*--*/;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import /*++{0}.{1}Manager.{1}Iterator++*//*--*/org.blendee.develop.ormgen.ManagerBase.IteratorBase/*--*/;
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
import org.blendee.sql.Condition;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.support.AbstractOrderQueryColumn;
import org.blendee.support.AbstractSelectQueryColumn;
/*++{8}++*/
import org.blendee.support.LogicalOperators;
import org.blendee.support.NotUniqueException;
import org.blendee.support.OneToManyExecutor;
import org.blendee.support.OrderByOfferFunction;
import org.blendee.support.Query;
import org.blendee.support.QueryColumn;
import org.blendee.support.QueryConditionContext;
import org.blendee.support.QueryContext;
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

	private static final QueryContext<WhereQueryColumn</*++{1}LogicalOperators++*//*--*/ConcreteLogicalOperators/*--*/>> whereContext =  QueryContext.newBuilder();

	private final /*++{1}Manager++*//*--*/ManagerBase/*--*/ manager = new /*++{1}Manager()++*//*--*/ManagerBase()/*--*/;

	/**
	 * WHERE 句 で使用する AND, OR です。
	 */
	public class /*++{1}LogicalOperators++*//*--*/ConcreteLogicalOperators/*--*/ implements LogicalOperators /*++'++*/{/*++'++*/

		private /*++{1}LogicalOperators++*//*--*/ConcreteLogicalOperators/*--*/() /*++'++*/{}/*++'++*/

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
		 */
		public final /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<WhereQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/./*++{1}LogicalOperators++*//*--*/ConcreteLogicalOperators/*--*/>, Void> AND =
			new /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<>(
				/*++{1}Query++*//*--*/QueryBase/*--*/.this,
				whereContext,
				QueryConditionContext.AND);

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
		 */
		public final /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<WhereQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/./*++{1}LogicalOperators++*//*--*/ConcreteLogicalOperators/*--*/>, Void> OR =
			new /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<>(
				/*++{1}Query++*//*--*/QueryBase/*--*/.this,
				whereContext,
				QueryConditionContext.OR);
	/*++'++*/}/*++'++*/

	private final /*++{1}LogicalOperators++*//*--*/ConcreteLogicalOperators/*--*/ operators = new /*++{1}LogicalOperators++*//*--*/ConcreteLogicalOperators/*--*/();

	/**
	 * この '{'@link Query'}' のテーブルを表す '{'@link QueryRelationship'}' を参照するためのインスタンスです。
	 */
	public final /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<QueryColumn, Void> rel =
		new /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<>(
			this,
			QueryContext.OTHER,
			QueryConditionContext.NULL);

	private Optimizer optimizer;

	private Condition condition;

	private OrderByClause orderByClause;

	/**
	 * ORDER BY 句用のカラムを選択するための '{'@link QueryRelationship'}' です。
	 */
	private final /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<SelectQueryColumn, Void> select =
		new /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<>(
			this,
			selectContext,
			QueryConditionContext.NULL);

	/**
	 * ORDER BY 句用のカラムを選択するための '{'@link QueryRelationship'}' です。
	 */
	private final /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<OrderByQueryColumn, Void> orderBy =
		new /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<>(
			this,
			orderByContext,
			QueryConditionContext.NULL);

	/**
	 * このクラスのインスタンスを生成します。<br>
	 * インスタンスは ID として、引数で渡された id を使用します。<br>
	 * フィールド定義の必要がなく、簡易に使用できますが、 ID は呼び出し側クラス内で一意である必要があります。
	 * @param id '{'@link Query'}' を使用するクラス内で一意の ID
	 * @return このクラスのインスタンス
	 */
	public static /*++{1}Query++*//*--*/QueryBase/*--*/ of(String id) /*++'++*/{/*++'++*/
		if (!U.presents(id))
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
			/*++{1}++*//*--*/RowBase/*--*/.$TABLE,
			using);
	/*++'++*/}/*++'++*/

	/**
	 * SELECT 句を記述します。
	 * @param function
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ SELECT(
		SelectOfferFunction</*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<SelectQueryColumn, Void>> function) /*++'++*/{/*++'++*/
		RuntimeOptimizer myOptimizer = new RuntimeOptimizer(/*++{1}++*//*--*/RowBase/*--*/.$TABLE);
		function.offer(select).get().forEach(c -> myOptimizer.add(c));
		optimizer = myOptimizer;
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * WHERE 句を記述します。
	 * @param consumer
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ WHERE(
		Consumer</*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<WhereQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/./*++{1}LogicalOperators++*//*--*/ConcreteLogicalOperators/*--*/>, Void>> consumer) /*++'++*/{/*++'++*/
		consumer.accept(operators.AND);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * ORDER BY 句を記述します。
	 * @param function
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ ORDER_BY(
		OrderByOfferFunction</*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<OrderByQueryColumn, Void>> function) /*++'++*/{/*++'++*/
		function.offer(orderBy);
		return this;
	/*++'++*/}/*++'++*/

	@Override
	public boolean hasCondition() /*++'++*/{/*++'++*/
		return condition != null && condition.isAvailable();
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
	 * @param condition AND 結合する新条件
	 * @return '{'@link Query'}' 自身
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ and(Condition condition) /*++'++*/{/*++'++*/
		QueryConditionContext.AND.addCondition(operators.AND, condition);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現時点の WHERE 句に新たな条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param condition OR 結合する新条件
	 * @return '{'@link Query'}' 自身
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ or(Condition condition) /*++'++*/{/*++'++*/
		QueryConditionContext.OR.addCondition(operators.OR, condition);
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現時点の WHERE 句に新たなサブクエリ条件を AND 結合します。<br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 * @param subquery AND 結合するサブクエリ条件
	 * @return '{'@link Query'}' 自身
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ and(Subquery subquery) /*++'++*/{/*++'++*/
		QueryConditionContext.AND.addCondition(operators.AND, subquery.createCondition(this));
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現時点の WHERE 句に新たなサブクエリ条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param subquery OR 結合するサブクエリ条件
	 * @return '{'@link Query'}' 自身
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ or(Subquery subquery) /*++'++*/{/*++'++*/
		QueryConditionContext.OR.addCondition(operators.OR, subquery.createCondition(this));
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
		return manager.select(getOptimizer(), condition, orderByClause);
	/*++'++*/}/*++'++*/

	@Override
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ execute(QueryOption... options) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), condition, orderByClause, options);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{1}++*//*--*/RowBase/*--*/> willUnique() /*++'++*/{/*++'++*/
		return getUnique(execute());
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{1}++*//*--*/RowBase/*--*/> willUnique(QueryOption... options) /*++'++*/{/*++'++*/
		return getUnique(execute(options));
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{1}++*//*--*/RowBase/*--*/> fetch(String... primaryKeyMembers) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{1}++*//*--*/RowBase/*--*/> fetch(Number... primaryKeyMembers) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{1}++*//*--*/RowBase/*--*/> fetch(Bindable... primaryKeyMembers) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{1}++*//*--*/RowBase/*--*/> fetch(QueryOptions options, String... primaryKeyMembers) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), options, primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{1}++*//*--*/RowBase/*--*/> fetch(QueryOptions options, Number... primaryKeyMembers) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), options, primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{1}++*//*--*/RowBase/*--*/> fetch(QueryOptions options, Bindable... primaryKeyMembers) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), options, primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public int count() /*++'++*/{/*++'++*/
		return manager.count(condition);
	/*++'++*/}/*++'++*/

	@Override
	public Condition getCondition() /*++'++*/{/*++'++*/
		return condition.replicate();
	/*++'++*/}/*++'++*/

	/**
	 * 現在保持している条件をリセットします。
	 * @return このインスタンス
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ resetCondition() /*++'++*/{/*++'++*/
		condition = null;
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現在保持している並び順をリセットします。
	 * @return このインスタンス
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ resetOrder() /*++'++*/{/*++'++*/
		orderByClause = null;
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現在保持している条件、並び順をリセットします。
	 * @return このインスタンス
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ reset() /*++'++*/{/*++'++*/
		condition = null;
		orderByClause = null;
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 自動生成された '{'@link OneToManyExecutor'}' の実装クラスです。
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class /*++{1}Executor++*//*--*/O2MExecutor/*--*/<M>
		extends OneToManyExecutor</*++{1}++*//*--*/RowBase/*--*/, M> /*++'++*/{/*++'++*/

		private /*++{1}Executor++*//*--*/O2MExecutor/*--*/(QueryRelationship parent) /*++'++*/{/*++'++*/
			super(parent);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	private Optimizer getOptimizer() /*++'++*/{/*++'++*/
		if (optimizer != null) return optimizer;
		optimizer = new SimpleOptimizer(/*++{1}++*//*--*/RowBase/*--*/.$TABLE);
		return optimizer;
	/*++'++*/}/*++'++*/

	private static Class<?> getUsing(StackTraceElement element) /*++'++*/{/*++'++*/
		try /*++'++*/{/*++'++*/
			return Class.forName(element.getClassName());
		/*++'++*/}/*++'++*/ catch (Exception e) /*++'++*/{/*++'++*/
			throw new IllegalStateException(e.toString());
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	private static Optional</*++{1}++*//*--*/RowBase/*--*/> getUnique(/*++{1}Iterator++*//*--*/IteratorBase/*--*/ iterator) /*++'++*/{/*++'++*/
		if (!iterator.hasNext()) return Optional.empty();

		/*++{1}++*//*--*/RowBase/*--*/ row = iterator.next();

		if (iterator.hasNext()) throw new NotUniqueException();

		return Optional.of(row);
	/*++'++*/}/*++'++*/

	/**
	 * 自動生成された '{'@link QueryRelationship'}' の実装クラスです。<br>
	 * 条件として使用できるカラムと、参照しているテーブルを内包しており、それらを使用して検索 SQL を生成可能にします。
	 * @param <T> 使用されるカラムのタイプにあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<T, M>
		implements QueryRelationship, SelectOffer /*++'++*/{/*++'++*/

		private final /*++{1}Query++*//*--*/QueryBase/*--*/ $query;

		private final QueryConditionContext $context;

		private final QueryRelationship $parent;

		private final String $fkName;

		private final TablePath $path;

		private final /*++{1}Manager++*//*--*/ManagerBase/*--*/ $manager = new /*++{1}Manager()++*//*--*/ManagerBase()/*--*/;

/*++{3}++*/
/*==ColumnPart1==*/
		/**
		 * 項目名 {0}
		 */
		public final T /*++{0}++*//*--*/columnName/*--*/;

/*==ColumnPart1==*/
/*++{4}++*/
/*==RelationshipPart1==*/
		/**
		 * 参照先テーブル名 {0}
		 * 外部キー名 {1}
		 */
		public final /*++{0}Query.{0}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<T, /*++{3}++*//*--*/Object/*--*/> /*--*/relationshipName/*--*//*++{2}++*/;

/*==RelationshipPart1==*/

		/**
		 * 直接使用しないでください。
		 * @param builder
		 * @param parent
		 * @param fkName
		 * @param path
		 * @param root
		 */
		public /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/(
			QueryContext<T> builder,
			QueryRelationship parent,
			String fkName,
			TablePath path,
			TablePath root) /*++'++*/{/*++'++*/
			$query = null;
			$context = null;
			$parent = parent;
			$fkName = fkName;
			$path = path;

/*==ColumnPart2==*/this./*++{0}++*//*--*/columnName/*--*/ = builder.buildQueryColumn(
				this, /*++{1}++*//*--*/RowBase/*--*/./*++{0}++*//*--*/columnName/*--*/);
/*==ColumnPart2==*/
/*++{5}++*/

/*==RelationshipPart2==*/this./*--*/relationshipName/*--*//*++{2}++*/ = path.equals(root) ? null : new /*++{0}Query.{0}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<T, /*++{4}++*//*--*/Object/*--*/>(
				builder, this, /*++{3}++*//*--*/RowBase/*--*/./*++{0}++*/_BY_/*++{1}++*/, /*++{5}.{0}++*//*--*/RowBase/*--*/.$TABLE, root);
/*==RelationshipPart2==*/
/*++{6}++*/
		/*++'++*/}/*++'++*/

		private /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/(
			/*++{1}Query++*//*--*/QueryBase/*--*/ query,
			QueryContext<T> builder,
			QueryConditionContext context) /*++'++*/{/*++'++*/
			$query = query;
			$context = context;
			$parent = null;
			$fkName = null;
			$path = /*++{1}++*//*--*/RowBase/*--*/.$TABLE;

			/*--*/columnName = null;/*--*/
/*++{5}++*/

/*==RelationshipPart3==*/this./*--*/relationshipName/*--*//*++{2}++*/ = new /*++{0}Query.{0}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<T, /*++{4}++*//*--*/Object/*--*/>(
				builder, this, /*++{3}++*//*--*/RowBase/*--*/./*++{0}++*/_BY_/*++{1}++*/, /*++{5}.{0}++*//*--*/RowBase/*--*/.$TABLE, $path);
/*==RelationshipPart3==*/
/*++{7}++*/
		/*++'++*/}/*++'++*/

		/**
		 * この '{'@link QueryRelationship'}' が表すテーブルの Row を一とし、多をもつ検索結果を生成する '{'@link OneToManyExecutor'}' を返します。
		 * @return 自動生成された '{'@link OneToManyExecutor'}' のサブクラス
		 */
		public /*++{1}Executor++*//*--*/O2MExecutor/*--*/<M> intercept() /*++'++*/{/*++'++*/
			if ($query != null) throw new IllegalStateException($path.getSchemaName() + " から直接使用することはできません");
			return new /*++{1}Executor++*//*--*/O2MExecutor/*--*/<>(this);
		/*++'++*/}/*++'++*/

		@Override
		public QueryConditionContext getContext() /*++'++*/{/*++'++*/
			if ($context == null) return $parent.getContext();

			return $context;
		/*++'++*/}/*++'++*/

		@Override
		public Relationship getRelationship() /*++'++*/{/*++'++*/
			if ($parent != null) /*++'++*/{/*++'++*/
				return $parent.getRelationship().find($fkName);
			/*++'++*/}/*++'++*/

			return ContextManager.get(RelationshipFactory.class).getInstance($query.manager.getTablePath());
		/*++'++*/}/*++'++*/

		@Override
		public Optimizer getOptimizer() /*++'++*/{/*++'++*/
			if ($query != null) return $query.getOptimizer();
			return null;
		/*++'++*/}/*++'++*/

		@Override
		public OrderByClause getOrderByClause() /*++'++*/{/*++'++*/
			if ($query == null) return $parent.getOrderByClause();

			OrderByClause clause = $query.orderByClause;
			if (clause == null) /*++'++*/{/*++'++*/
				clause = new OrderByClause();
				$query.orderByClause = clause;
			/*++'++*/}/*++'++*/

			return clause;
		/*++'++*/}/*++'++*/

		@Override
		public void setWhereClause(Condition condition) /*++'++*/{/*++'++*/
			if ($query == null) /*++'++*/{/*++'++*/
				$parent.setWhereClause(condition);
				return;
			/*++'++*/}/*++'++*/

			$query.condition = condition;
		/*++'++*/}/*++'++*/

		@Override
		public Condition getWhereClause() /*++'++*/{/*++'++*/
			if ($query == null) return $parent.getWhereClause();

			return $query.condition;
		/*++'++*/}/*++'++*/

		@Override
		public QueryRelationship getParent() /*++'++*/{/*++'++*/
			return $parent;
		/*++'++*/}/*++'++*/

		@Override
		public TablePath getTablePath() /*++'++*/{/*++'++*/
			return $path;
		/*++'++*/}/*++'++*/

		@Override
		public Query getRoot() /*++'++*/{/*++'++*/
			if ($query != null) return $query;
			return $parent.getRoot();
		/*++'++*/}/*++'++*/

		@Override
		public /*++{1}++*//*--*/RowBase/*--*/ createRow(DataObject data) /*++'++*/{/*++'++*/
			return $manager.createRow(data);
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
		extends AbstractSelectQueryColumn</*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<
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
		extends AbstractOrderQueryColumn<
			/*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<
				OrderByQueryColumn,
				Void>> /*++'++*/{/*++'++*/

		private OrderByQueryColumn(QueryRelationship relationship, String name) /*++'++*/{/*++'++*/
			super(relationship, name);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/
/*++'++*/}/*++'++*/
