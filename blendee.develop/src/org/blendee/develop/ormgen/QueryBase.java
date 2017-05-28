/*--*//*@formatter:off*//*--*/package /*++{0}++*//*--*/org.blendee.develop.ormgen/*--*/;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Generated;

import /*++{0}.{1}Manager.{1}Iterator++*//*--*/org.blendee.develop.ormgen.ManagerBase.IteratorBase/*--*/;
import org.blendee.support.AbstractOrderQueryColumn;
import org.blendee.support.AbstractSelectQueryColumn;
/*++{8}++*/
import org.blendee.support.None;
import org.blendee.support.NotUniqueException;
import org.blendee.support.OneToManyExecutor;
import org.blendee.support.OrderByOfferFunction;
import org.blendee.support.Query;
import org.blendee.support.QueryConditionContext;
import org.blendee.support.QueryContext;
import org.blendee.support.QueryOptions;
import org.blendee.support.QueryRelationship;
import org.blendee.support.ReferenceQueryColumn;
import org.blendee.support.SelectOffer;
import org.blendee.support.SelectOfferFunction;
import org.blendee.support.SelectOfferFunction.SelectOffers;
import org.blendee.internal.U;
import org.blendee.support.Subquery;
import org.blendee.support.WhereQueryColumn;
import org.blendee.jdbc.BlendeeContext;
import org.blendee.jdbc.ResourceLocator;
import org.blendee.orm.QueryOption;
import org.blendee.orm.DataObject;
import org.blendee.selector.AnchorOptimizerFactory;
import org.blendee.selector.Optimizer;
import org.blendee.selector.RuntimeOptimizer;
import org.blendee.selector.SimpleOptimizer;
import org.blendee.sql.Bindable;
import org.blendee.sql.Condition;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;

/**
 * 自動生成された '{'@link Query'}' の実装クラスです。
 *
 * パッケージ名 {0}
 * テーブル名 {1}
 */
