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
import org.blendee.sql.SQLQueryBuilder;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.ValueExtractor;
import org.blendee.sql.ValueExtractorsConfigure;
import org.blendee.support.Query;
import org.blendee.support.GroupByOfferFunction;
import org.blendee.support.GroupByColumn;
import org.blendee.support.GroupByRelationship;
import org.blendee.support.HavingColumn;
import org.blendee.support.HavingRelationship;
/*++[[IMPORTS]]++*/
import org.blendee.support.LogicalOperators;
import org.blendee.support.OnLeftColumn;
import org.blendee.support.OnLeftRelationship;
import org.blendee.support.OnRightColumn;
import org.blendee.support.OnRightRelationship;
import org.blendee.support.OneToManyQuery;
import org.blendee.support.InstantOneToManyQuery;
import org.blendee.support.OrderByOfferFunction;
import org.blendee.support.OrderByColumn;
import org.blendee.support.OrderByRelationship;
import org.blendee.support.SelectStatement;
import org.blendee.support.DataManipulationStatement;
import org.blendee.support.RightTable;
import org.blendee.support.Row;
import org.blendee.support.RowIterator;
import org.blendee.support.TableFacade;
import org.blendee.support.TableFacadeColumn;
import org.blendee.support.TableFacadeContext;
import org.blendee.support.CriteriaContext;
import org.blendee.support.SelectStatementBehavior;
import org.blendee.support.SelectStatementBehavior.PlaybackQuery;
import org.blendee.support.DataManipulationStatementBehavior;
import org.blendee.support.OnClause;
import org.blendee.support.TableFacadeRelationship;
import org.blendee.support.SelectOfferFunction;
import org.blendee.support.InsertOfferFunction;
import org.blendee.support.UpdateOfferFunction;
import org.blendee.support.SelectColumn;
import org.blendee.support.InsertColumn;
import org.blendee.support.SelectRelationship;
import org.blendee.support.WhereColumn;
import org.blendee.support.WhereRelationship;
import org.blendee.support.InsertRelationship;
import org.blendee.support.InsertStatementIntermediate;
import org.blendee.support.UpdateStatementIntermediate;
import org.blendee.support.DataManipulator;
import org.blendee.support.annotation.Table;
import org.blendee.support.annotation.Column;
/*--*/import org.blendee.support.annotation.PrimaryKey;/*--*/
/*--*/import org.blendee.support.annotation.ForeignKey;/*--*/

/**
 * 自動生成されたテーブル操作クラスです。
[[TABLE_COMMENT]]
 */
@Table(name = "[[TABLE]]", schema = "[[SCHEMA]]", type = "[[TYPE]]", remarks = "[[REMARKS]]")/*++[[PRIMARY_KEY_PART]]++*//*==PrimaryKeyPart==*/@PrimaryKey(name = "[[PK]]", columns = { /*++[[PK_COLUMNS]]++*//*--*/""/*--*/ }/*++[[PSEUDO]]++*/)/*==PrimaryKeyPart==*/
public class /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/
	extends /*++[[PARENT]]++*//*--*/Object/*--*/
	implements
		TableFacade<Row>,
		SelectStatement,
		Query</*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/.Iterator, /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/.Row>,
		RightTable</*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/.OnRightRel> {

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
	@Column(
		name = "[[COLUMN]]",
		type = /*++[[DB_TYPE]]++*//*--*/0/*--*/,
		typeName = "[[TYPE_NAME]]",
		size = /*++[[SIZE]]++*//*--*/0/*--*/,
		hasDecimalDigits = /*++[[HAS_DECIMAL_DIGITS]]++*//*--*/false/*--*/,
		decimalDigits = /*++[[DECIMAL_DIGITS]]++*//*--*/0/*--*/,
		remarks = "[[REMARKS]]",
		defaultValue = "[[DEFAULT]]",
		ordinalPosition = /*++[[ORDINAL_POSITION]]++*//*--*/0/*--*/,
		notNull = /*++[[NOT_NULL]]++*//*--*/true/*--*/)
	public static final String /*++[[COLUMN]]++*//*--*/columnName/*--*/ = "[[COLUMN]]";
/*==ColumnNamesPart==*/

