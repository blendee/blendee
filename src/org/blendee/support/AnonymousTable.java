package org.blendee.support;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.blendee.jdbc.AutoCloseableIterator;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.selector.Optimizer;
import org.blendee.sql.Bindable;
import org.blendee.sql.Binder;
import org.blendee.sql.Criteria;
import org.blendee.sql.FromClause.JoinType;
import org.blendee.sql.GroupByClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.PseudoColumn;
import org.blendee.sql.Relationship;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.RuntimeIdFactory;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.SQLQueryBuilder;
import org.blendee.sql.SelectClause;
import org.blendee.support.SelectStatementBehavior.PlaybackQuery;

/**
 * 無名テーブルクラスです。
 * @author 千葉 哲嗣
 */
public class AnonymousTable implements SelectStatement, Query<AutoCloseableIterator<Void>, Void>, RightTable<AnonymousTable.OnRightRel> {

	private static final TableFacadeContext<SelectCol> selectContext = (
		relationship,
		name) -> new SelectCol(relationship, name);

	private static final TableFacadeContext<GroupByCol> groupByContext = (
		relationship,
		name) -> new GroupByCol(relationship, name);

	private static final TableFacadeContext<OrderByCol> orderByContext = (
		relationship,
		name) -> new OrderByCol(relationship, name);

	private static final TableFacadeContext<WhereColumn<WhereLogicalOperators>> whereContext = TableFacadeContext
		.newWhereBuilder();

	private static final TableFacadeContext<HavingColumn<HavingLogicalOperators>> havingContext = TableFacadeContext
		.newHavingBuilder();

	private static final TableFacadeContext<OnLeftColumn<OnLeftLogicalOperators>> onLeftContext = TableFacadeContext
		.newOnLeftBuilder();

	private static final TableFacadeContext<OnRightColumn<OnRightLogicalOperators>> onRightContext = TableFacadeContext
		.newOnRightBuilder();

	private final RuntimeId id = RuntimeIdFactory.getInstance();

	/**
	 * WHERE 句 で使用する AND, OR です。
	 */
	public class WhereLogicalOperators implements LogicalOperators<WhereRel> {

		private WhereLogicalOperators() {}

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final WhereRel OR = new WhereRel(AnonymousTable.this, whereContext, CriteriaContext.OR, null);

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final WhereRel AND = new WhereRel(AnonymousTable.this, whereContext, CriteriaContext.AND, OR);

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
		 * HAVING 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final HavingRel OR = new HavingRel(AnonymousTable.this, havingContext, CriteriaContext.OR, null);

		/**
		 * HAVING 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final HavingRel AND = new HavingRel(AnonymousTable.this, havingContext, CriteriaContext.AND, OR);

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
		 * ON 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final OnLeftRel OR = new OnLeftRel(AnonymousTable.this, onLeftContext, CriteriaContext.OR, null);

		/**
		 * ON 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final OnLeftRel AND = new OnLeftRel(AnonymousTable.this, onLeftContext, CriteriaContext.AND, OR);

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
		 * ON 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final OnRightRel OR = new OnRightRel(AnonymousTable.this, onRightContext, CriteriaContext.OR, null);

		/**
		 * ON 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final OnRightRel AND = new OnRightRel(AnonymousTable.this, onRightContext, CriteriaContext.AND, OR);

		@Override
		public OnRightRel defaultOperator() {
			return AND;
		}
	}

	private final List<SQLDecorator> decorators = new LinkedList<SQLDecorator>();

	private OnRightLogicalOperators onRightOperators;

	private Behavior behavior;

	private Behavior behavior() {
		return behavior == null ? (behavior = new Behavior()) : behavior;
	}

	private class Behavior extends SelectStatementBehavior<SelectRel, GroupByRel, WhereRel, HavingRel, OrderByRel, OnLeftRel> {

		private Behavior() {
			super(new AnonymousFromClause(relationship, id), AnonymousTable.this);
		}

		@Override
		protected SelectRel newSelect() {
			return new SelectRel(AnonymousTable.this, selectContext, CriteriaContext.NULL);
		}

		@Override
		protected GroupByRel newGroupBy() {
			return new GroupByRel(AnonymousTable.this, groupByContext, CriteriaContext.NULL);
		}

		@Override
		protected OrderByRel newOrderBy() {
			return new OrderByRel(AnonymousTable.this, orderByContext, CriteriaContext.NULL);
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

	private final AnonymousRelationship relationship;

	/**
	 * (inner) alias
	 * @param inner {@link SelectStatement}
	 * @param alias 表別名
	 */
	public AnonymousTable(SelectStatement inner, String alias) {
		relationship = new AnonymousRelationship(inner.query(), alias);
	}

