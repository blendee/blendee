package org.blendee.assist;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.blendee.assist.SelectStatementBehavior.PlaybackQuery;
import org.blendee.jdbc.AutoCloseableIterator;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.selector.Optimizer;
import org.blendee.sql.Bindable;
import org.blendee.sql.Binder;
import org.blendee.sql.Criteria;
import org.blendee.sql.FromClause.JoinType;
import org.blendee.sql.GroupByClause;
import org.blendee.sql.MultiColumn;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.PseudoColumn;
import org.blendee.sql.Relationship;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.RuntimeIdFactory;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.SQLQueryBuilder;
import org.blendee.sql.SelectClause;

/**
 * 無名テーブルクラスです。
 * @author 千葉 哲嗣
 */
public class AnonymousTable implements SelectStatement, Query<AutoCloseableIterator<Void>, Void>, RightTable<AnonymousTable.OnRightAssist> {

	private static final TableFacadeContext<SelectCol> selectContext = (
		assist,
		name) -> new SelectCol(assist, name);

	private static final TableFacadeContext<GroupByCol> groupByContext = (
		assist,
		name) -> new GroupByCol(assist, name);

	private static final TableFacadeContext<OrderByCol> orderByContext = (
		assist,
		name) -> new OrderByCol(assist, name);

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
	public class WhereLogicalOperators implements LogicalOperators<WhereAssist> {

		private WhereLogicalOperators() {}

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final WhereAssist OR = new WhereAssist(AnonymousTable.this, whereContext, CriteriaContext.OR, null);

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final WhereAssist AND = new WhereAssist(AnonymousTable.this, whereContext, CriteriaContext.AND, OR);

		@Override
		public WhereAssist defaultOperator() {
			return AND;
		}
	}

	/**
	 * HAVING 句 で使用する AND, OR です。
	 */
	public class HavingLogicalOperators implements LogicalOperators<HavingAssist> {

		private HavingLogicalOperators() {}

		/**
		 * HAVING 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final HavingAssist OR = new HavingAssist(AnonymousTable.this, havingContext, CriteriaContext.OR, null);

		/**
		 * HAVING 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final HavingAssist AND = new HavingAssist(AnonymousTable.this, havingContext, CriteriaContext.AND, OR);

		@Override
		public HavingAssist defaultOperator() {
			return AND;
		}
	}

	/**
	 * ON 句 (LEFT) で使用する AND, OR です。
	 */
	public class OnLeftLogicalOperators implements LogicalOperators<OnLeftAssist> {

		private OnLeftLogicalOperators() {}

		/**
		 * ON 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final OnLeftAssist OR = new OnLeftAssist(AnonymousTable.this, onLeftContext, CriteriaContext.OR, null);

		/**
		 * ON 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final OnLeftAssist AND = new OnLeftAssist(AnonymousTable.this, onLeftContext, CriteriaContext.AND, OR);

		@Override
		public OnLeftAssist defaultOperator() {
			return AND;
		}
	}

	/**
	 * ON 句 (RIGHT) で使用する AND, OR です。
	 */
	public class OnRightLogicalOperators implements LogicalOperators<OnRightAssist> {

		private OnRightLogicalOperators() {}