@Generated(value = /*++'++*/{/*++'++*/"{9}"/*++'++*/}/*++'++*/)
public class /*++{1}Query++*//*--*/QueryBase/*--*/
	extends /*++{2}++*//*--*/Object/*--*/
	implements Query /*++'++*/{/*++'++*/

	private static final QueryContext<SelectQueryColumn> selectContext = (relationship, name) -> new SelectQueryColumn(relationship, name);

	private static final QueryContext<OrderByQueryColumn> orderByContext = (relationship, name) -> new OrderByQueryColumn(relationship, name);

	private static final QueryContext<WhereQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/>> whereContext =  QueryContext.newBuilder();

	private final /*++{1}Manager++*//*--*/ManagerBase/*--*/ manager = new /*++{1}Manager()++*//*--*/ManagerBase()/*--*/;

	/**
	 * WHERE 句用のカラムを選択するための '{'@link QueryRelationship'}' です。
	 * <br>
	 * このインスタンスは WHERE 句を新設するときのみ使用してください。
	 * <br>
	 * 一度の検索内で複数回使用すると、 WHERE 句を上書きすることになってしまうため、その場合は例外が発生します。
	 */
	public final /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<WhereQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/>, None> WHERE =
		new /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<>(
			this,
			whereContext,
			QueryConditionContext.WHERE);

	/**
	 * WHERE 句に AND 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
	 * <br>
	 * 既存の条件に AND 結合させるために使用しますが、先に '{'@link #WHERE'}' を使用していない場合は単体の条件となります。
	 */
	public final /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<WhereQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/>, None> AND =
		new /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<>(
			this,
			whereContext,
			QueryConditionContext.AND);

	/**
	 * WHERE 句に OR 結合する条件用のカラムを選択するための '{'@link QueryRelationship'}' です。
	 * <br>
	 * 既存の条件に OR 結合させるために使用しますが、先に '{'@link #WHERE'}' を使用していない場合は単体の条件となります。
	 */
	public final /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<WhereQueryColumn</*++{1}Query++*//*--*/QueryBase/*--*/>, None> OR =
		new /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<>(
			this,
			whereContext,
			QueryConditionContext.OR);

	/**
	 * この '{'@link Query'}' のテーブルを表す '{'@link QueryRelationship'}' を参照するためのインスタンスです。
	 */
	public final /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<ReferenceQueryColumn, None> rel =
		new /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<>(
			this,
			QueryContext.REFERENCE,
			QueryConditionContext.NULL);

	private Optimizer optimizer;

	private Condition condition;

	private OrderByClause orderByClause;

	/**
	 * ORDER BY 句用のカラムを選択するための '{'@link QueryRelationship'}' です。
	 */
	private final /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<SelectQueryColumn, None> select =
		new /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<>(
			this,
			selectContext,
			QueryConditionContext.NULL);

	/**
	 * ORDER BY 句用のカラムを選択するための '{'@link QueryRelationship'}' です。
	 */
	private final /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<OrderByQueryColumn, None> orderBy =
		new /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<>(
			this,
			orderByContext,
			QueryConditionContext.NULL);

	/**
	 * このクラスのインスタンスを生成します。
	 * <br>
	 * インスタンスは ID として、引数で渡された id を使用します。
	 * <br>
	 * フィールド定義の必要がなく、簡易に使用できますが、 ID は呼び出し側クラス内で一意である必要があります。
	 *
	 * @param id '{'@link Query'}' を使用するクラス内で一意の ID
	 * @return このクラスのインスタンス
	 */
	public static /*++{1}Query++*//*--*/QueryBase/*--*/ of(String id) /*++'++*/{/*++'++*/
		if (!U.isAvailable(id))
			throw new IllegalArgumentException("id が空です");

		return new /*++{1}Query++*//*--*/QueryBase/*--*/(getUsing(new Throwable().getStackTrace()[1]), id);
	/*++'++*/}/*++'++*/

	/**
	 * 空のインスタンスを生成します。
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/() /*++'++*/{}/*++'++*/

	/**
	 * このクラスのインスタンスを生成します。
	 * <br>
	 * このコンストラクタで生成されたインスタンス の SELECT 句で使用されるカラムは、 パラメータの '{'@link Optimizer'}' に依存します。
	 *
	 * @param optimizer SELECT 句を決定する
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/(Optimizer optimizer) /*++'++*/{/*++'++*/
		this.optimizer = Objects.requireNonNull(optimizer);
	/*++'++*/}/*++'++*/

	private /*++{1}Query++*//*--*/QueryBase/*--*/(Class<?> using, String id) /*++'++*/{/*++'++*/
		optimizer = BlendeeContext.get(AnchorOptimizerFactory.class).getInstance(
			id,
			/*++{1}Constants++*//*--*/Constants/*--*/.RESOURCE_LOCATOR,
			using);
	/*++'++*/}/*++'++*/

	/**
	 * SELECT 句を記述します。
	 *
	 * @param function
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ SELECT(
		SelectOfferFunction</*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<SelectQueryColumn, None>> function) /*++'++*/{/*++'++*/
		RuntimeOptimizer myOptimizer = new RuntimeOptimizer(/*++{1}Constants++*//*--*/Constants/*--*/.RESOURCE_LOCATOR);
		function.offer(select).get().forEach(c -> myOptimizer.add(c));
		optimizer = myOptimizer;
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * ORDER BY 句を記述します。
	 *
	 * @param function
	 * @return この '{'@link Query'}'
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ ORDER_BY(
		OrderByOfferFunction</*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<OrderByQueryColumn, None>> function) /*++'++*/{/*++'++*/
		function.offer(orderBy);
		return this;
	/*++'++*/}/*++'++*/

	@Override
	public boolean hasCondition() /*++'++*/{/*++'++*/
		return condition != null && condition.isAvailable();
	/*++'++*/}/*++'++*/

	@Override
	public void orderBy(OrderByClause clause) /*++'++*/{/*++'++*/
		if (orderByClause != null)
			throw new IllegalStateException("既に ORDER BY 句がセットされています");
		orderByClause = clause;
	/*++'++*/}/*++'++*/

	@Override
	public void where(Condition condition) /*++'++*/{/*++'++*/
		QueryConditionContext.WHERE.addCondition(WHERE, condition);
	/*++'++*/}/*++'++*/

	@Override
	public void and(Condition condition) /*++'++*/{/*++'++*/
		QueryConditionContext.AND.addCondition(AND, condition);
	/*++'++*/}/*++'++*/

	@Override
	public void or(Condition condition) /*++'++*/{/*++'++*/
		QueryConditionContext.OR.addCondition(OR, condition);
	/*++'++*/}/*++'++*/

	@Override
	public void where(Subquery subquery) /*++'++*/{/*++'++*/
		QueryConditionContext.WHERE.addCondition(WHERE, subquery.createCondition(this));
	/*++'++*/}/*++'++*/

	@Override
	public void and(Subquery subquery) /*++'++*/{/*++'++*/
		QueryConditionContext.AND.addCondition(AND, subquery.createCondition(this));
	/*++'++*/}/*++'++*/

	@Override
	public void or(Subquery subquery) /*++'++*/{/*++'++*/
		QueryConditionContext.OR.addCondition(OR, subquery.createCondition(this));
	/*++'++*/}/*++'++*/

	@Override
	public Relationship getRootRealtionship() /*++'++*/{/*++'++*/
		return BlendeeContext.get(RelationshipFactory.class).getInstance(manager.getResourceLocator());
	/*++'++*/}/*++'++*/

	private Optimizer getOptimizer() /*++'++*/{/*++'++*/
		if (optimizer != null) return optimizer;
		optimizer = new SimpleOptimizer(/*++{1}Constants++*//*--*/Constants/*--*/.RESOURCE_LOCATOR);
		return optimizer;
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
	public Optional</*++{1}++*//*--*/EntityBase/*--*/> willUnique() /*++'++*/{/*++'++*/
		return getUnique(execute());
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{1}++*//*--*/EntityBase/*--*/> willUnique(QueryOption... options) /*++'++*/{/*++'++*/
		return getUnique(execute(options));
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{1}++*//*--*/EntityBase/*--*/> fetch(String... primaryKeyMembers) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{1}++*//*--*/EntityBase/*--*/> fetch(Number... primaryKeyMembers) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{1}++*//*--*/EntityBase/*--*/> fetch(Bindable... primaryKeyMembers) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{1}++*//*--*/EntityBase/*--*/> fetch(QueryOptions options, String... primaryKeyMembers) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), options, primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{1}++*//*--*/EntityBase/*--*/> fetch(QueryOptions options, Number... primaryKeyMembers) /*++'++*/{/*++'++*/
		return manager.select(getOptimizer(), options, primaryKeyMembers);
	/*++'++*/}/*++'++*/

	@Override
	public Optional</*++{1}++*//*--*/EntityBase/*--*/> fetch(QueryOptions options, Bindable... primaryKeyMembers) /*++'++*/{/*++'++*/
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
	 *
	 * @return このインスタンス
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ resetCondition() /*++'++*/{/*++'++*/
		condition = null;
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現在保持している並び順をリセットします。
	 *
	 * @return このインスタンス
	 */
	public /*++{1}Query++*//*--*/QueryBase/*--*/ resetOrder() /*++'++*/{/*++'++*/
		orderByClause = null;
		return this;
	/*++'++*/}/*++'++*/

	/**
	 * 現在保持している条件、並び順をリセットします。
	 *
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
		extends OneToManyExecutor</*++{1}++*//*--*/EntityBase/*--*/, M> /*++'++*/{/*++'++*/

		private /*++{1}Executor++*//*--*/O2MExecutor/*--*/(QueryRelationship parent) /*++'++*/{/*++'++*/
			super(parent);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	private static Class<?> getUsing(StackTraceElement element) /*++'++*/{/*++'++*/
		try /*++'++*/{/*++'++*/
			return Class.forName(element.getClassName());
		/*++'++*/}/*++'++*/ catch (Exception e) /*++'++*/{/*++'++*/
			throw new IllegalStateException(e.toString());
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/

	private static Optional</*++{1}++*//*--*/EntityBase/*--*/> getUnique(/*++{1}Iterator++*//*--*/IteratorBase/*--*/ iterator) /*++'++*/{/*++'++*/
		if (!iterator.hasNext()) return Optional.empty();

		/*++{1}++*//*--*/EntityBase/*--*/ entity = iterator.next();

		if (iterator.hasNext()) throw new NotUniqueException();

		return Optional.of(entity);
	/*++'++*/}/*++'++*/

	/**
	 * 自動生成された '{'@link QueryRelationship'}' の実装クラスです。
	 * <br>
	 * 条件として使用できるカラムと、参照しているテーブルを内包しており、それらを使用して検索 SQL を生成可能にします。
	 *
	 * @param <T> 使用されるカラムのタイプ（ '{'@link #select'}', '{'@link #orderBy'}', '{'@link #WHERE'}', '{'@link #AND'}', '{'@link #OR'}', '{'@link #rel'}' ）にあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<T, M>
		implements QueryRelationship, SelectOffer /*++'++*/{/*++'++*/

		private final /*++{1}Query++*//*--*/QueryBase/*--*/ $query;

		private final QueryConditionContext $context;

		private final QueryRelationship $parent;

		private final String $fkName;

		private final ResourceLocator $locator;

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
		 *
		 * @param builder
		 * @param parent
		 * @param fkName
		 * @param locator
		 * @param root
		 */
		public /*++{1}Relationship++*//*--*/ConcreteQueryRelationship/*--*/(
			QueryContext<T> builder,
			QueryRelationship parent,
			String fkName,
			ResourceLocator locator,
			ResourceLocator root) /*++'++*/{/*++'++*/
			$query = null;
			$context = null;
			$parent = parent;
			$fkName = fkName;
			$locator = locator;

/*==ColumnPart2==*//*++{0}++*//*--*/columnName/*--*/ = builder.buildQueryColumn(
				this, /*++{1}Constants++*//*--*/Constants/*--*/./*++{0}++*//*--*/columnName/*--*/);
/*==ColumnPart2==*/
/*++{5}++*/

/*==RelationshipPart2==*//*--*/relationshipName/*--*//*++{2}++*/ = locator.equals(root) ? null : new /*++{0}Query.{0}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<T, /*++{4}++*//*--*/Object/*--*/>(
				builder, this, /*++{3}Constants++*//*--*/Constants/*--*/./*++{0}++*/_BY_/*++{1}++*/, /*++{0}Constants++*//*--*/Constants/*--*/.RESOURCE_LOCATOR, root);
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
			$locator = /*++{1}Constants++*//*--*/Constants/*--*/.RESOURCE_LOCATOR;

			/*--*/columnName = builder.buildQueryColumn(this, "columnName");/*--*/
/*++{5}++*/

/*==RelationshipPart3==*//*--*/relationshipName/*--*//*++{2}++*/ = new /*++{0}Query.{0}Relationship++*//*--*/ConcreteQueryRelationship/*--*/<T, /*++{4}++*//*--*/Object/*--*/>(
				builder, this, /*++{3}Constants++*//*--*/Constants/*--*/./*++{0}++*/_BY_/*++{1}++*/, /*++{0}Constants++*//*--*/Constants/*--*/.RESOURCE_LOCATOR, $locator);
/*==RelationshipPart3==*/
/*++{7}++*/
		/*++'++*/}/*++'++*/

		/**
		 * この '{'@link QueryRelationship'}' が表すテーブルの Entity を一とし、多をもつ検索結果を生成する '{'@link OneToManyExecutor'}' を返します。
		 *
		 * @return 自動生成された '{'@link OneToManyExecutor'}' のサブクラス
		 */
		public /*++{1}Executor++*//*--*/O2MExecutor/*--*/<M> intercept() /*++'++*/{/*++'++*/
			if ($query != null) throw new IllegalStateException($locator.getSchemaName() + " から直接使用することはできません");
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

			return BlendeeContext.get(RelationshipFactory.class).getInstance($query.manager.getResourceLocator());
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
		public ResourceLocator getResourceLocator() /*++'++*/{/*++'++*/
			return $locator;
		/*++'++*/}/*++'++*/

		@Override
		public Query getRoot() /*++'++*/{/*++'++*/
			if ($query != null) return $query;
			return $parent.getRoot();
		/*++'++*/}/*++'++*/

		@Override
		public /*++{1}++*//*--*/EntityBase/*--*/ createEntity(DataObject data) /*++'++*/{/*++'++*/
			return $manager.createEntity(data);
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
			None>> /*++'++*/{/*++'++*/

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
			None>> /*++'++*/{/*++'++*/

		private OrderByQueryColumn(QueryRelationship relationship, String name) /*++'++*/{/*++'++*/
			super(relationship, name);
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/
/*++'++*/}/*++'++*/
