package org.blendee.support;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataObject;
import org.blendee.selector.Optimizer;
import org.blendee.sql.Bindable;
import org.blendee.sql.Criteria;
import org.blendee.sql.FromClause.JoinType;
import org.blendee.sql.GroupByClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.PseudoColumn;
import org.blendee.sql.QueryBuilder;
import org.blendee.sql.Relationship;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.SelectClause;
import org.blendee.support.QueryHelper.PlaybackExecutor;

/**
 * 自動生成された {@link Query} の実装クラスです。
 * パッケージ名 blenexam.orm
 * テーブル名 a_asset
 */
public class AnonymousQuery extends java.lang.Object
	implements Query, Executor<Iterator<Void>, Void>, RightQuery<AnonymousQuery.OnRightQRel> {

	private static final QueryContext<SelectQCol> selectContext = (
		relationship,
		name) -> new SelectQCol(relationship, name);

	private static final QueryContext<GroupByQCol> groupByContext = (
		relationship,
		name) -> new GroupByQCol(relationship, name);

	private static final QueryContext<OrderByQCol> orderByContext = (
		relationship,
		name) -> new OrderByQCol(relationship, name);

	private static final QueryContext<WhereQueryColumn<WhereLogicalOperators>> whereContext = QueryContext
		.newWhereBuilder();

	private static final QueryContext<HavingQueryColumn<HavingLogicalOperators>> havingContext = QueryContext
		.newHavingBuilder();

	private static final QueryContext<OnLeftQueryColumn<OnLeftLogicalOperators>> onLeftContext = QueryContext
		.newOnLeftBuilder();

	private static final QueryContext<OnRightQueryColumn<OnRightLogicalOperators>> onRightContext = QueryContext
		.newOnRightBuilder();

	/**
	 * WHERE 句 で使用する AND, OR です。
	 */
	public class WhereLogicalOperators implements LogicalOperators<WhereQRel> {

		private WhereLogicalOperators() {}

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final WhereQRel AND = new WhereQRel(AnonymousQuery.this, whereContext, QueryCriteriaContext.AND);

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final WhereQRel OR = new WhereQRel(AnonymousQuery.this, whereContext, QueryCriteriaContext.OR);

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
		public final HavingQRel AND = new HavingQRel(AnonymousQuery.this, havingContext, QueryCriteriaContext.AND);

		/**
		 * HAVING 句に OR 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final HavingQRel OR = new HavingQRel(AnonymousQuery.this, havingContext, QueryCriteriaContext.OR);

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
		public final OnLeftQRel AND = new OnLeftQRel(AnonymousQuery.this, onLeftContext, QueryCriteriaContext.AND);

		/**
		 * ON 句に OR 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final OnLeftQRel OR = new OnLeftQRel(AnonymousQuery.this, onLeftContext, QueryCriteriaContext.OR);

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
		public final OnRightQRel AND = new OnRightQRel(AnonymousQuery.this, onRightContext, QueryCriteriaContext.AND);

		/**
		 * ON 句に OR 結合する条件用のカラムを選択するための {@link QueryRelationship} です。
		 */
		public final OnRightQRel OR = new OnRightQRel(AnonymousQuery.this, onRightContext, QueryCriteriaContext.OR);

		@Override
		public OnRightQRel defaultOperator() {
			return AND;
		}
	}

	private final WhereLogicalOperators whereOperators = new WhereLogicalOperators();

	private final HavingLogicalOperators havingOperators = new HavingLogicalOperators();

	private final OnLeftLogicalOperators onLeftOperators = new OnLeftLogicalOperators();

	private final OnRightLogicalOperators onRightOperators = new OnRightLogicalOperators();

	/**
	 * SELECT 句用のカラムを選択するための {@link QueryRelationship} です。
	 */
	private final SelectQRel select = new SelectQRel(this, selectContext, QueryCriteriaContext.NULL);

	/**
	 * GROUP BY 句用のカラムを選択するための {@link QueryRelationship} です。
	 */
	private final GroupByQRel groupBy = new GroupByQRel(this, groupByContext, QueryCriteriaContext.NULL);

	/**
	 * ORDER BY 句用のカラムを選択するための {@link QueryRelationship} です。
	 */
	private final OrderByQRel orderBy = new OrderByQRel(this, orderByContext, QueryCriteriaContext.NULL);

	private final QueryHelper<SelectQRel, GroupByQRel, WhereQRel, HavingQRel, OrderByQRel, OnLeftQRel> helper;

	private final AnonymousRelationship relationship;

	/**
	 * (innerQuery) alias
	 * @param innerQuery {@link Query}
	 * @param alias 表別名
	 */
	public AnonymousQuery(Query innerQuery, String alias) {
		relationship = new AnonymousRelationship(innerQuery.executor(), alias);

		helper = new QueryHelper<>(
			new AnonymousFromClause(relationship),
			select,
			groupBy,
			orderBy,
			whereOperators,
			havingOperators,
			onLeftOperators);
	}

	/**
	 * SELECT 句を記述します。
	 * @param function
	 * @return この {@link Query}
	 */
	public AnonymousQuery SELECT(SelectOfferFunction<SelectQRel> function) {
		helper.SELECT(function);
		return this;
	}

	/**
	 * DISTINCT を使用した SELECT 句を記述します。
	 * @param function
	 * @return この {@link Query}
	 */
	public AnonymousQuery SELECT_DISTINCT(SelectOfferFunction<SelectQRel> function) {
		helper.SELECT_DISTINCT(function);
		return this;
	}

	/**
	 * COUNT(*) を使用した SELECT 句を記述します。
	 * @return この {@link Query}
	 */
	public AnonymousQuery SELECT_COUNT() {
		helper.SELECT_COUNT();
		return this;
	}

	/**
	 * GROUP BY 句を記述します。
	 * @param function
	 * @return この {@link Query}
	 */
	public AnonymousQuery GROUP_BY(GroupByOfferFunction<GroupByQRel> function) {
		helper.GROUP_BY(function);
		return this;
	}

	/**
	 * WHERE 句を記述します。
	 * @param consumers
	 * @return この {@link Query}
	 */
	@SafeVarargs
	public final AnonymousQuery WHERE(Consumer<WhereQRel>... consumers) {
		helper.WHERE(consumers);
		return this;
	}

	/**
	 * WHERE 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 * @return {@link Criteria}
	 */
	public Criteria createWhereCriteria(Consumer<WhereQRel> consumer) {
		return helper.createWhereCriteria(consumer);
	}

	/**
	 * HAVING 句を記述します。
	 * @param consumers
	 * @return この {@link Query}
	 */
	@SafeVarargs
	public final AnonymousQuery HAVING(Consumer<HavingQRel>... consumers) {
		helper.HAVING(consumers);
		return this;
	}

	/**
	 * HAVING 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 * @return {@link Criteria}
	 */
	public Criteria createHavingCriteria(Consumer<HavingQRel> consumer) {
		return helper.createHavingCriteria(consumer);
	}

	/**
	 * このクエリに INNER JOIN で別テーブルを結合します。
	 * @param right 別クエリの {@link OnRightQueryRelationship}
	 * @return ON
	 */
	public <R extends OnRightQueryRelationship> QueryOnClause<OnLeftQRel, R, AnonymousQuery> INNER_JOIN(
		RightQuery<R> right) {
		return helper.INNER_JOIN(right, this);
	}

	/**
	 * このクエリに LEFT OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリの {@link OnRightQueryRelationship}
	 * @return ON
	 */
	public <R extends OnRightQueryRelationship> QueryOnClause<OnLeftQRel, R, AnonymousQuery> LEFT_OUTER_JOIN(
		RightQuery<R> right) {
		return helper.LEFT_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに RIGHT OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリの {@link OnRightQueryRelationship}
	 * @return ON
	 */
	public <R extends OnRightQueryRelationship> QueryOnClause<OnLeftQRel, R, AnonymousQuery> RIGHT_OUTER_JOIN(
		RightQuery<R> right) {
		return helper.RIGHT_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに FULL OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリの {@link OnRightQueryRelationship}
	 * @return ON
	 */
	public <R extends OnRightQueryRelationship> QueryOnClause<OnLeftQRel, R, AnonymousQuery> FULL_OUTER_JOIN(
		RightQuery<R> right) {
		return helper.FULL_OUTER_JOIN(right, this);
	}

	/**
	 * UNION するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param query UNION 対象
	 * @return この {@link Query}
	 */
	public AnonymousQuery UNION(ComposedSQL query) {
		helper.UNION(query);
		return this;
	}

	/**
	 * UNION ALL するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param query UNION ALL 対象
	 * @return この {@link Query}
	 */
	public AnonymousQuery UNION_ALL(ComposedSQL query) {
		helper.UNION_ALL(query);
		return this;
	}

	/**
	 * ORDER BY 句を記述します。
	 * @param function
	 * @return この {@link Query}
	 */
	public AnonymousQuery ORDER_BY(OrderByOfferFunction<OrderByQRel> function) {
		helper.ORDER_BY(function);
		return this;
	}

	@Override
	public boolean hasWhereClause() {
		return helper.hasWhereClause();
	}

	/**
	 * 新規に GROUP BY 句をセットします。
	 * @param clause 新 ORDER BY 句
	 * @return {@link Query} 自身
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public AnonymousQuery groupBy(GroupByClause clause) {
		helper.setGroupByClause(clause);
		return this;
	}

	/**
	 * 新規に ORDER BY 句をセットします。
	 * @param clause 新 ORDER BY 句
	 * @return {@link Query} 自身
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public AnonymousQuery orderBy(OrderByClause clause) {
		helper.setOrderByClause(clause);
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たな条件を AND 結合します。<br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria AND 結合する新条件
	 * @return {@link Query} 自身
	 */
	public AnonymousQuery and(Criteria criteria) {
		helper.and(criteria);
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たな条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria OR 結合する新条件
	 * @return {@link Query} 自身
	 */
	public AnonymousQuery or(Criteria criteria) {
		helper.or(criteria);
		return this;
	}

	/**
	 * 生成された SQL 文を加工する {SQLDecorator} を設定します。
	 * @param decorators {@link SQLDecorator}
	 * @return {@link Query} 自身
	 */
	public AnonymousQuery apply(SQLDecorator... decorators) {
		helper.apply(decorators);
		return this;
	}

	@Override
	public Relationship getRootRealtionship() {
		return relationship;
	}

	@Override
	public LogicalOperators<?> getWhereLogicalOperators() {
		return whereOperators;
	}

	@Override
	public LogicalOperators<?> getHavingLogicalOperators() {
		return havingOperators;
	}

	@Override
	public LogicalOperators<?> getOnLeftLogicalOperators() {
		return onLeftOperators;
	}

	@Override
	public LogicalOperators<?> getOnRightLogicalOperators() {
		return onRightOperators;
	}

	@Override
	public SQLDecorator[] decorators() {
		return helper.decorators();
	}

	@Override
	public Iterator<Void> execute() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Void> fetch(String... primaryKeyMembers) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Void> fetch(Number... primaryKeyMembers) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Void> fetch(Bindable... primaryKeyMembers) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int count() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ComposedSQL toCountSQL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String sql() {
		careEmptySelect();
		return helper.composeSQL().sql();
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		careEmptySelect();
		return helper.composeSQL().complement(done, statement);
	}

	@Override
	public AnonymousExecutor reproduce(Object... placeHolderValues) {
		careEmptySelect();
		return new AnonymousExecutor(helper.executor().reproduce(placeHolderValues));
	}

	@Override
	public void joinTo(QueryBuilder builder, JoinType joinType, Criteria onCriteria) {
		careEmptySelect();//下でこのQueryが評価されてしまうのでSELECT句を補う
		helper.joinTo(builder, joinType, onCriteria);
	}

	@Override
	public QueryBuilder toQueryBuilder() {
		careEmptySelect();
		return helper.buildBuilder();
	}

	@Override
	public void forSubquery(boolean forSubquery) {
		helper.forSubquery(forSubquery);
	}

	/**
	 * 現在保持している WHERE 句をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousQuery resetWhere() {
		helper.resetWhere();
		return this;
	}

	/**
	 * 現在保持している HAVING 句をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousQuery resetHaving() {
		helper.resetHaving();
		return this;
	}

	/**
	 * 現在保持している SELECT 句をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousQuery resetSelect() {
		helper.resetSelect();
		return this;
	}

	/**
	 * 現在保持している GROUP BY 句をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousQuery resetGroupBy() {
		helper.resetGroupBy();
		return this;
	}

	/**
	 * 現在保持している ORDER BY 句をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousQuery resetOrderBy() {
		helper.resetOrderBy();
		return this;
	}

	/**
	 * 現在保持している UNION をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousQuery resetUnions() {
		helper.resetUnions();
		return this;
	}

	/**
	 * 現在保持している JOIN をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousQuery resetJoins() {
		helper.resetJoins();
		return this;
	}

	/**
	 * 現在保持している {@link SQLDecorator} をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousQuery resetDecorators() {
		helper.resetDecorators();
		return this;
	}

	@Override
	public void quitRowMode() {
		helper.quitRowMode();
	}

	@Override
	public boolean rowMode() {
		return helper.rowMode();
	}

	@Override
	public AnonymousExecutor executor() {
		careEmptySelect();
		return new AnonymousExecutor(helper.executor());
	}

	@Override
	public OnRightQRel joint() {
		return onRightOperators.AND;
	}

	@Override
	public String toString() {
		careEmptySelect();
		return helper.toString();
	}

	private void careEmptySelect() {
		SelectClause select = helper.getSelectClause();
		if (select == null || select.getColumnsSize() == 0) {
			select = new SelectClause();
			select.add("{0}", new PseudoColumn(relationship, "*", true));
			helper.setSelectClause(select);
		}
	}

	/**
	 * {@link QueryRelationship} の実装クラスです。<br>
	 * 条件として使用できるカラムを内包しており、それらを使用して検索 SQL を生成可能にします。
	 * @param <T> 使用されるカラムのタイプにあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class QRel<T, M> implements QueryRelationship {

		private final AnonymousQuery query$;

		private final QueryCriteriaContext context$;

		private final QueryContext<T> builder$;

		private QRel(AnonymousQuery query$, QueryContext<T> builder$, QueryCriteriaContext context$) {
			this.query$ = query$;
			this.context$ = context$;
			this.builder$ = builder$;
		}

		@Override
		public QueryCriteriaContext getContext() {
			return context$;
		}

		@Override
		public Relationship getRelationship() {
			return query$.relationship;
		}

		@Override
		public Optimizer getOptimizer() {
			throw new UnsupportedOperationException();
		}

		@Override
		public GroupByClause getGroupByClause() {
			return query$.helper.getGroupByClause();
		}

		@Override
		public OrderByClause getOrderByClause() {
			return query$.helper.getOrderByClause();
		}

		@Override
		public Criteria getWhereClause() {
			return query$.helper.getWhereClause();
		}

		@Override
		public QueryRelationship getParent() {
			throw new UnsupportedOperationException();
		}

		@Override
		public TablePath getTablePath() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Query getRoot() {
			return query$;
		}

		@Override
		public Row createRow(DataObject data) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @param name カラム名
		 * @return 使用されるカラムのタイプにあった型
		 */
		public T col(String name) {
			return builder$.buildQueryColumn(this, name);
		}
	}

	/**
	 * SELECT 句用
	 */
	public static class SelectQRel extends QRel<SelectQCol, Void> implements SelectQueryRelationship {

		private SelectQRel(AnonymousQuery query$, QueryContext<SelectQCol> builder$, QueryCriteriaContext context$) {
			super(query$, builder$, context$);
		}
	}

	/**
	 * WHERE 句用
	 */
	public static class WhereQRel extends QRel<WhereQueryColumn<WhereLogicalOperators>, Void>
		implements WhereQueryRelationship {

		private WhereQRel(
			AnonymousQuery query$,
			QueryContext<WhereQueryColumn<WhereLogicalOperators>> builder$,
			QueryCriteriaContext context$) {
			super(query$, builder$, context$);
		}
	}

	/**
	 * GROUB BY 句用
	 */
	public static class GroupByQRel extends QRel<GroupByQCol, Void> implements GroupByQueryRelationship {

		private GroupByQRel(AnonymousQuery query$, QueryContext<GroupByQCol> builder$, QueryCriteriaContext context$) {
			super(query$, builder$, context$);
		}
	}

	/**
	 * HAVING 句用
	 */
	public static class HavingQRel extends QRel<HavingQueryColumn<HavingLogicalOperators>, Void>
		implements HavingQueryRelationship {

		private HavingQRel(
			AnonymousQuery query$,
			QueryContext<HavingQueryColumn<HavingLogicalOperators>> builder$,
			QueryCriteriaContext context$) {
			super(query$, builder$, context$);
		}
	}

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByQRel extends QRel<OrderByQCol, Void> implements OrderByQueryRelationship {

		private OrderByQRel(AnonymousQuery query$, QueryContext<OrderByQCol> builder$, QueryCriteriaContext context$) {
			super(query$, builder$, context$);
		}
	}

	/**
	 * ON 句 (LEFT) 用
	 */
	public static class OnLeftQRel extends QRel<OnLeftQueryColumn<OnLeftLogicalOperators>, Void>
		implements OnLeftQueryRelationship {

		private OnLeftQRel(
			AnonymousQuery query$,
			QueryContext<OnLeftQueryColumn<OnLeftLogicalOperators>> builder$,
			QueryCriteriaContext context$) {
			super(query$, builder$, context$);
		}
	}

	/**
	 * ON 句 (RIGHT) 用
	 */
	public static class OnRightQRel extends QRel<OnRightQueryColumn<OnRightLogicalOperators>, Void>
		implements OnRightQueryRelationship {

		private OnRightQRel(
			AnonymousQuery query$,
			QueryContext<OnRightQueryColumn<OnRightLogicalOperators>> builder$,
			QueryCriteriaContext context$) {
			super(query$, builder$, context$);
		}
	}

	/**
	 * SELECT 句用
	 */
	public static class SelectQCol extends SelectQueryColumn {

		private SelectQCol(QueryRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * GROUP BY 句用
	 */
	public static class GroupByQCol extends GroupByQueryColumn {

		private GroupByQCol(QueryRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByQCol extends OrderByQueryColumn {

		private OrderByQCol(QueryRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * Executor
	 */
	public class AnonymousExecutor implements org.blendee.support.Executor<Iterator<Void>, Void> {

		private final PlaybackExecutor inner;

		private AnonymousExecutor(PlaybackExecutor inner) {
			this.inner = inner;
		}

		@Override
		public Iterator<Void> execute() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Optional<Void> fetch(Bindable... primaryKeyMembers) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int count() {
			throw new UnsupportedOperationException();
		}

		@Override
		public ComposedSQL toCountSQL() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean rowMode() {
			return false;
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
		public AnonymousExecutor reproduce(Object... placeHolderValues) {
			return new AnonymousExecutor(inner.reproduce(placeHolderValues));
		}
	}
}