		/**
		 * ON 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final OnRightAssist OR = new OnRightAssist(AnonymousTable.this, onRightContext, CriteriaContext.OR, null);

		/**
		 * ON 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final OnRightAssist AND = new OnRightAssist(AnonymousTable.this, onRightContext, CriteriaContext.AND, OR);

		@Override
		public OnRightAssist defaultOperator() {
			return AND;
		}
	}

	private final List<SQLDecorator> decorators = new LinkedList<SQLDecorator>();

	private OnRightLogicalOperators onRightOperators;

	private Behavior behavior;

	private Behavior behavior() {
		return behavior == null ? (behavior = new Behavior()) : behavior;
	}

	private class Behavior extends SelectStatementBehavior<SelectAssist, GroupByAssist, WhereAssist, HavingAssist, OrderByAssist, OnLeftAssist> {

		private Behavior() {
			super(new AnonymousFromClause(relationship, id), AnonymousTable.this);
		}

		@Override
		protected SelectAssist newSelect() {
			return new SelectAssist(AnonymousTable.this, selectContext, CriteriaContext.NULL);
		}

		@Override
		protected GroupByAssist newGroupBy() {
			return new GroupByAssist(AnonymousTable.this, groupByContext, CriteriaContext.NULL);
		}

		@Override
		protected OrderByAssist newOrderBy() {
			return new OrderByAssist(AnonymousTable.this, orderByContext, CriteriaContext.NULL);
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
	public AnonymousTable SELECT(SelectOfferFunction<SelectAssist> function) {
		behavior().SELECT(function);
		return this;
	}

	/**
	 * DISTINCT を使用した SELECT 句を記述します。
	 * @param function {@link SelectOfferFunction}
	 * @return この {@link SelectStatement}
	 */
	public AnonymousTable SELECT_DISTINCT(SelectOfferFunction<SelectAssist> function) {
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
	public AnonymousTable GROUP_BY(GroupByOfferFunction<GroupByAssist> function) {
		behavior().GROUP_BY(function);
		return this;
	}

	/**
	 * WHERE 句を記述します。
	 * @param consumers {@link Consumer}
	 * @return この {@link SelectStatement}
	 */
	@SafeVarargs
	public final AnonymousTable WHERE(Consumer<WhereAssist>... consumers) {
		behavior().WHERE(consumers);
		return this;
	}

	/**
	 * WHERE 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 * @return {@link Criteria}
	 */
	public Criteria createWhereCriteria(Consumer<WhereAssist> consumer) {
		return behavior().createWhereCriteria(consumer);
	}

	/**
	 * HAVING 句を記述します。
	 * @param consumers {@link Consumer}
	 * @return この {@link SelectStatement}
	 */
	@SafeVarargs
	public final AnonymousTable HAVING(Consumer<HavingAssist>... consumers) {
		behavior().HAVING(consumers);
		return this;
	}

	/**
	 * HAVING 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 * @return {@link Criteria}
	 */
	public Criteria createHavingCriteria(Consumer<HavingAssist> consumer) {
		return behavior().createHavingCriteria(consumer);
	}

	/**
	 * このクエリに INNER JOIN で別テーブルを結合します。
	 * @param right 別クエリの {@link OnRightClauseAssist}
	 * @param <R> {@link OnRightClauseAssist}
	 * @return ON
	 */
	public <R extends OnRightClauseAssist<?>> OnClause<OnLeftAssist, R, AnonymousTable> INNER_JOIN(
		RightTable<R> right) {
		return behavior().INNER_JOIN(right, this);
	}

	/**
	 * このクエリに LEFT OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリの {@link OnRightClauseAssist}
	 * @param <R> {@link OnRightClauseAssist}
	 * @return ON
	 */
	public <R extends OnRightClauseAssist<?>> OnClause<OnLeftAssist, R, AnonymousTable> LEFT_OUTER_JOIN(
		RightTable<R> right) {
		return behavior().LEFT_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに RIGHT OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリの {@link OnRightClauseAssist}
	 * @param <R> {@link OnRightClauseAssist}
	 * @return ON
	 */
	public <R extends OnRightClauseAssist<?>> OnClause<OnLeftAssist, R, AnonymousTable> RIGHT_OUTER_JOIN(
		RightTable<R> right) {
		return behavior().RIGHT_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに FULL OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリの {@link OnRightClauseAssist}
	 * @param <R> {@link OnRightClauseAssist}
	 * @return ON
	 */
	public <R extends OnRightClauseAssist<?>> OnClause<OnLeftAssist, R, AnonymousTable> FULL_OUTER_JOIN(
		RightTable<R> right) {
		return behavior().FULL_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに CROSS JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @param <R> {@link OnRightClauseAssist}
	 * @return この {@link SelectStatement}
	 */
	public <R extends OnRightClauseAssist<?>> AnonymousTable CROSS_JOIN(RightTable<R> right) {
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
	public AnonymousTable ORDER_BY(OrderByOfferFunction<OrderByAssist> function) {
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
	public LogicalOperators<WhereAssist> getWhereLogicalOperators() {
		return behavior().whereOperators();
	}

	@Override
	public LogicalOperators<HavingAssist> getHavingLogicalOperators() {
		return behavior().havingOperators();
	}

	@Override
	public LogicalOperators<OnLeftAssist> getOnLeftLogicalOperators() {
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
	public OnRightAssist joint() {
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
	 * {@link TableFacadeAssist} の実装クラスです。<br>
	 * 条件として使用できるカラムを内包しており、それらを使用して検索 SQL を生成可能にします。
	 * @param <T> 使用されるカラムのタイプにあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class Assist<T, M> implements TableFacadeAssist {

		private final AnonymousTable table;

		private final CriteriaContext context;

		private final TableFacadeContext<T> builder;

		private Assist(AnonymousTable table, TableFacadeContext<T> builder, CriteriaContext context) {
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
		public OneToManyBehavior getOneToManyBehavior() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * SELECT 句用
	 */
	public static class SelectAssist extends Assist<SelectCol, Void> implements SelectClauseAssist {

		private SelectAssist(AnonymousTable query, TableFacadeContext<SelectCol> builder, CriteriaContext context) {
			super(query, builder, context);
		}
	}

	/**
	 * WHERE 句用
	 */
	public static class WhereAssist extends Assist<WhereColumn<WhereLogicalOperators>, Void>
		implements WhereClauseAssist<WhereAssist> {

		/**
		 * 条件接続 OR
		 */
		public final WhereAssist OR;

		private WhereAssist(
			AnonymousTable query,
			TableFacadeContext<WhereColumn<WhereLogicalOperators>> builder,
			CriteriaContext context,
			WhereAssist or) {
			super(query, builder, context);
			OR = or == null ? this : or;
		}

		/**
		 * この句に任意のカラムを追加します。
		 * @param template カラムのテンプレート
		 * @return {@link LogicalOperators} AND か OR
		 */
		@Override
		public WhereColumn<WhereLogicalOperators> any(String template) {
			return new WhereColumn<>(
				getSelectStatement(),
				getContext(),
				new MultiColumn(template));
		}

		/**
		 * Consumer に渡された条件句を () で囲みます。
		 * @param consumer {@link Consumer}
		 * @return this
		 */
		@Override
		public WhereLogicalOperators paren(Consumer<WhereAssist> consumer) {
			SelectStatement statement = getSelectStatement();
			Paren.execute(statement.getRuntimeId(), getContext(), consumer, this);
			return (WhereLogicalOperators) statement.getWhereLogicalOperators();
		}
	}

	/**
	 * GROUB BY 句用
	 */
	public static class GroupByAssist extends Assist<GroupByCol, Void> implements GroupByClauseAssist {

		private GroupByAssist(AnonymousTable query, TableFacadeContext<GroupByCol> builder, CriteriaContext context) {
			super(query, builder, context);
		}
	}

	/**
	 * HAVING 句用
	 */
	public static class HavingAssist extends Assist<HavingColumn<HavingLogicalOperators>, Void>
		implements HavingClauseAssist<HavingAssist> {

		/**
		 * 条件接続 OR
		 */
		public final HavingAssist OR;

		private HavingAssist(
			AnonymousTable query,
			TableFacadeContext<HavingColumn<HavingLogicalOperators>> builder,
			CriteriaContext context,
			HavingAssist or) {
			super(query, builder, context);
			OR = or == null ? this : or;
		}

		/**
		 * この句に任意のカラムを追加します。
		 * @param template カラムのテンプレート
		 * @return {@link LogicalOperators} AND か OR
		 */
		@Override
		public HavingColumn<HavingLogicalOperators> any(String template) {
			return new HavingColumn<>(
				getSelectStatement(),
				getContext(),
				new MultiColumn(template));
		}

		/**
		 * Consumer に渡された条件句を () で囲みます。
		 * @param consumer {@link Consumer}
		 * @return this
		 */
		@Override
		public HavingLogicalOperators paren(Consumer<HavingAssist> consumer) {
			SelectStatement statement = getSelectStatement();
			Paren.execute(statement.getRuntimeId(), getContext(), consumer, this);
			return (HavingLogicalOperators) statement.getHavingLogicalOperators();
		}
	}

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByAssist extends Assist<OrderByCol, Void> implements OrderByClauseAssist {

		private OrderByAssist(AnonymousTable query, TableFacadeContext<OrderByCol> builder, CriteriaContext context) {
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
	public static class OnLeftAssist extends Assist<OnLeftColumn<OnLeftLogicalOperators>, Void>
		implements OnLeftClauseAssist<OnLeftAssist> {

		/**
		 * 条件接続 OR
		 */
		public final OnLeftAssist OR;

		private OnLeftAssist(
			AnonymousTable query,
			TableFacadeContext<OnLeftColumn<OnLeftLogicalOperators>> builder,
			CriteriaContext context,
			OnLeftAssist or) {
			super(query, builder, context);
			OR = or == null ? this : or;
		}

		/**
		 * この句に任意のカラムを追加します。
		 * @param template カラムのテンプレート
		 * @return {@link OnLeftColumn}
		 */
		@Override
		public OnLeftColumn<OnLeftLogicalOperators> any(String template) {
			return new OnLeftColumn<>(
				getSelectStatement(),
				getContext(),
				new MultiColumn(template));
		}

		/**
		 * Consumer に渡された条件句を () で囲みます。
		 * @param consumer {@link Consumer}
		 * @return {@link OnLeftLogicalOperators}
		 */
		@Override
		public OnLeftLogicalOperators paren(Consumer<OnLeftAssist> consumer) {
			SelectStatement statement = getSelectStatement();
			Paren.execute(statement.getRuntimeId(), getContext(), consumer, this);
			return (OnLeftLogicalOperators) statement.getOnLeftLogicalOperators();
		}
	}

	/**
	 * ON 句 (RIGHT) 用
	 */
	public static class OnRightAssist extends Assist<OnRightColumn<OnRightLogicalOperators>, Void>
		implements OnRightClauseAssist<OnRightAssist> {

		/**
		 * 条件接続 OR
		 */
		public final OnRightAssist OR;

		private OnRightAssist(
			AnonymousTable query,
			TableFacadeContext<OnRightColumn<OnRightLogicalOperators>> builder,
			CriteriaContext context,
			OnRightAssist or) {
			super(query, builder, context);
			OR = or == null ? this : or;
		}

		/**
		 * この句に任意のカラムを追加します。
		 * @param template カラムのテンプレート
		 * @return {@link LogicalOperators} AND か OR
		 */
		@Override
		public OnRightColumn<OnRightLogicalOperators> any(String template) {
			return new OnRightColumn<>(
				getSelectStatement(),
				getContext(),
				new MultiColumn(template));
		}

		/**
		 * Consumer に渡された条件句を () で囲みます。
		 * @param consumer {@link Consumer}
		 * @return this
		 */
		@Override
		public OnRightLogicalOperators paren(Consumer<OnRightAssist> consumer) {
			SelectStatement statement = getSelectStatement();
			Paren.execute(statement.getRuntimeId(), getContext(), consumer, this);
			return (OnRightLogicalOperators) statement.getOnRightLogicalOperators();
		}
	}

	/**
	 * SELECT 句用
	 */
	public static class SelectCol extends SelectColumn {

		private SelectCol(TableFacadeAssist assist, String name) {
			super(assist, name);
		}
	}

	/**
	 * GROUP BY 句用
	 */
	public static class GroupByCol extends GroupByColumn {

		private GroupByCol(TableFacadeAssist assist, String name) {
			super(assist, name);
		}
	}

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByCol extends OrderByColumn {

		private OrderByCol(TableFacadeAssist assist, String name) {
			super(assist, name);
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