/*++[[FOREIGN_KEYS_PART]]++*/
/*==ForeignKeysPart==*/
	/**
	 * name: [[FK]]<br>
	 * reference: [[REFERENCE]]<br>
	 * columns: [[FK_COLUMNS]]
	 */
	@ForeignKey(name = "[[FK]]", references = "[[REFERENCE]]", columns = { /*++[[ANNOTATION_FK_COLUMNS]]++*//*--*/""/*--*/ }, refColumns = { /*++[[REF_COLUMNS]]++*//*--*/""/*--*/ }/*++[[PSEUDO]]++*/)
	public static final String /*++[[REFERENCE]]$[[FK]]++*//*--*/FK/*--*/ = "[[FK]]";
/*==ForeignKeysPart==*/

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
		 * 項目名 [[FK_COLUMNS]]
		 * @return 参照しているレコードの Row
		 */
		public /*++[[REFERENCE_PACKAGE]].[[REFERENCE]].++*/Row /*++[[METHOD]]++*//*--*/getRelationship/*--*/() {
			return /*++[[REFERENCE_PACKAGE]].[[REFERENCE]].++*/row(
				data$.getDataObject(/*++[[REFERENCE]]$[[FK]]++*//*--*/FK/*--*/));
		}

/*==RowRelationshipPart==*/
	}

	private static final TableFacadeContext<SelectCol> selectContext$ = (relationship, name) -> new SelectCol(relationship, name);

	private static final TableFacadeContext<GroupByCol> groupByContext$ = (relationship, name) -> new GroupByCol(relationship, name);

	private static final TableFacadeContext<OrderByCol> orderByContext$ = (relationship, name) -> new OrderByCol(relationship, name);

	private static final TableFacadeContext<InsertCol> insertContext$ = (relationship, name) -> new InsertCol(relationship, name);

	private static final TableFacadeContext<WhereColumn<WhereLogicalOperators>> whereContext$ =  TableFacadeContext.newWhereBuilder();

	private static final TableFacadeContext<HavingColumn<HavingLogicalOperators>> havingContext$ =  TableFacadeContext.newHavingBuilder();

	private static final TableFacadeContext<OnLeftColumn<OnLeftLogicalOperators>> onLeftContext$ =  TableFacadeContext.newOnLeftBuilder();

	private static final TableFacadeContext<OnRightColumn<OnRightLogicalOperators>> onRightContext$ =  TableFacadeContext.newOnRightBuilder();

	/**
	 * WHERE 句 で使用する AND, OR です。
	 */
	public class WhereLogicalOperators implements LogicalOperators<WhereRel> {

		private WhereLogicalOperators() {}

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final WhereRel AND = new WhereRel(
				/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/.this,
				whereContext$,
				CriteriaContext.AND);

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final WhereRel OR = new WhereRel(
				/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/.this,
				whereContext$,
				CriteriaContext.OR);

		@Override
		public WhereRel defaultOperator() {
			return AND;
		}
	}

	/**
	 * HAVING 句 で使用する AND, OR です。
	 */
	public class HavingLogicalOperators implements LogicalOperators<HavingRel> {

		private HavingLogicalOperators() {}

		/**
		 * HAVING 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final HavingRel AND =
			new HavingRel(
				/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/.this,
				havingContext$,
				CriteriaContext.AND);

		/**
		 * HAVING 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final HavingRel OR = new HavingRel(
				/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/.this,
				havingContext$,
				CriteriaContext.OR);

		@Override
		public HavingRel defaultOperator() {
			return AND;
		}
	}

	/**
	 * ON 句 (LEFT) で使用する AND, OR です。
	 */
	public class OnLeftLogicalOperators implements LogicalOperators<OnLeftRel> {

		private OnLeftLogicalOperators() {}

		/**
		 * ON 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final OnLeftRel AND =
			new OnLeftRel(
				/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/.this,
				onLeftContext$,
				CriteriaContext.AND);

		/**
		 * ON 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final OnLeftRel OR =
			new OnLeftRel(
				/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/.this,
				onLeftContext$,
				CriteriaContext.OR);

		@Override
		public OnLeftRel defaultOperator() {
			return AND;
		}
	}


	/**
	 * ON 句 (RIGHT) で使用する AND, OR です。
	 */
	public class OnRightLogicalOperators implements LogicalOperators<OnRightRel> {

		private OnRightLogicalOperators() {}

		/**
		 * ON 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final OnRightRel AND =
			new OnRightRel(
				/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/.this,
				onRightContext$,
				CriteriaContext.AND);

		/**
		 * ON 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final OnRightRel OR =
			new OnRightRel(
				/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/.this,
				onRightContext$,
				CriteriaContext.OR);

		@Override
		public OnRightRel defaultOperator() {
			return AND;
		}
	}

	private OnRightLogicalOperators onRightOperators$;

	private SelectBehavior selectBehavior$;

	private SelectBehavior selectBehavior() {
		return selectBehavior$ == null ? (selectBehavior$ = new SelectBehavior()) : selectBehavior$;
	}

	private class SelectBehavior extends SelectStatementBehavior<SelectRel, GroupByRel, WhereRel, HavingRel, OrderByRel, OnLeftRel> {

		private SelectBehavior() {
			super($TABLE);
		}

		@Override
		protected SelectRel newSelect() {
			return new SelectRel(
					/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/.this,
					selectContext$);
		}

		@Override
		protected GroupByRel newGroupBy() {
			return new GroupByRel(
				/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/.this,
				groupByContext$);
		}

		@Override
		protected OrderByRel newOrderBy() {
			return new OrderByRel(
				/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/.this,
				orderByContext$);
		}

		@Override
		protected WhereLogicalOperators newWhereOperators() {
			return new WhereLogicalOperators();
		}

		@Override
		protected HavingLogicalOperators newHavingOperators() {
			return new HavingLogicalOperators();
		}

		@Override
		protected OnLeftLogicalOperators newOnLeftOperators() {
			return new OnLeftLogicalOperators();
		}
	}

	private DMSBehavior dmsBehavior$;

	private DMSBehavior dmsBehavior() {
		return dmsBehavior$ == null ? (dmsBehavior$ = new DMSBehavior()) : dmsBehavior$;
	}

	private class DMSBehavior extends DataManipulationStatementBehavior<InsertRel, WhereRel> {

		public DMSBehavior() {
			super($TABLE);
		}

		@Override
		protected InsertRel newInsert() {
			return new InsertRel(
				/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/.this,
				insertContext$);
		}

		@Override
		protected LogicalOperators<WhereRel> newWhereOperators() {
			return new WhereLogicalOperators();
		}
	}

	/**
	 * このクラスのインスタンスを生成します。<br>
	 * インスタンスは ID として、引数で渡された id を使用します。<br>
	 * フィールド定義の必要がなく、簡易に使用できますが、 ID は呼び出し側クラス内で一意である必要があります。
	 * @param id {@link SelectStatement} を使用するクラス内で一意の ID
	 * @return このクラスのインスタンス
	 */
	public static /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ of(String id) {
		if (id == null || id.equals(""))
			throw new IllegalArgumentException("id が空です");

		return new /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/(getUsing(new Throwable().getStackTrace()[1]), id);
	}


	/**
	 * 空のインスタンスを生成します。
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/() {}

	/**
	 * このクラスのインスタンスを生成します。<br>
	 * このコンストラクタで生成されたインスタンス の SELECT 句で使用されるカラムは、 パラメータの {@link Optimizer} に依存します。
	 * @param optimizer SELECT 句を決定する
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/(Optimizer optimizer) {
		selectBehavior().setOptimizer(Objects.requireNonNull(optimizer));
	}

	private /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/(Class<?> using, String id) {
		selectBehavior().setOptimizer(
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
	 * この {@link SelectStatement} のテーブルを表す {@link TableFacadeRelationship} を参照するためのインスタンスです。
	 * @return rel
	 */
	public ExtRel<TableFacadeColumn, Void> rel() {
		return new ExtRel<>(this, TableFacadeContext.OTHER, CriteriaContext.NULL);
	}

	/**
	 * SELECT 句を記述します。
	 * @param function
	 * @return この {@link SelectStatement}
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ SELECT(
		SelectOfferFunction<SelectRel> function) {
		selectBehavior().SELECT(function);
		return this;
	}

	/**
	 * DISTINCT を使用した SELECT 句を記述します。
	 * @param function
	 * @return この {@link SelectStatement}
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ SELECT_DISTINCT(
		SelectOfferFunction<SelectRel> function) {
		selectBehavior().SELECT_DISTINCT(function);
		return this;
	}

	/**
	 * COUNT(*) を使用した SELECT 句を記述します。
	 * @return この {@link SelectStatement}
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ SELECT_COUNT() {
		selectBehavior().SELECT_COUNT();
		return this;
	}

	/**
	 * GROUP BY 句を記述します。
	 * @param function
	 * @return この {@link SelectStatement}
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ GROUP_BY(
		GroupByOfferFunction<GroupByRel> function) {
		selectBehavior().GROUP_BY(function);
		return this;
	}

	/**
	 * WHERE 句を記述します。
	 * @param consumers
	 * @return この {@link SelectStatement}
	 */
	@SafeVarargs
	public final /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ WHERE(
		Consumer<WhereRel>... consumers) {
		selectBehavior().WHERE(consumers);
		return this;
	}

	/**
	 * WHERE 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 * @return {@link Criteria}
	 */
	public Criteria createWhereCriteria(
		Consumer<WhereRel> consumer) {
		return selectBehavior().createWhereCriteria(consumer);
	}

	/**
	 * HAVING 句を記述します。
	 * @param consumers
	 * @return この {@link SelectStatement}
	 */
	@SafeVarargs
	public final /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ HAVING(
		Consumer<HavingRel>... consumers) {
		selectBehavior().HAVING(consumers);
		return this;
	}

	/**
	 * HAVING 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 * @return {@link Criteria}
	 */
	public Criteria createHavingCriteria(
		Consumer<HavingRel> consumer) {
		return selectBehavior().createHavingCriteria(consumer);
	}

	/**
	 * このクエリに INNER JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return ON
	 */
	public <R extends OnRightRelationship> OnClause<OnLeftRel, R, /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/> INNER_JOIN(RightTable<R> right) {
		return selectBehavior().INNER_JOIN(right, this);
	}

	/**
	 * このクエリに LEFT OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return ON
	 */
	public <R extends OnRightRelationship> OnClause<OnLeftRel, R, /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/> LEFT_OUTER_JOIN(RightTable<R> right) {
		return selectBehavior().LEFT_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに RIGHT OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return ON
	 */
	public <R extends OnRightRelationship> OnClause<OnLeftRel, R, /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/> RIGHT_OUTER_JOIN(RightTable<R> right) {
		return selectBehavior().RIGHT_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに FULL OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return ON
	 */
	public <R extends OnRightRelationship> OnClause<OnLeftRel, R, /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/> FULL_OUTER_JOIN(RightTable<R> right) {
		return selectBehavior().FULL_OUTER_JOIN(right, this);
	}

	/**
	 * UNION するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param sql UNION 対象
	 * @return この {@link SelectStatement}
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ UNION(ComposedSQL sql) {
		selectBehavior().UNION(sql);
		return this;
	}

	/**
	 * UNION ALL するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param sql UNION ALL 対象
	 * @return この {@link SelectStatement}
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ UNION_ALL(ComposedSQL sql) {
		selectBehavior().UNION_ALL(sql);
		return this;
	}

	/**
	 * ORDER BY 句を記述します。
	 * @param function
	 * @return この {@link SelectStatement}
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ ORDER_BY(
		OrderByOfferFunction<OrderByRel> function) {
		selectBehavior().ORDER_BY(function);
		return this;
	}

	@Override
	public boolean hasWhereClause() {
		return selectBehavior().hasWhereClause();
	}

	/**
	 * 新規に GROUP BY 句をセットします。
	 * @param clause 新 ORDER BY 句
	 * @return {@link SelectStatement} 自身
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ groupBy(GroupByClause clause) {
		selectBehavior().setGroupByClause(clause);
		return this;
	}

	/**
	 * 新規に ORDER BY 句をセットします。
	 * @param clause 新 ORDER BY 句
	 * @return {@link SelectStatement} 自身
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ orderBy(OrderByClause clause) {
		selectBehavior().setOrderByClause(clause);
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たな条件を AND 結合します。<br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria AND 結合する新条件
	 * @return {@link SelectStatement} 自身
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ and(Criteria criteria) {
		selectBehavior().and(criteria);
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たな条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria OR 結合する新条件
	 * @return {@link SelectStatement} 自身
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ or(Criteria criteria) {
		selectBehavior().or(criteria);
		return this;
	}

	/**
	 * 生成された SQL 文を加工する {SQLDecorator} を設定します。
	 * @param decorators {@link SQLDecorator}
	 * @return {@link SelectStatement} 自身
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ apply(SQLDecorator... decorators) {
		selectBehavior().apply(decorators);
		return this;
	}

	@Override
	public Optimizer getOptimizer() {
		return selectBehavior().getOptimizer();
	}

	@Override
	public GroupByClause getGroupByClause() {
		return selectBehavior().getGroupByClause();
	}

	@Override
	public OrderByClause getOrderByClause() {
		return selectBehavior().getOrderByClause();
	}

	@Override
	public Criteria getWhereClause() {
		return selectBehavior().getWhereClause();
	}

	@Override
	public Relationship getRootRealtionship() {
		return ContextManager.get(RelationshipFactory.class).getInstance(getTablePath());
	}

	@Override
	public LogicalOperators<WhereRel> getWhereLogicalOperators() {
		return selectBehavior().whereOperators();
	}

	@Override
	public LogicalOperators<HavingRel> getHavingLogicalOperators() {
		return selectBehavior().havingOperators();
	}

	@Override
	public LogicalOperators<OnLeftRel> getOnLeftLogicalOperators() {
		return selectBehavior().onLeftOperators();
	}

	@Override
	public OnRightLogicalOperators getOnRightLogicalOperators() {
		return onRightOperators$ == null ? (onRightOperators$ = new OnRightLogicalOperators()) : onRightOperators$;
	}

	@Override
	public SQLDecorator[] decorators() {
		return selectBehavior().decorators();
	}

	@Override
	public Iterator execute() {
		selectBehavior().checkRowMode();
		return wrap(selectBehavior().query().execute());
	}

	@Override
	public Optional<Row> fetch(String... primaryKeyMembers) {
		selectBehavior().checkRowMode();
		return selectBehavior().query().fetch(primaryKeyMembers).map(o -> createRow(o));
	}

	@Override
	public Optional<Row> fetch(Number... primaryKeyMembers) {
		selectBehavior().checkRowMode();
		return selectBehavior().query().fetch(primaryKeyMembers).map(o -> createRow(o));
	}

	@Override
	public Optional<Row> fetch(Bindable... primaryKeyMembers) {
		selectBehavior().checkRowMode();
		return selectBehavior().query().fetch(primaryKeyMembers).map(o -> createRow(o));
	}

	@Override
	public int count() {
		selectBehavior().checkRowMode();
		return selectBehavior().query().count();
	}

	@Override
	public ComposedSQL toCountSQL() {
		selectBehavior().checkRowMode();
		return selectBehavior().query().toCountSQL();
	}

	@Override
	public void aggregate(Consumer<BResultSet> consumer) {
		selectBehavior().quitRowMode();
		org.blendee.support.Query.super.aggregate(consumer);
	}

	@Override
	public <T> T aggregateAndGet(Function<BResultSet, T> function) {
		selectBehavior().quitRowMode();
		return org.blendee.support.Query.super.aggregateAndGet(function);
	}

	@Override
	public ResultSetIterator aggregate() {
		selectBehavior().quitRowMode();
		return org.blendee.support.Query.super.aggregate();
	}

	@Override
	public String sql() {
		return selectBehavior().composeSQL().sql();
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		return selectBehavior().composeSQL().complement(done, statement);
	}

	@Override
	public Query reproduce(Object... placeHolderValues) {
		return new Query(selectBehavior().query().reproduce(placeHolderValues));
	}

	@Override
	public void joinTo(SQLQueryBuilder builder, JoinType joinType, Criteria onCriteria) {
		selectBehavior().joinTo(builder, joinType, onCriteria);
	}

	@Override
	public SQLQueryBuilder toSQLQueryBuilder() {
		return selectBehavior().buildBuilder();
	}

	@Override
	public void forSubquery(boolean forSubquery) {
		 selectBehavior().forSubquery(forSubquery);
	}

	/**
	 * 現在保持している WHERE 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ resetWhere() {
		selectBehavior().resetWhere();
		return this;
	}

	/**
	 * 現在保持している HAVING 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ resetHaving() {
		selectBehavior().resetHaving();
		return this;
	}

	/**
	 * 現在保持している SELECT 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ resetSelect() {
		selectBehavior().resetSelect();
		return this;
	}

	/**
	 * 現在保持している GROUP BY 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ resetGroupBy() {
		selectBehavior().resetGroupBy();
		return this;
	}

	/**
	 * 現在保持している ORDER BY 句をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ resetOrderBy() {
		selectBehavior().resetOrderBy();
		return this;
	}

	/**
	 * 現在保持している UNION をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ resetUnions() {
		selectBehavior().resetUnions();
		return this;
	}

	/**
	 * 現在保持している JOIN をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ resetJoins() {
		selectBehavior().resetJoins();
		return this;
	}

	/**
	 * 現在保持している {@link SQLDecorator} をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ resetDecorators() {
		selectBehavior().resetDecorators();
		return this;
	}

	/**
	 * 現在保持している条件、並び順をリセットします。
	 * @return このインスタンス
	 */
	public /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ reset() {
		selectBehavior().reset();
		return this;
	}

	@Override
	public void quitRowMode() {
		selectBehavior().quitRowMode();
	}

	@Override
	public boolean rowMode() {
		return selectBehavior().rowMode();
	}

	@Override
	public Query query() {
		return  new Query(selectBehavior().query());
	}

	@Override
	public OnRightRel joint() {
		return getOnRightLogicalOperators().AND;
	}

	/**
	 * INSERT 文を生成します。
	 * @param function function
	 * @return {@link InsertStatementIntermediate}
	 */
	public InsertStatementIntermediate INSERT(InsertOfferFunction<InsertRel> function) {
		return dmsBehavior().INSERT(function);
	}

	/**
	 * INSERT 文を生成します。
	 * @param function function
	 * @param select select
	 * @return {@link InsertStatementIntermediate}
	 */
	public DataManipulator INSERT(InsertOfferFunction<InsertRel> function, SelectStatement select) {
		return dmsBehavior().INSERT(function, select);
	}

	/**
	 * INSERT 文を生成します。
	 * @param select select
	 * @return {@link InsertStatementIntermediate}
	 */
	public DataManipulator INSERT(SelectStatement select) {
		return dmsBehavior().INSERT(select);
	}

	public UpdateStatementIntermediate UPDATE(UpdateOfferFunction<UpdateRel> function) {
		return new UpdateStatementIntermediate(null);
	}

	@SafeVarargs
	public final DataManipulator DELETE(Consumer<WhereRel>... consumers) {
		return dmsBehavior().DELETE(consumers);
	}

	@Override
	public String toString() {
		return selectBehavior().toString();
	}

	private static Class<?> getUsing(StackTraceElement element) {
		try {
			return Class.forName(element.getClassName());
		} catch (Exception e) {
			throw new IllegalStateException(e.toString());
		}
	}

	/**
	 * 自動生成された {@link TableFacadeRelationship} の実装クラスです。<br>
	 * 条件として使用できるカラムを内包しており、それらを使用して検索 SQL を生成可能にします。
	 * @param <T> 使用されるカラムのタイプにあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class Rel<T, M> implements TableFacadeRelationship {

		private final /*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ table$;

		private final CriteriaContext context$;

		private final TableFacadeRelationship parent$;

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
		public Rel(
			TableFacadeContext<T> builder$,
			TableFacadeRelationship parent$,
			String fkName$,
			TablePath path$) {
			table$ = null;
			context$ = null;
			this.parent$ = parent$;
			this.fkName$ = fkName$;
			this.path$ = path$;

/*++[[COLUMN_PART2]]++*/
/*==ColumnPart2==*/this./*++[[COLUMN]]++*//*--*/columnName/*--*/ = builder$.buildColumn(
				this, /*++[[PACKAGE]].[[TABLE]].[[COLUMN]]++*//*--*/TableFacadeTemplate.columnName/*--*/);
/*==ColumnPart2==*/
		}

		private Rel(
			/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ table$,
			TableFacadeContext<T> builder$,
			CriteriaContext context$) {
			this.table$ = table$;
			this.context$ = context$;
			parent$ = null;
			fkName$ = null;
			path$ = $TABLE;

			/*--*/columnName = null;/*--*/
/*++[[COLUMN_PART2]]++*/
		}

		@Override
		public CriteriaContext getContext() {
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
		public TableFacadeRelationship getParent() {
			return parent$;
		}

		@Override
		public TablePath getTablePath() {
			return path$;
		}

		@Override
		public SelectStatement getSelectStatement() {
			if (table$ != null) return table$;
			return parent$.getSelectStatement();
		}

		@Override
		public DataManipulationStatement getDataManipulationStatement() {
			if (table$ != null) return table$.dmsBehavior$;
			return parent$.getDataManipulationStatement();
		}

		@Override
		public Row createRow(DataObject data) {
			return new Row(data);
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof TableFacadeRelationship)) return false;
			return getRelationship()
				.equals(((TableFacadeRelationship) o).getRelationship());
		}

		@Override
		public int hashCode() {
			return getRelationship().hashCode();
		}
	}

	/**
	 * 自動生成された {@link TableFacadeRelationship} の実装クラスです。<br>
	 * 条件として使用できるカラムと、参照しているテーブルを内包しており、それらを使用して検索 SQL を生成可能にします。
	 * @param <T> 使用されるカラムのタイプにあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class ExtRel<T, M> extends Rel<T, M> {

		/*--?--*/private final TableFacadeContext<T> builder$;/*--?--*/

		/*--?--*/private final TablePath root$;/*--?--*/

		/**
		 * 直接使用しないでください。
		 * @param builder$ builder
		 * @param parent$ parent
		 * @param fkName$ fkName
		 * @param path$ path
		 * @param root$ root
		 */
		public ExtRel(
			TableFacadeContext<T> builder$,
			TableFacadeRelationship parent$,
			String fkName$,
			TablePath path$,
			TablePath root$) {
			super(builder$, parent$, fkName$, path$);
			/*--?--*/this.builder$ = builder$;/*--?--*/
			/*--?--*/this.root$ = root$;/*--?--*/
		}

		private ExtRel(
			/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ table$,
			TableFacadeContext<T> builder$,
			CriteriaContext context$) {
			super(table$, builder$, context$);
			/*--?--*/this.builder$ = builder$;/*--?--*/
			/*--?--*/root$ = null;/*--?--*/
		}

		/**
		 * この {@link TableFacadeRelationship} が表すテーブルの Row を一とし、多をもつ検索結果を生成する {@link OneToManyQuery} を返します。
		 * @return {@link OneToManyQuery}
		 */
		public OneToManyQuery<Row, M> intercept() {
			if (super.table$ != null) throw new IllegalStateException(super.path$.getSchemaName() + " から直接使用することはできません");
			if (!getSelectStatement().rowMode()) throw new IllegalStateException("集計モードでは実行できない処理です");
			return new InstantOneToManyQuery<>(this, getSelectStatement().decorators());
		}

/*++[[TABLE_RELATIONSHIP_PART]]++*/
/*==TableRelationshipPart==*/
		/**
		 * 参照先テーブル名 [[REFERENCE]]<br>
		 * 外部キー名 [[FK]]<br>
		 * 項目名 [[FK_COLUMNS]]
		 * @return [[REFERENCE]] relationship
		 */
		public /*++[[REFERENCE_PACKAGE]].[[REFERENCE]].++*/ExtRel<T, /*++[[MANY]]++*//*--*/Object/*--*/> /*--*/relationshipName/*--*//*++[[RELATIONSHIP]]++*/() {
			return new /*++[[REFERENCE_PACKAGE]].[[REFERENCE]].++*/ExtRel<>(
				builder$,
				this,
				/*++[[PACKAGE]].[[TABLE]].[[REFERENCE]]$[[FK]]++*//*--*/FK/*--*/,
				/*++[[REFERENCE_PACKAGE]].[[REFERENCE]].++*/$TABLE,
				root$ != null ? root$ : super.path$);
		}
/*==TableRelationshipPart==*/
	}

	/**
	 * SELECT 句用
	 */
	public static class SelectRel extends ExtRel<SelectCol, Void> implements SelectRelationship {

		private SelectRel(
			/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ table$,
			TableFacadeContext<SelectCol> builder$) {
			super(table$, builder$, CriteriaContext.NULL);
		}
	}

	/**
	 * WHERE 句用
	 */
	public static class WhereRel extends ExtRel<WhereColumn<WhereLogicalOperators>, Void> implements WhereRelationship {

		private WhereRel(
			/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ table$,
			TableFacadeContext<WhereColumn<WhereLogicalOperators>> builder$,
			CriteriaContext context$) {
			super(table$, builder$, context$);
		}
	}

	/**
	 * GROUB BY 句用
	 */
	public static class GroupByRel extends ExtRel<GroupByCol, Void> implements GroupByRelationship {

		private GroupByRel(
			/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ table$,
			TableFacadeContext<GroupByCol> builder$) {
			super(table$, builder$, CriteriaContext.NULL);
		}
	}

	/**
	 * HAVING 句用
	 */
	public static class HavingRel extends ExtRel<HavingColumn<HavingLogicalOperators>, Void> implements HavingRelationship {

		private HavingRel(
			/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ table$,
			TableFacadeContext<HavingColumn<HavingLogicalOperators>> builder$,
			CriteriaContext context$) {
			super(table$, builder$, context$);
		}
	}

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByRel extends ExtRel<OrderByCol, Void> implements OrderByRelationship {

		private OrderByRel(
			/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ table$,
			TableFacadeContext<OrderByCol> builder$) {
			super(table$, builder$, CriteriaContext.NULL);
		}

		@Override
		public OrderByClause getOrderByClause() {
			return getSelectStatement().getOrderByClause();
		}
	}

	/**
	 * ON 句 (LEFT) 用
	 */
	public static class OnLeftRel extends ExtRel<OnLeftColumn<OnLeftLogicalOperators>, Void> implements OnLeftRelationship {

		private OnLeftRel(
			/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ table$,
			TableFacadeContext<OnLeftColumn<OnLeftLogicalOperators>> builder$,
			CriteriaContext context$) {
			super(table$, builder$, context$);
		}
	}

	/**
	 * ON 句 (RIGHT) 用
	 */
	public static class OnRightRel extends Rel<OnRightColumn<OnRightLogicalOperators>, Void> implements OnRightRelationship {

		private OnRightRel(
			/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ table$,
			TableFacadeContext<OnRightColumn<OnRightLogicalOperators>> builder$,
			CriteriaContext context$) {
			super(table$, builder$, context$);
		}
	}

	/**
	 * INSERT 用
	 */
	public static class InsertRel extends Rel<InsertCol, Void> implements InsertRelationship {

		private InsertRel(
			/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ table$,
			TableFacadeContext<InsertCol> builder$) {
			super(table$, builder$, CriteriaContext.NULL);
		}
	}

	/**
	 * UPDATE 用
	 */
	public static class UpdateRel extends Rel<TableFacadeColumn, Void> {

		private UpdateRel(/*++[[TABLE]]++*//*--*/TableFacadeTemplate/*--*/ table$) {
			super(table$, TableFacadeContext.OTHER, CriteriaContext.NULL);
		}
	}

	/**
	 * SELECT 句用
	 */
	public static class SelectCol extends SelectColumn {

		private SelectCol(TableFacadeRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * GROUP BY 句用
	 */
	public static class GroupByCol extends GroupByColumn {

		private GroupByCol(TableFacadeRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByCol extends OrderByColumn {

		private OrderByCol(TableFacadeRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * INSERT 文用
	 */
	public static class InsertCol extends InsertColumn {

		private InsertCol(TableFacadeRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * Query
	 */
	public class Query implements org.blendee.support.Query<Iterator, Row> {

		private final PlaybackQuery inner;

		private Query(PlaybackQuery inner) {
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
		public Query reproduce(Object... placeHolderValues) {
			return new Query(inner.reproduce(placeHolderValues));
		}
	}
}