	/**
	 * SELECT 句を記述します。
	 * @param function {@link SelectOfferFunction}
	 * @return この {@link SelectStatement}
	 */
	public AnonymousTable SELECT(SelectOfferFunction<SelectRel> function) {
		behavior().SELECT(function);
		return this;
	}

	/**
	 * DISTINCT を使用した SELECT 句を記述します。
	 * @param function {@link SelectOfferFunction}
	 * @return この {@link SelectStatement}
	 */
	public AnonymousTable SELECT_DISTINCT(SelectOfferFunction<SelectRel> function) {
		behavior().SELECT_DISTINCT(function);
		return this;
	}

	/**
	 * COUNT(*) を使用した SELECT 句を記述します。
	 * @return この {@link SelectStatement}
	 */
	public AnonymousTable SELECT_COUNT() {
		behavior().SELECT_COUNT();
		return this;
	}

	/**
	 * GROUP BY 句を記述します。
	 * @param function {link {@link GroupByOfferFunction}}
	 * @return この {@link SelectStatement}
	 */
	public AnonymousTable GROUP_BY(GroupByOfferFunction<GroupByRel> function) {
		behavior().GROUP_BY(function);
		return this;
	}

	/**
	 * WHERE 句を記述します。
	 * @param consumers {@link Consumer}
	 * @return この {@link SelectStatement}
	 */
	@SafeVarargs
	public final AnonymousTable WHERE(Consumer<WhereRel>... consumers) {
		behavior().WHERE(consumers);
		return this;
	}

	/**
	 * WHERE 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 * @return {@link Criteria}
	 */
	public Criteria createWhereCriteria(Consumer<WhereRel> consumer) {
		return behavior().createWhereCriteria(consumer);
	}

	/**
	 * HAVING 句を記述します。
	 * @param consumers {@link Consumer}
	 * @return この {@link SelectStatement}
	 */
	@SafeVarargs
	public final AnonymousTable HAVING(Consumer<HavingRel>... consumers) {
		behavior().HAVING(consumers);
		return this;
	}

	/**
	 * HAVING 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 * @return {@link Criteria}
	 */
	public Criteria createHavingCriteria(Consumer<HavingRel> consumer) {
		return behavior().createHavingCriteria(consumer);
	}

	/**
	 * このクエリに INNER JOIN で別テーブルを結合します。
	 * @param right 別クエリの {@link OnRightRelationship}
	 * @param <R> {@link OnRightRelationship}
	 * @return ON
	 */
	public <R extends OnRightRelationship> OnClause<OnLeftRel, R, AnonymousTable> INNER_JOIN(
		RightTable<R> right) {
		return behavior().INNER_JOIN(right, this);
	}

