/*--*//*@formatter:off*//*--*/package /*++[[PACKAGE]]++*//*--*/org.blendee.develop.ormgen/*--*/;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.Result;
import org.blendee.jdbc.ResultSetIterator;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.ColumnNameDataObjectBuilder;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObject;
import org.blendee.orm.DataObjectIterator;
import org.blendee.selector.AnchorOptimizerFactory;
import org.blendee.selector.Optimizer;
import org.blendee.selector.SimpleOptimizer;
import org.blendee.sql.Bindable;
import org.blendee.sql.Binder;
import org.blendee.sql.Criteria;
import org.blendee.sql.FromClause.JoinType;
import org.blendee.sql.GroupByClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.QueryBuilder;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.ValueExtractor;
import org.blendee.sql.ValueExtractorsConfigure;
import org.blendee.support.Executor;
import org.blendee.support.GroupByOfferFunction;
import org.blendee.support.GroupByQueryColumn;
import org.blendee.support.GroupByQueryRelationship;
import org.blendee.support.HavingQueryColumn;
import org.blendee.support.HavingQueryRelationship;
/*++[[IMPORTS]]++*/
import org.blendee.support.LogicalOperators;
import org.blendee.support.OnLeftQueryColumn;
import org.blendee.support.OnLeftQueryRelationship;
import org.blendee.support.OnRightQueryColumn;
import org.blendee.support.OnRightQueryRelationship;
import org.blendee.support.OneToManyExecutor;
import org.blendee.support.InstantOneToManyExecutor;
import org.blendee.support.OrderByOfferFunction;
import org.blendee.support.OrderByQueryColumn;
import org.blendee.support.OrderByQueryRelationship;
import org.blendee.support.Query;
import org.blendee.support.RightQuery;
import org.blendee.support.Row;
import org.blendee.support.RowIterator;
import org.blendee.support.RowManager;
import org.blendee.support.QueryColumn;
import org.blendee.support.QueryContext;
import org.blendee.support.QueryCriteriaContext;
import org.blendee.support.QueryHelper;
import org.blendee.support.QueryHelper.PlaybackExecutor;
import org.blendee.support.annotation.RowGetter;
import org.blendee.support.annotation.RowSetter;
import org.blendee.support.QueryOnClause;
import org.blendee.support.QueryRelationship;
import org.blendee.support.SelectOfferFunction;
import org.blendee.support.SelectQueryColumn;
import org.blendee.support.SelectQueryRelationship;
import org.blendee.support.WhereQueryColumn;
import org.blendee.support.WhereQueryRelationship;
/*--*/import org.blendee.support.annotation.RowRelationship;/*--*/

/**
 * 自動生成されたテーブル操作クラスです。
[[TABLE_COMMENT]]
 */