	/**
	 * このクエリに LEFT OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリの {@link OnRightRelationship}
	 * @param <R> {@link OnRightRelationship}
	 * @return ON
	 */
	public <R extends OnRightRelationship> OnClause<OnLeftRel, R, AnonymousTable> LEFT_OUTER_JOIN(
		RightTable<R> right) {
		return behavior().LEFT_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに RIGHT OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリの {@link OnRightRelationship}
	 * @param <R> {@link OnRightRelationship}
	 * @return ON
	 */
	public <R extends OnRightRelationship> OnClause<OnLeftRel, R, AnonymousTable> RIGHT_OUTER_JOIN(
		RightTable<R> right) {
		return behavior().RIGHT_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに FULL OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリの {@link OnRightRelationship}
	 * @param <R> {@link OnRightRelationship}
	 * @return ON
	 */
	public <R extends OnRightRelationship> OnClause<OnLeftRel, R, AnonymousTable> FULL_OUTER_JOIN(
		RightTable<R> right) {
		return behavior().FULL_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに CROSS JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @param <R> {@link OnRightRelationship}
	 * @return この {@link SelectStatement}
	 */
	public <R extends OnRightRelationship> AnonymousTable CROSS_JOIN(RightTable<R> right) {
		behavior().CROSS_JOIN(right, this);
		return this;
	}

	/**
	 * UNION するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param select UNION 対象
	 * @return この {@link SelectStatement}
	 */
	public AnonymousTable UNION(SelectStatement select) {
		behavior().UNION(select);
		return this;
	}

	/**
	 * UNION ALL するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param select UNION ALL 対象
	 * @return この {@link SelectStatement}
	 */
	public AnonymousTable UNION_ALL(SelectStatement select) {
		behavior().UNION_ALL(select);
		return this;
	}

	/**
	 * ORDER BY 句を記述します。
	 * @param function {@link OrderByOfferFunction}
	 * @return この {@link SelectStatement}
	 */
	public AnonymousTable ORDER_BY(OrderByOfferFunction<OrderByRel> function) {
		behavior().ORDER_BY(function);
		return this;
	}

	@Override
	public boolean hasWhereClause() {
		return behavior().hasWhereClause();
	}

	@Override
	public Optimizer getOptimizer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public GroupByClause getGroupByClause() {
		return behavior().getGroupByClause();
	}

	@Override
	public OrderByClause getOrderByClause() {
		return behavior().getOrderByClause();
	}

	@Override
	public Criteria getWhereClause() {
		return behavior().getWhereClause();
	}

	/**
	 * 新規に GROUP BY 句をセットします。
	 * @param clause 新 ORDER BY 句
	 * @return {@link SelectStatement} 自身
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public AnonymousTable groupBy(GroupByClause clause) {
		behavior().setGroupByClause(clause);
		return this;
	}

	/**
	 * 新規に ORDER BY 句をセットします。
	 * @param clause 新 ORDER BY 句
	 * @return {@link SelectStatement} 自身
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public AnonymousTable orderBy(OrderByClause clause) {
		behavior().setOrderByClause(clause);
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たな条件を AND 結合します。<br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria AND 結合する新条件
	 * @return {@link SelectStatement} 自身
	 */
	public AnonymousTable and(Criteria criteria) {
		behavior().and(criteria);
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たな条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria OR 結合する新条件
	 * @return {@link SelectStatement} 自身
	 */
	public AnonymousTable or(Criteria criteria) {
		behavior().or(criteria);
		return this;
	}

	/**
	 * 生成された SQL 文を加工する {SQLDecorator} を設定します。
	 * @param decorators {@link SQLDecorator}
	 * @return {@link SelectStatement} 自身
	 */
	@Override
	public AnonymousTable apply(SQLDecorator... decorators) {
		for (SQLDecorator decorator : decorators) {
			this.decorators.add(decorator);
		}

		return this;
	}

	@Override
	public Relationship getRootRealtionship() {
		return relationship;
	}

	@Override
	public LogicalOperators<WhereRel> getWhereLogicalOperators() {
		return behavior().whereOperators();
	}

	@Override
	public LogicalOperators<HavingRel> getHavingLogicalOperators() {
		return behavior().havingOperators();
	}

	@Override
	public LogicalOperators<OnLeftRel> getOnLeftLogicalOperators() {
		return behavior().onLeftOperators();
	}

	@Override
	public OnRightLogicalOperators getOnRightLogicalOperators() {
		return onRightOperators == null ? (onRightOperators = new OnRightLogicalOperators()) : onRightOperators;
	}

	@Override
	public SQLDecorator[] decorators() {
		return decorators.toArray(new SQLDecorator[decorators.size()]);
	}

	@Override
	public AutoCloseableIterator<Void> execute() {
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
		return behavior().composeSQL().sql();
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		careEmptySelect();
		return behavior().composeSQL().complement(done, statement);
	}

	@Override
	public AnonymousQuery reproduce(Object... placeHolderValues) {
		careEmptySelect();
		return new AnonymousQuery(behavior().query().reproduce(placeHolderValues));
	}

	@Override
	public AnonymousQuery reproduce() {
		careEmptySelect();
		return new AnonymousQuery(behavior().query().reproduce());
	}

	@Override
	public Binder[] currentBinders() {
		careEmptySelect();
		return behavior().query().currentBinders();
	}

	@Override
	public void joinTo(SQLQueryBuilder builder, JoinType joinType, Criteria onCriteria) {
		careEmptySelect();//下でこのQueryが評価されてしまうのでSELECT句を補う
		behavior().joinTo(builder, joinType, onCriteria);
	}

	@Override
	public SQLQueryBuilder toSQLQueryBuilder() {
		careEmptySelect();
		return behavior().buildBuilder();
	}

	@Override
	public void forSubquery(boolean forSubquery) {
		behavior().forSubquery(forSubquery);
	}

	/**
	 * 現在保持している WHERE 句をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousTable resetWhere() {
		behavior().resetWhere();
		return this;
	}

	/**
	 * 現在保持している HAVING 句をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousTable resetHaving() {
		behavior().resetHaving();
		return this;
	}

	/**
	 * 現在保持している SELECT 句をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousTable resetSelect() {
		behavior().resetSelect();
		return this;
	}

	/**
	 * 現在保持している GROUP BY 句をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousTable resetGroupBy() {
		behavior().resetGroupBy();
		return this;
	}

	/**
	 * 現在保持している ORDER BY 句をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousTable resetOrderBy() {
		behavior().resetOrderBy();
		return this;
	}

	/**
	 * 現在保持している UNION をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousTable resetUnions() {
		behavior().resetUnions();
		return this;
	}

	/**
	 * 現在保持している JOIN をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousTable resetJoins() {
		behavior().resetJoins();
		return this;
	}

	/**
	 * 現在保持している {@link SQLDecorator} をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousTable resetDecorators() {
		decorators.clear();
		return this;
	}

	/**
	 * 現在保持している条件、並び順をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousTable reset() {
		behavior.reset();
		resetDecorators();
		return this;
	}

	@Override
	public void quitRowMode() {
		behavior().quitRowMode();
	}

	@Override
	public boolean rowMode() {
		return behavior().rowMode();
	}

	@Override
	public AnonymousQuery query() {
		careEmptySelect();
		return new AnonymousQuery(behavior().query());
	}

	@Override
	public OnRightRel joint() {
		return onRightOperators.AND;
	}

	@Override
	public SelectStatement getSelectStatement() {
		return this;
	}

	@Override
	public RuntimeId getRuntimeId() {
		return id;
	}

	@Override
	public String toString() {
		careEmptySelect();
		return behavior().toString();
	}

	private void careEmptySelect() {
		SelectClause select = behavior().getSelectClause();
		if (select == null || select.getColumnsSize() == 0) {
			select = new SelectClause(RuntimeIdFactory.getInstance());
			select.add("{0}", new PseudoColumn(relationship, "*", true));
			behavior().setSelectClause(select);
		}
	}

	/**
	 * {@link TableFacadeRelationship} の実装クラスです。<br>
	 * 条件として使用できるカラムを内包しており、それらを使用して検索 SQL を生成可能にします。
	 * @param <T> 使用されるカラムのタイプにあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class Rel<T, M> implements TableFacadeRelationship {

		private final AnonymousTable table;

		private final CriteriaContext context;

		private final TableFacadeContext<T> builder;

		private Rel(AnonymousTable table, TableFacadeContext<T> builder, CriteriaContext context) {
			this.table = table;
			this.context = context;
			this.builder = builder;
		}

		@Override
		public CriteriaContext getContext() {
			return context;
		}

		@Override
		public Relationship getRelationship() {
			return table.relationship;
		}

		@Override
		public SelectStatement getSelectStatement() {
			return table;
		}

		@Override
		public DataManipulationStatement getDataManipulationStatement() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @param name カラム名
		 * @return 使用されるカラムのタイプにあった型
		 */
		public T col(String name) {
			return builder.buildColumn(this, name);
		}

		@Override
		public OneToManyRelationship getOneToManyRelationship() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * SELECT 句用
	 */
	public static class SelectRel extends Rel<SelectCol, Void> implements SelectRelationship {

		private SelectRel(AnonymousTable query, TableFacadeContext<SelectCol> builder, CriteriaContext context) {
			super(query, builder, context);
		}
	}

	/**
	 * WHERE 句用
	 */
	public static class WhereRel extends Rel<WhereColumn<WhereLogicalOperators>, Void>
		implements WhereRelationship {

		/**
		 * 条件接続 OR
		 */
		public final WhereRel OR;

		private WhereRel(
			AnonymousTable query,
			TableFacadeContext<WhereColumn<WhereLogicalOperators>> builder,
			CriteriaContext context,
			WhereRel or) {
			super(query, builder, context);
			OR = or == null ? this : or;
		}
	}

	/**
	 * GROUB BY 句用
	 */
	public static class GroupByRel extends Rel<GroupByCol, Void> implements GroupByRelationship {

		private GroupByRel(AnonymousTable query, TableFacadeContext<GroupByCol> builder, CriteriaContext context) {
			super(query, builder, context);
		}
	}

	/**
	 * HAVING 句用
	 */
	public static class HavingRel extends Rel<HavingColumn<HavingLogicalOperators>, Void>
		implements HavingRelationship {

		/**
		 * 条件接続 OR
		 */
		public final HavingRel OR;

		private HavingRel(
			AnonymousTable query,
			TableFacadeContext<HavingColumn<HavingLogicalOperators>> builder,
			CriteriaContext context,
			HavingRel or) {
			super(query, builder, context);
			OR = or == null ? this : or;
		}
	}

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByRel extends Rel<OrderByCol, Void> implements OrderByRelationship {

		private OrderByRel(AnonymousTable query, TableFacadeContext<OrderByCol> builder, CriteriaContext context) {
			super(query, builder, context);
		}

		@Override
		public OrderByClause getOrderByClause() {
			return getSelectStatement().getOrderByClause();
		}
	}

	/**
	 * ON 句 (LEFT) 用
	 */
	public static class OnLeftRel extends Rel<OnLeftColumn<OnLeftLogicalOperators>, Void>
		implements OnLeftRelationship {

		/**
		 * 条件接続 OR
		 */
		public final OnLeftRel OR;

		private OnLeftRel(
			AnonymousTable query,
			TableFacadeContext<OnLeftColumn<OnLeftLogicalOperators>> builder,
			CriteriaContext context,
			OnLeftRel or) {
			super(query, builder, context);
			OR = or == null ? this : or;
		}
	}

	/**
	 * ON 句 (RIGHT) 用
	 */
	public static class OnRightRel extends Rel<OnRightColumn<OnRightLogicalOperators>, Void>
		implements OnRightRelationship {

		/**
		 * 条件接続 OR
		 */
		public final OnRightRel OR;

		private OnRightRel(
			AnonymousTable query,
			TableFacadeContext<OnRightColumn<OnRightLogicalOperators>> builder,
			CriteriaContext context,
			OnRightRel or) {
			super(query, builder, context);
			OR = or == null ? this : or;
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
	 * Query
	 */
	public class AnonymousQuery implements Query<AutoCloseableIterator<Void>, Void> {

		private final PlaybackQuery inner;

		private AnonymousQuery(PlaybackQuery inner) {
			this.inner = inner;
		}

		@Override
		public AutoCloseableIterator<Void> execute() {
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
		public AnonymousQuery reproduce(Object... placeHolderValues) {
			return new AnonymousQuery(inner.reproduce(placeHolderValues));
		}

		@Override
		public AnonymousQuery reproduce() {
			return new AnonymousQuery(inner.reproduce());
		}

		@Override
		public Binder[] currentBinders() {
			return inner.currentBinders();
		}
	}
}