/*++[[ANNOTATION]]++*/public class /*++[[TABLE]]++*//*--*/TableBase/*--*/
	extends /*++[[PARENT]]++*//*--*/Object/*--*/
	implements
		RowManager<Row>,
		Query,
		Executor</*++[[TABLE]]++*//*--*/TableBase/*--*/.Iterator, /*++[[TABLE]]++*//*--*/TableBase/*--*/.Row>,
		RightQuery</*++[[TABLE]]++*//*--*/TableBase/*--*/.OnRightQRel> {

	/**
	 * この定数クラスのスキーマ名
	 */
	public static final String SCHEMA = "[[SCHEMA]]";

	/**
	 * この定数クラスのテーブル名
	 */
	public static final String TABLE = "[[TABLE]]";

	/**
	 * この定数クラスのテーブルを指す {@link TablePath}
	 */
	public static final TablePath $TABLE = new TablePath(SCHEMA, TABLE);

/*++[[COLUMN_NAMES_PART]]++*/
/*==ColumnNamesPart==*/
	/**
[[COMMENT]]
	 */
	public static final String /*++[[COLUMN]]++*//*--*/columnName/*--*/ = "[[COLUMN]]";
/*==ColumnNamesPart==*/

/*++[[RELATIONSHIPS_PART]]++*/
/*==RelationshipsPart==*/
	/**
	 * 参照先テーブル名 [[REFERENCE]]<br>
	 * 外部キー名 [[FK]]<br>
	 */
	public static final String /*++[[REFERENCE]]++*/$/*++[[FK]]++*/ = "[[FK]]";
/*==RelationshipsPart==*/

	/**
	 * 登録用コンストラクタです。
	 * @return {@link Row}
	 */
	public static Row row() {
		return new Row();
	}

	/**
	 * 参照、更新用コンストラクタです。<br>
	 * aggregate の検索結果からカラム名により値を取り込みます。
	 * @param result 値を持つ {@link Result}
	 * @return {@link Row}
	 */
	public static Row row(Result result) {
		return new Row(result);
	}

	/**
	 * 参照、更新用コンストラクタです。
	 * @param data 値を持つ {@link DataObject}
	 * @return {@link Row}
	 */
	public static Row row(DataObject data) {
		return new Row(data);
	}

	/**
	 * 自動生成された {@link Row} の実装クラスです。
	 */
	public static class Row extends /*++[[ROW_PARENT]]++*//*--*/Object/*--*/
		implements org.blendee.support.Row {

		private final DataObject data$;

		private final Relationship relationship$;

		private Row() {
			relationship$ = ContextManager.get(RelationshipFactory.class).getInstance($TABLE);
			data$ = new DataObject(relationship$);
		}

		private Row(DataObject data) {
			relationship$ = ContextManager.get(RelationshipFactory.class).getInstance($TABLE);
			this.data$ = data;
		}

		private Row(Result result) {
			relationship$ = ContextManager.get(RelationshipFactory.class).getInstance($TABLE);
			this.data$ = ColumnNameDataObjectBuilder.build(result, relationship$, ContextManager.get(ValueExtractorsConfigure.class).getValueExtractors());
		}

		@Override
		public DataObject dataObject() {
			return data$;
		}

		@Override
		public TablePath tablePath() {
			return $TABLE;
		}

/*++[[ROW_PROPERTY_ACCESSOR_PART]]++*/
/*==RowPropertyAccessorPart==*/
		/**
		 * setter
	[[COMMENT]]
		 * @param value [[TYPE]]
		 */
		@RowSetter(column = "[[TABLE]]", type = /*++[[TYPE]]++*//*--*/Object/*--*/.class)
		public void set/*++[[METHOD]]++*/(/*++[[TYPE]]++*//*--*/Object/*--*/ value) {
			/*++[[NULL_CHECK]]++*/ValueExtractor valueExtractor = ContextManager.get(ValueExtractorsConfigure.class).getValueExtractors().selectValueExtractor(
				relationship$.getColumn("[[COLUMN]]").getType());
			data$.setValue("[[COLUMN]]", valueExtractor.extractAsBinder(value));
		}

		/**
		 * getter
	[[COMMENT]]
		 * @return [[TYPE]]
		 */
		@RowGetter(column = "[[TABLE]]", type = /*++[[TYPE]]++*//*--*/Object/*--*/.class, optional = /*++[[OPTIONAL]]++*//*--*/false/*--*/)
		public /*++[[RETURN_TYPE]]++*/ /*--*/String/*--*/get/*++[[METHOD]]++*/() {
			Binder binder = data$.getBinder("[[COLUMN]]");
			return /*++[[PREFIX]]++*/(/*++[[TYPE]]++*//*--*/String/*--*/) binder.getValue()/*++[[SUFFIX]]++*/;
		}

/*==RowPropertyAccessorPart==*/
/*++[[ROW_RELATIONSHIP_PART]]++*/
/*==RowRelationshipPart==*/
		/**
		 * このレコードが参照しているレコードの Row を返します。<br>
		 * 参照先テーブル名 [[REFERENCE]]<br>
		 * 外部キー名 [[FK]]<br>
		 * 項目名 [[FK_COLUMNS]]<br>
		 * @return 参照しているレコードの Row
		 */
		@RowRelationship(fk = "[[FK]]", referenced = /*++[[REFERENCE_PACKAGE]].[[REFERENCE]].++*/Row.class)
		public /*++[[REFERENCE_PACKAGE]].[[REFERENCE]].++*/Row /*++[[METHOD]]++*//*--*/getRelationship/*--*/() {
			return /*++[[REFERENCE_PACKAGE]].[[REFERENCE]].++*/row(
				data$.getDataObject(/*++[[REFERENCE]]++*/$/*++[[FK]]++*/));
		}

/*==RowRelationshipPart==*/
	}

	private static final QueryContext<SelectQCol> selectContext$ = (relationship, name) -> new SelectQCol(relationship, name);

	private static final QueryContext<GroupByQCol> groupByContext$ = (relationship, name) -> new GroupByQCol(relationship, name);

	private static final QueryContext<OrderByQCol> orderByContext$ = (relationship, name) -> new OrderByQCol(relationship, name);

	private static final QueryContext<WhereQueryColumn<WhereLogicalOperators>> whereContext$ =  QueryContext.newWhereBuilder();

	private static final QueryContext<HavingQueryColumn<HavingLogicalOperators>> havingContext$ =  QueryContext.newHavingBuilder();

	private static final QueryContext<OnLeftQueryColumn<OnLeftLogicalOperators>> onLeftContext$ =  QueryContext.newOnLeftBuilder();

	private static final QueryContext<OnRightQueryColumn<OnRightLogicalOperators>> onRightContext$ =  QueryContext.newOnRightBuilder();

	/**
	 * WHERE 句 で使用する AND, OR です。
	 */
	public class WhereLogicalOperators implements LogicalOperators<WhereQRel> {

		private WhereLogicalOperators() {}

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final WhereQRel AND = new WhereQRel(
				/*++[[TABLE]]++*//*--*/TableBase/*--*/.this,
				whereContext$,
				QueryCriteriaContext.AND);

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final WhereQRel OR = new WhereQRel(
				/*++[[TABLE]]++*//*--*/TableBase/*--*/.this,
				whereContext$,
				QueryCriteriaContext.OR);

		@Override
		public WhereQRel defaultOperator() {
			return AND;
		}
	}

	/**
	 * HAVING 句 で使用する AND, OR です。
	 */
	public class HavingLogicalOperators implements LogicalOperators<HavingQRel> {

		private HavingLogicalOperators() {}

		/**
		 * HAVING 句に AND 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final HavingQRel AND =
			new HavingQRel(
				/*++[[TABLE]]++*//*--*/TableBase/*--*/.this,
				havingContext$,
				QueryCriteriaContext.AND);

		/**
		 * HAVING 句に OR 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final HavingQRel OR = new HavingQRel(
				/*++[[TABLE]]++*//*--*/TableBase/*--*/.this,
				havingContext$,
				QueryCriteriaContext.OR);

		@Override
		public HavingQRel defaultOperator() {
			return AND;
		}
	}

	/**
	 * ON 句 (LEFT) で使用する AND, OR です。
	 */
	public class OnLeftLogicalOperators implements LogicalOperators<OnLeftQRel> {

		private OnLeftLogicalOperators() {}

		/**
		 * ON 句に AND 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final OnLeftQRel AND =
			new OnLeftQRel(
				/*++[[TABLE]]++*//*--*/TableBase/*--*/.this,
				onLeftContext$,
				QueryCriteriaContext.AND);

		/**
		 * ON 句に OR 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final OnLeftQRel OR =
			new OnLeftQRel(
				/*++[[TABLE]]++*//*--*/TableBase/*--*/.this,
				onLeftContext$,
				QueryCriteriaContext.OR);

		@Override
		public OnLeftQRel defaultOperator() {
			return AND;
		}
	}


	/**
	 * ON 句 (RIGHT) で使用する AND, OR です。
	 */
	public class OnRightLogicalOperators implements LogicalOperators<OnRightQRel> {

		private OnRightLogicalOperators() {}

		/**
		 * ON 句に AND 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final OnRightQRel AND =
			new OnRightQRel(
				/*++[[TABLE]]++*//*--*/TableBase/*--*/.this,
				onRightContext$,
				QueryCriteriaContext.AND);

		/**
		 * ON 句に OR 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final OnRightQRel OR =
			new OnRightQRel(
				/*++[[TABLE]]++*//*--*/TableBase/*--*/.this,
				onRightContext$,
				QueryCriteriaContext.OR);

		@Override
		public OnRightQRel defaultOperator() {
			return AND;
		}
	}

	private final WhereLogicalOperators whereOperators$ = new WhereLogicalOperators();

	private final HavingLogicalOperators havingOperators$ = new HavingLogicalOperators();

	private final OnLeftLogicalOperators onLeftOperators$ = new OnLeftLogicalOperators();

	private final OnRightLogicalOperators onRightOperators$ = new OnRightLogicalOperators();

	/**
	 * SELECT 句用のカラムを選択するための {@link QueryRelationship} です。
	 */
	private final SelectQRel select$ = new SelectQRel(
			this,
			selectContext$,
			QueryCriteriaContext.NULL);

	/**
	 * GROUP BY 句用のカラムを選択するための {@link QueryRelationship} です。
	 */
	private final GroupByQRel groupBy$ = new GroupByQRel(
			this,
			groupByContext$,
			QueryCriteriaContext.NULL);

	/**
	 * ORDER BY 句用のカラムを選択するための {@link QueryRelationship} です。
	 */
	private final OrderByQRel orderBy$ = new OrderByQRel(
			this,
			orderByContext$,
			QueryCriteriaContext.NULL);

	private final QueryHelper<SelectQRel, GroupByQRel, WhereQRel, HavingQRel, OrderByQRel, OnLeftQRel> helper$ = new QueryHelper<>(
		$TABLE,
		select$,
		groupBy$,
		orderBy$,
		whereOperators$,
		havingOperators$,
		onLeftOperators$);

	/**
	 * このクラスのインスタンスを生成します。<br>
	 * インスタンスは ID として、引数で渡された id を使用します。<br>
	 * フィールド定義の必要がなく、簡易に使用できますが、 ID は呼び出し側クラス内で一意である必要があります。
	 * @param id {@link Query} を使用するクラス内で一意の ID
	 * @return このクラスのインスタンス
	 */
	public static /*++[[TABLE]]++*//*--*/TableBase/*--*/ of(String id) {
		if (id == null || id.equals(""))
			throw new IllegalArgumentException("id が空です");

		return new /*++[[TABLE]]++*//*--*/TableBase/*--*/(getUsing(new Throwable().getStackTrace()[1]), id);
	}


	/**
	 * 空のインスタンスを生成します。
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/() {}

	/**
	 * このクラスのインスタンスを生成します。<br>
	 * このコンストラクタで生成されたインスタンス の SELECT 句で使用されるカラムは、 パラメータの {@link Optimizer} に依存します。
	 * @param optimizer SELECT 句を決定する
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/(Optimizer optimizer) {
		helper$.setOptimizer(Objects.requireNonNull(optimizer));
	}

	private /*++[[TABLE]]++*//*--*/TableBase/*--*/(Class<?> using, String id) {
		helper$.setOptimizer(
			ContextManager.get(AnchorOptimizerFactory.class).getInstance(id, $TABLE, using));
	}

	@Override
	public Row createRow(DataObject data) {
		return new Row(data);
	}

	@Override
	public TablePath getTablePath() {
		return $TABLE;
	}

	/**
	 *  {@link DataObjectIterator} を {@link RowIterator} に変換します。
	 * @param base 変換される {@link DataObjectIterator}
	 * @return {@link RowIterator}
	 */
	public Iterator wrap(DataObjectIterator base) {
		return new Iterator(base);
	}

	/**
	 * Iterator クラスです。
	 */
	public class Iterator extends RowIterator<Row> {

		/**
		 * 唯一のコンストラクタです。
		 * @param iterator
		 */
		private Iterator(
			DataObjectIterator iterator) {
			super(iterator);
		}

		@Override
		public Row next() {
			return createRow(nextDataObject());
		}
	}

	/**
	 * パラメータの条件にマッチするレコードを検索し、 {@link Iterator} として返します。<br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 * @param criteria WHERE 句となる条件
	 * @param order  ORDER 句
	 * @param options 検索オプション
	 * @return {@link RowIterator}
	 */
	public Iterator select(
		Criteria criteria,
		OrderByClause order,
		SQLDecorator... options) {
		return select(
			new SimpleOptimizer(getTablePath()),
			criteria,
			order,
			options);
	}

	/**
	 * パラメータの条件にマッチするレコードを検索し、 {@link Iterator} として返します。
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param criteria WHERE 句となる条件
	 * @param order  ORDER 句
	 * @param options 検索オプション
	 * @return {@link RowIterator}
	 */
	public Iterator select(
		Optimizer optimizer,
		Criteria criteria,
		OrderByClause order,
		SQLDecorator... options) {
		return wrap(new DataAccessHelper().getDataObjects(
			optimizer,
			criteria,
			order,
			options));
	}

	/**
	 * この {@link Query} のテーブルを表す {@link QueryRelationship} を参照するためのインスタンスです。
	 * @return rel
	 */
	public ExtQRel<QueryColumn, Void> rel() {
		return new ExtQRel<>(this, QueryContext.OTHER, QueryCriteriaContext.NULL);
	}

	/**
	 * SELECT 句を記述します。
	 * @param function
	 * @return この {@link Query}
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ SELECT(
		SelectOfferFunction<SelectQRel> function) {
		helper$.SELECT(function);
		return this;
	}

	/**
	 * DISTINCT を使用した SELECT 句を記述します。
	 * @param function
	 * @return この {@link Query}
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ SELECT_DISTINCT(
		SelectOfferFunction<SelectQRel> function) {
		helper$.SELECT_DISTINCT(function);
		return this;
	}

	/**
	 * COUNT(*) を使用した SELECT 句を記述します。
	 * @return この {@link Query}
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ SELECT_COUNT() {
		helper$.SELECT_COUNT();
		return this;
	}

	/**
	 * GROUP BY 句を記述します。
	 * @param function
	 * @return この {@link Query}
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ GROUP_BY(
		GroupByOfferFunction<GroupByQRel> function) {
		helper$.GROUP_BY(function);
		return this;
	}

	/**
	 * WHERE 句を記述します。
	 * @param consumers
	 * @return この {@link Query}
	 */
	@SafeVarargs
	public final /*++[[TABLE]]++*//*--*/TableBase/*--*/ WHERE(
		Consumer<WhereQRel>... consumers) {
		helper$.WHERE(consumers);
		return this;
	}

	/**
	 * WHERE 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 * @return {@link Criteria}
	 */
	public Criteria createWhereCriteria(
		Consumer<WhereQRel> consumer) {
		return helper$.createWhereCriteria(consumer);
	}

	/**
	 * HAVING 句を記述します。
	 * @param consumers
	 * @return この {@link Query}
	 */
	@SafeVarargs
	public final /*++[[TABLE]]++*//*--*/TableBase/*--*/ HAVING(
		Consumer<HavingQRel>... consumers) {
		helper$.HAVING(consumers);
		return this;
	}

	/**
	 * HAVING 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 * @return {@link Criteria}
	 */
	public Criteria createHavingCriteria(
		Consumer<HavingQRel> consumer) {
		return helper$.createHavingCriteria(consumer);
	}

	/**
	 * このクエリに INNER JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return ON
	 */
	public <R extends OnRightQueryRelationship> QueryOnClause<OnLeftQRel, R, /*++[[TABLE]]++*//*--*/TableBase/*--*/> INNER_JOIN(RightQuery<R> right) {
		return helper$.INNER_JOIN(right, this);
	}

	/**
	 * このクエリに LEFT OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return ON
	 */
	public <R extends OnRightQueryRelationship> QueryOnClause<OnLeftQRel, R, /*++[[TABLE]]++*//*--*/TableBase/*--*/> LEFT_OUTER_JOIN(RightQuery<R> right) {
		return helper$.LEFT_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに RIGHT OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return ON
	 */
	public <R extends OnRightQueryRelationship> QueryOnClause<OnLeftQRel, R, /*++[[TABLE]]++*//*--*/TableBase/*--*/> RIGHT_OUTER_JOIN(RightQuery<R> right) {
		return helper$.RIGHT_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに FULL OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return ON
	 */
	public <R extends OnRightQueryRelationship> QueryOnClause<OnLeftQRel, R, /*++[[TABLE]]++*//*--*/TableBase/*--*/> FULL_OUTER_JOIN(RightQuery<R> right) {
		return helper$.FULL_OUTER_JOIN(right, this);
	}

	/**
	 * UNION するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param query UNION 対象
	 * @return この {@link Query}
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ UNION(ComposedSQL query) {
		helper$.UNION(query);
		return this;
	}

	/**
	 * UNION ALL するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param query UNION ALL 対象
	 * @return この {@link Query}
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ UNION_ALL(ComposedSQL query) {
		helper$.UNION_ALL(query);
		return this;
	}

	/**
	 * ORDER BY 句を記述します。
	 * @param function
	 * @return この {@link Query}
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ ORDER_BY(
		OrderByOfferFunction<OrderByQRel> function) {
		helper$.ORDER_BY(function);
		return this;
	}

	@Override
	public boolean hasWhereClause() {
		return helper$.hasWhereClause();
	}

	/**
	 * 新規に GROUP BY 句をセットします。
	 * @param clause 新 ORDER BY 句
	 * @return {@link Query} 自身
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ groupBy(GroupByClause clause) {
		helper$.setGroupByClause(clause);
		return this;
	}

	/**
	 * 新規に ORDER BY 句をセットします。
	 * @param clause 新 ORDER BY 句
	 * @return {@link Query} 自身
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ orderBy(OrderByClause clause) {
		helper$.setOrderByClause(clause);
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たな条件を AND 結合します。<br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria AND 結合する新条件
	 * @return {@link Query} 自身
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ and(Criteria criteria) {
		helper$.and(criteria);
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たな条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria OR 結合する新条件
	 * @return {@link Query} 自身
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ or(Criteria criteria) {
		helper$.or(criteria);
		return this;
	}

	/**
	 * 生成された SQL 文を加工する {SQLDecorator} を設定します。
	 * @param decorators {@link SQLDecorator}
	 * @return {@link Query} 自身
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ apply(SQLDecorator... decorators) {
		helper$.apply(decorators);
		return this;
	}

	@Override
	public Relationship getRootRealtionship() {
		return ContextManager.get(RelationshipFactory.class).getInstance(getTablePath());
	}

	@Override
	public LogicalOperators<?> getWhereLogicalOperators() {
		return whereOperators$;
	}

	@Override
	public LogicalOperators<?> getHavingLogicalOperators() {
		return havingOperators$;
	}

	@Override
	public LogicalOperators<?> getOnLeftLogicalOperators() {
		return onLeftOperators$;
	}

	@Override
	public LogicalOperators<?> getOnRightLogicalOperators() {
		return onRightOperators$;
	}

	@Override
	public SQLDecorator[] decorators() {
		return helper$.decorators();
	}

	@Override
	public Iterator execute() {
		helper$.checkRowMode();
		return wrap(helper$.executor().execute());
	}

	@Override
	public Optional<Row> fetch(String... primaryKeyMembers) {
		helper$.checkRowMode();
		return helper$.executor().fetch(primaryKeyMembers).map(o -> createRow(o));
	}

	@Override
	public Optional<Row> fetch(Number... primaryKeyMembers) {
		helper$.checkRowMode();
		return helper$.executor().fetch(primaryKeyMembers).map(o -> createRow(o));
	}

	@Override
	public Optional<Row> fetch(Bindable... primaryKeyMembers) {
		helper$.checkRowMode();
		return helper$.executor().fetch(primaryKeyMembers).map(o -> createRow(o));
	}

	@Override
	public int count() {
		helper$.checkRowMode();
		return helper$.executor().count();
	}

	@Override
	public ComposedSQL toCountSQL() {
		helper$.checkRowMode();
		return helper$.executor().toCountSQL();
	}

	@Override
	public void aggregate(Consumer<BResultSet> consumer) {
		helper$.quitRowMode();
		org.blendee.support.Executor.super.aggregate(consumer);
	}

	@Override
	public <T> T aggregateAndGet(Function<BResultSet, T> function) {
		helper$.quitRowMode();
		return org.blendee.support.Executor.super.aggregateAndGet(function);
	}

	@Override
	public ResultSetIterator aggregate() {
		helper$.quitRowMode();
		return org.blendee.support.Executor.super.aggregate();
	}

	@Override
	public String sql() {
		return helper$.composeSQL().sql();
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		return helper$.composeSQL().complement(done, statement);
	}

	@Override
	public Executor reproduce(Object... placeHolderValues) {
		return new Executor(helper$.executor().reproduce(placeHolderValues));
	}

	@Override
	public void joinTo(QueryBuilder builder, JoinType joinType, Criteria onCriteria) {
		helper$.joinTo(builder, joinType, onCriteria);
	}

	@Override
	public QueryBuilder toQueryBuilder() {
		return helper$.buildBuilder();
	}

	@Override
	public void forSubquery(boolean forSubquery) {
		 helper$.forSubquery(forSubquery);
	}

	/**
	 * 現在保持している WHERE 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ resetWhere() {
		helper$.resetWhere();
		return this;
	}

	/**
	 * 現在保持している HAVING 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ resetHaving() {
		helper$.resetHaving();
		return this;
	}

	/**
	 * 現在保持している SELECT 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ resetSelect() {
		helper$.resetSelect();
		return this;
	}

	/**
	 * 現在保持している GROUP BY 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ resetGroupBy() {
		helper$.resetGroupBy();
		return this;
	}

	/**
	 * 現在保持している ORDER BY 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ resetOrderBy() {
		helper$.resetOrderBy();
		return this;
	}

	/**
	 * 現在保持している UNION をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ resetUnions() {
		helper$.resetUnions();
		return this;
	}

	/**
	 * 現在保持している JOIN をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ resetJoins() {
		helper$.resetJoins();
		return this;
	}

	/**
	 * 現在保持している {@link SQLDecorator} をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ resetDecorators() {
		helper$.resetDecorators();
		return this;
	}

	/**
	 * 現在保持している条件、並び順をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableBase/*--*/ reset() {
		helper$.reset();
		return this;
	}

	@Override
	public void quitRowMode() {
		helper$.quitRowMode();
	}

	@Override
	public boolean rowMode() {
		return helper$.rowMode();
	}

	@Override
	public Executor executor() {
		return  new Executor(helper$.executor());
	}

	@Override
	public OnRightQRel joint() {
		return onRightOperators$.AND;
	}

	@Override
	public String toString() {
		return helper$.toString();
	}

	private static Class<?> getUsing(StackTraceElement element) {
		try {
			return Class.forName(element.getClassName());
		} catch (Exception e) {
			throw new IllegalStateException(e.toString());
		}
	}

	/**
	 * 自動生成された {@link QueryRelationship} の実装クラスです。<br>
	 * 条件として使用できるカラムを内包しており、それらを使用して検索 SQL を生成可能にします。
	 * @param <T> 使用されるカラムのタイプにあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class QRel<T, M> implements QueryRelationship {

		private final /*++[[TABLE]]++*//*--*/TableBase/*--*/ table$;

		private final QueryCriteriaContext context$;

		private final QueryRelationship parent$;

		private final String fkName$;

		private final TablePath path$;

/*++[[COLUMN_PART1]]++*/
/*==ColumnPart1==*/
		/**
		 * 項目名 [[COLUMN]]
		 */
		public final T /*++[[COLUMN]]++*//*--*/columnName/*--*/;

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
			TablePath path$) {
			table$ = null;
			context$ = null;
			this.parent$ = parent$;
			this.fkName$ = fkName$;
			this.path$ = path$;

/*++[[COLUMN_PART2]]++*/
/*==ColumnPart2==*/this./*++[[COLUMN]]++*//*--*/columnName/*--*/ = builder$.buildQueryColumn(
				this, /*++[[PACKAGE]].[[TABLE]].[[COLUMN]]++*//*--*/TableBase.columnName/*--*/);
/*==ColumnPart2==*/
		}

		private QRel(
			/*++[[TABLE]]++*//*--*/TableBase/*--*/ table$,
			QueryContext<T> builder$,
			QueryCriteriaContext context$) {
			this.table$ = table$;
			this.context$ = context$;
			parent$ = null;
			fkName$ = null;
			path$ = $TABLE;

			/*--*/columnName = null;/*--*/
/*++[[COLUMN_PART2]]++*/
		}

		/**
		 * この {@link QueryRelationship} が表すテーブルの Row を一とし、多をもつ検索結果を生成する {@link OneToManyExecutor} を返します。
		 * @return {@link OneToManyExecutor}
		 */
		public OneToManyExecutor<Row, M> intercept() {
			if (table$ != null) throw new IllegalStateException(path$.getSchemaName() + " から直接使用することはできません");
			if (!getRoot().rowMode()) throw new IllegalStateException("集計モードでは実行できない処理です");
			return new InstantOneToManyExecutor<>(this, getRoot().decorators());
		}

		@Override
		public QueryCriteriaContext getContext() {
			if (context$ == null) return parent$.getContext();

			return context$;
		}

		@Override
		public Relationship getRelationship() {
			if (parent$ != null) {
				return parent$.getRelationship().find(fkName$);
			}

			return ContextManager.get(RelationshipFactory.class).getInstance(table$.getTablePath());
		}

		@Override
		public Optimizer getOptimizer() {
			if (table$ != null) return table$.helper$.getOptimizer();
			return null;
		}

		@Override
		public GroupByClause getGroupByClause() {
			if (table$ == null) return parent$.getGroupByClause();
			return table$.helper$.getGroupByClause();
		}

		@Override
		public OrderByClause getOrderByClause() {
			if (table$ == null) return parent$.getOrderByClause();
			return table$.helper$.getOrderByClause();
		}

		@Override
		public Criteria getWhereClause() {
			if (table$ == null) return parent$.getWhereClause();
			return table$.helper$.getWhereClause();
		}

		@Override
		public QueryRelationship getParent() {
			return parent$;
		}

		@Override
		public TablePath getTablePath() {
			return path$;
		}

		@Override
		public Query getRoot() {
			if (table$ != null) return table$;
			return parent$.getRoot();
		}

		@Override
		public Row createRow(DataObject data) {
			return table$.createRow(data);
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
	 * 自動生成された {@link QueryRelationship} の実装クラスです。<br>
	 * 条件として使用できるカラムと、参照しているテーブルを内包しており、それらを使用して検索 SQL を生成可能にします。
	 * @param <T> 使用されるカラムのタイプにあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class ExtQRel<T, M> extends QRel<T, M> {

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
			TablePath root$) {
			super(builder$, parent$, fkName$, path$);
			/*--?--*/this.builder$ = builder$;/*--?--*/
			/*--?--*/this.root$ = root$;/*--?--*/
		}

		private ExtQRel(
			/*++[[TABLE]]++*//*--*/TableBase/*--*/ table$,
			QueryContext<T> builder$,
			QueryCriteriaContext context$) {
			super(table$, builder$, context$);
			/*--?--*/this.builder$ = builder$;/*--?--*/
			/*--?--*/root$ = null;/*--?--*/
		}

/*++[[QUERY_RELATIONSHIP_PART]]++*/
/*==QueryRelationshipPart==*/
		/**
		 * 参照先テーブル名 [[REFERENCE]]
		 * 外部キー名 [[FK]]
		 * @return [[REFERENCE]] relationship
		 */
		public /*++[[REFERENCE_PACKAGE]].[[REFERENCE]].++*/ExtQRel<T, /*++[[MANY]]++*//*--*/Object/*--*/> /*--*/relationshipName/*--*//*++[[RELATIONSHIP]]++*/() {
			return new /*++[[REFERENCE_PACKAGE]].[[REFERENCE]].++*/ExtQRel<>(
				builder$,
				this,
				/*++[[PACKAGE]].[[TABLE]].[[REFERENCE]]++*/$/*++[[FK]]++*/,
				/*++[[REFERENCE_PACKAGE]].[[REFERENCE]].++*/$TABLE,
				root$ != null ? root$ : super.path$);
		}
/*==QueryRelationshipPart==*/
	}

	/**
	 * SELECT 句用
	 */
	public static class SelectQRel extends ExtQRel<SelectQCol, Void> implements SelectQueryRelationship {

		private SelectQRel(
			/*++[[TABLE]]++*//*--*/TableBase/*--*/ table$,
			QueryContext<SelectQCol> builder$,
			QueryCriteriaContext context$) {
			super(table$, builder$, context$);
		}
	}

	/**
	 * WHERE 句用
	 */
	public static class WhereQRel extends ExtQRel<WhereQueryColumn<WhereLogicalOperators>, Void> implements WhereQueryRelationship {

		private WhereQRel(
			/*++[[TABLE]]++*//*--*/TableBase/*--*/ table$,
			QueryContext<WhereQueryColumn<WhereLogicalOperators>> builder$,
			QueryCriteriaContext context$) {
			super(table$, builder$, context$);
		}
	}

	/**
	 * GROUB BY 句用
	 */
	public static class GroupByQRel extends ExtQRel<GroupByQCol, Void> implements GroupByQueryRelationship {

		private GroupByQRel(
			/*++[[TABLE]]++*//*--*/TableBase/*--*/ table$,
			QueryContext<GroupByQCol> builder$,
			QueryCriteriaContext context$) {
			super(table$, builder$, context$);
		}
	}

	/**
	 * HAVING 句用
	 */
	public static class HavingQRel extends ExtQRel<HavingQueryColumn<HavingLogicalOperators>, Void> implements HavingQueryRelationship {

		private HavingQRel(
			/*++[[TABLE]]++*//*--*/TableBase/*--*/ table$,
			QueryContext<HavingQueryColumn<HavingLogicalOperators>> builder$,
			QueryCriteriaContext context$) {
			super(table$, builder$, context$);
		}
	}

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByQRel extends ExtQRel<OrderByQCol, Void> implements OrderByQueryRelationship {

		private OrderByQRel(
			/*++[[TABLE]]++*//*--*/TableBase/*--*/ table$,
			QueryContext<OrderByQCol> builder$,
			QueryCriteriaContext context$) {
			super(table$, builder$, context$);
		}
	}

	/**
	 * ON 句 (LEFT) 用
	 */
	public static class OnLeftQRel extends ExtQRel<OnLeftQueryColumn<OnLeftLogicalOperators>, Void> implements OnLeftQueryRelationship {

		private OnLeftQRel(
			/*++[[TABLE]]++*//*--*/TableBase/*--*/ table$,
			QueryContext<OnLeftQueryColumn<OnLeftLogicalOperators>> builder$,
			QueryCriteriaContext context$) {
			super(table$, builder$, context$);
		}
	}

	/**
	 * ON 句 (RIGHT) 用
	 */
	public static class OnRightQRel extends QRel<OnRightQueryColumn<OnRightLogicalOperators>, Void> implements OnRightQueryRelationship {

		private OnRightQRel(
			/*++[[TABLE]]++*//*--*/TableBase/*--*/ table$,
			QueryContext<OnRightQueryColumn<OnRightLogicalOperators>> builder$,
			QueryCriteriaContext context$) {
			super(table$, builder$, context$);
		}
	}

	/**
	 * SELECT 句用
	 */
	public static class SelectQCol
		extends SelectQueryColumn {

		private SelectQCol(QueryRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * GROUP BY 句用
	 */
	public static class GroupByQCol
		extends GroupByQueryColumn {

		private GroupByQCol(QueryRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByQCol
		extends OrderByQueryColumn {

		private OrderByQCol(QueryRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * Executor
	 */
	public class Executor
		implements org.blendee.support.Executor<Iterator, Row> {

		private final PlaybackExecutor inner;

		private Executor(PlaybackExecutor inner) {
			this.inner = inner;
		}

		@Override
		public Iterator execute() {
			return wrap(inner.execute());
		}

		@Override
		public Optional<Row> fetch(Bindable... primaryKeyMembers) {
			return inner.fetch(primaryKeyMembers).map(object -> createRow(object));
		}

		@Override
		public int count() {
			return inner.count();
		}

		@Override
		public ComposedSQL toCountSQL() {
			return inner.toCountSQL();
		}

		@Override
		public boolean rowMode() {
			return inner.rowMode();
		}

		@Override
		public String sql() {
			return inner.sql();
		}

		@Override
		public int complement(int done, BPreparedStatement statement) {
			return inner.complement(done, statement);
		}

		@Override
		public Executor reproduce(Object... placeHolderValues) {
			return new Executor(inner.reproduce(placeHolderValues));
		}
	}
}
