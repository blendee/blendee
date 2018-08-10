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
import org.blendee.sql.SelectStatementBuilder;
import org.blendee.sql.Relationship;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.SelectClause;
import org.blendee.support.QueryBuilderBehavior.PlaybackQuery;

/**
 * 無名テーブルクラスです。
 * @author 千葉 哲嗣
 */
public class AnonymousTable extends java.lang.Object
	implements QueryBuilder, Query<Iterator<Void>, Void>, RightTable<AnonymousTable.OnRightQRel> {

	private static final TableFacadeContext<SelectQCol> selectContext = (
		relationship,
		name) -> new SelectQCol(relationship, name);

	private static final TableFacadeContext<GroupByQCol> groupByContext = (
		relationship,
		name) -> new GroupByQCol(relationship, name);

	private static final TableFacadeContext<OrderByQCol> orderByContext = (
		relationship,
		name) -> new OrderByQCol(relationship, name);

	private static final TableFacadeContext<WhereColumn<WhereLogicalOperators>> whereContext = TableFacadeContext
		.newWhereBuilder();

	private static final TableFacadeContext<HavingColumn<HavingLogicalOperators>> havingContext = TableFacadeContext
		.newHavingBuilder();

	private static final TableFacadeContext<OnLeftColumn<OnLeftLogicalOperators>> onLeftContext = TableFacadeContext
		.newOnLeftBuilder();

	private static final TableFacadeContext<OnRightColumn<OnRightLogicalOperators>> onRightContext = TableFacadeContext
		.newOnRightBuilder();

	/**
	 * WHERE 句 で使用する AND, OR です。
	 */
	public class WhereLogicalOperators implements LogicalOperators<WhereQRel> {

		private WhereLogicalOperators() {}

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final WhereQRel AND = new WhereQRel(AnonymousTable.this, whereContext, CriteriaContext.AND);

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final WhereQRel OR = new WhereQRel(AnonymousTable.this, whereContext, CriteriaContext.OR);

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
		 * HAVING 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final HavingQRel AND = new HavingQRel(AnonymousTable.this, havingContext, CriteriaContext.AND);

		/**
		 * HAVING 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final HavingQRel OR = new HavingQRel(AnonymousTable.this, havingContext, CriteriaContext.OR);

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
		 * ON 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final OnLeftQRel AND = new OnLeftQRel(AnonymousTable.this, onLeftContext, CriteriaContext.AND);

		/**
		 * ON 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final OnLeftQRel OR = new OnLeftQRel(AnonymousTable.this, onLeftContext, CriteriaContext.OR);

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
		 * ON 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final OnRightQRel AND = new OnRightQRel(AnonymousTable.this, onRightContext, CriteriaContext.AND);

		/**
		 * ON 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final OnRightQRel OR = new OnRightQRel(AnonymousTable.this, onRightContext, CriteriaContext.OR);

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
	 * SELECT 句用のカラムを選択するための {@link TableFacadeRelationship} です。
	 */
	private final SelectQRel select = new SelectQRel(this, selectContext, CriteriaContext.NULL);

	/**
	 * GROUP BY 句用のカラムを選択するための {@link TableFacadeRelationship} です。
	 */
	private final GroupByQRel groupBy = new GroupByQRel(this, groupByContext, CriteriaContext.NULL);

	/**
	 * ORDER BY 句用のカラムを選択するための {@link TableFacadeRelationship} です。
	 */
	private final OrderByQRel orderBy = new OrderByQRel(this, orderByContext, CriteriaContext.NULL);

	private final QueryBuilderBehavior<SelectQRel, GroupByQRel, WhereQRel, HavingQRel, OrderByQRel, OnLeftQRel> helper;

	private final AnonymousRelationship relationship;

	/**
	 * (inner) alias
	 * @param inner {@link QueryBuilder}
	 * @param alias 表別名
	 */
	public AnonymousTable(QueryBuilder inner, String alias) {
		relationship = new AnonymousRelationship(inner.query(), alias);

		helper = new QueryBuilderBehavior<>(
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
	 * @return この {@link QueryBuilder}
	 */
	public AnonymousTable SELECT(SelectOfferFunction<SelectQRel> function) {
		helper.SELECT(function);
		return this;
	}

	/**
	 * DISTINCT を使用した SELECT 句を記述します。
	 * @param function
	 * @return この {@link QueryBuilder}
	 */
	public AnonymousTable SELECT_DISTINCT(SelectOfferFunction<SelectQRel> function) {
		helper.SELECT_DISTINCT(function);
		return this;
	}

	/**
	 * COUNT(*) を使用した SELECT 句を記述します。
	 * @return この {@link QueryBuilder}
	 */
	public AnonymousTable SELECT_COUNT() {
		helper.SELECT_COUNT();
		return this;
	}

	/**
	 * GROUP BY 句を記述します。
	 * @param function
	 * @return この {@link QueryBuilder}
	 */
	public AnonymousTable GROUP_BY(GroupByOfferFunction<GroupByQRel> function) {
		helper.GROUP_BY(function);
		return this;
	}

	/**
	 * WHERE 句を記述します。
	 * @param consumers
	 * @return この {@link QueryBuilder}
	 */
	@SafeVarargs
	public final AnonymousTable WHERE(Consumer<WhereQRel>... consumers) {
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
	 * @return この {@link QueryBuilder}
	 */
	@SafeVarargs
	public final AnonymousTable HAVING(Consumer<HavingQRel>... consumers) {
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
	 * @param right 別クエリの {@link OnRightRelationship}
	 * @return ON
	 */
	public <R extends OnRightRelationship> OnClause<OnLeftQRel, R, AnonymousTable> INNER_JOIN(
		RightTable<R> right) {
		return helper.INNER_JOIN(right, this);
	}

	/**
	 * このクエリに LEFT OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリの {@link OnRightRelationship}
	 * @return ON
	 */
	public <R extends OnRightRelationship> OnClause<OnLeftQRel, R, AnonymousTable> LEFT_OUTER_JOIN(
		RightTable<R> right) {
		return helper.LEFT_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに RIGHT OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリの {@link OnRightRelationship}
	 * @return ON
	 */
	public <R extends OnRightRelationship> OnClause<OnLeftQRel, R, AnonymousTable> RIGHT_OUTER_JOIN(
		RightTable<R> right) {
		return helper.RIGHT_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに FULL OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリの {@link OnRightRelationship}
	 * @return ON
	 */
	public <R extends OnRightRelationship> OnClause<OnLeftQRel, R, AnonymousTable> FULL_OUTER_JOIN(
		RightTable<R> right) {
		return helper.FULL_OUTER_JOIN(right, this);
	}

	/**
	 * UNION するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param sql UNION 対象
	 * @return この {@link QueryBuilder}
	 */
	public AnonymousTable UNION(ComposedSQL sql) {
		helper.UNION(sql);
		return this;
	}

	/**
	 * UNION ALL するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param sql UNION ALL 対象
	 * @return この {@link QueryBuilder}
	 */
	public AnonymousTable UNION_ALL(ComposedSQL sql) {
		helper.UNION_ALL(sql);
		return this;
	}

	/**
	 * ORDER BY 句を記述します。
	 * @param function
	 * @return この {@link QueryBuilder}
	 */
	public AnonymousTable ORDER_BY(OrderByOfferFunction<OrderByQRel> function) {
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
	 * @return {@link QueryBuilder} 自身
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public AnonymousTable groupBy(GroupByClause clause) {
		helper.setGroupByClause(clause);
		return this;
	}

	/**
	 * 新規に ORDER BY 句をセットします。
	 * @param clause 新 ORDER BY 句
	 * @return {@link QueryBuilder} 自身
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public AnonymousTable orderBy(OrderByClause clause) {
		helper.setOrderByClause(clause);
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たな条件を AND 結合します。<br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria AND 結合する新条件
	 * @return {@link QueryBuilder} 自身
	 */
	public AnonymousTable and(Criteria criteria) {
		helper.and(criteria);
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たな条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria OR 結合する新条件
	 * @return {@link QueryBuilder} 自身
	 */
	public AnonymousTable or(Criteria criteria) {
		helper.or(criteria);
		return this;
	}

	/**
	 * 生成された SQL 文を加工する {SQLDecorator} を設定します。
	 * @param decorators {@link SQLDecorator}
	 * @return {@link QueryBuilder} 自身
	 */
	public AnonymousTable apply(SQLDecorator... decorators) {
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
	public AnonymousQuery reproduce(Object... placeHolderValues) {
		careEmptySelect();
		return new AnonymousQuery(helper.query().reproduce(placeHolderValues));
	}

	@Override
	public void joinTo(SelectStatementBuilder builder, JoinType joinType, Criteria onCriteria) {
		careEmptySelect();//下でこのQueryが評価されてしまうのでSELECT句を補う
		helper.joinTo(builder, joinType, onCriteria);
	}

	@Override
	public SelectStatementBuilder toSelectStatementBuilder() {
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
	public AnonymousTable resetWhere() {
		helper.resetWhere();
		return this;
	}

	/**
	 * 現在保持している HAVING 句をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousTable resetHaving() {
		helper.resetHaving();
		return this;
	}

	/**
	 * 現在保持している SELECT 句をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousTable resetSelect() {
		helper.resetSelect();
		return this;
	}

	/**
	 * 現在保持している GROUP BY 句をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousTable resetGroupBy() {
		helper.resetGroupBy();
		return this;
	}

	/**
	 * 現在保持している ORDER BY 句をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousTable resetOrderBy() {
		helper.resetOrderBy();
		return this;
	}

	/**
	 * 現在保持している UNION をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousTable resetUnions() {
		helper.resetUnions();
		return this;
	}

	/**
	 * 現在保持している JOIN をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousTable resetJoins() {
		helper.resetJoins();
		return this;
	}

	/**
	 * 現在保持している {@link SQLDecorator} をリセットします。
	 * @return このインスタンス
	 */
	public AnonymousTable resetDecorators() {
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
	public AnonymousQuery query() {
		careEmptySelect();
		return new AnonymousQuery(helper.query());
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
	 * {@link TableFacadeRelationship} の実装クラスです。<br>
	 * 条件として使用できるカラムを内包しており、それらを使用して検索 SQL を生成可能にします。
	 * @param <T> 使用されるカラムのタイプにあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class QRel<T, M> implements TableFacadeRelationship {

		private final AnonymousTable table$;

		private final CriteriaContext context$;

		private final TableFacadeContext<T> builder$;

		private QRel(AnonymousTable table$, TableFacadeContext<T> builder$, CriteriaContext context$) {
			this.table$ = table$;
			this.context$ = context$;
			this.builder$ = builder$;
		}

		@Override
		public CriteriaContext getContext() {
			return context$;
		}

		@Override
		public Relationship getRelationship() {
			return table$.relationship;
		}

		@Override
		public Optimizer getOptimizer() {
			throw new UnsupportedOperationException();
		}

		@Override
		public GroupByClause getGroupByClause() {
			return table$.helper.getGroupByClause();
		}

		@Override
		public OrderByClause getOrderByClause() {
			return table$.helper.getOrderByClause();
		}

		@Override
		public Criteria getWhereClause() {
			return table$.helper.getWhereClause();
		}

		@Override
		public TableFacadeRelationship getParent() {
			throw new UnsupportedOperationException();
		}

		@Override
		public TablePath getTablePath() {
			throw new UnsupportedOperationException();
		}

		@Override
		public QueryBuilder getRoot() {
			return table$;
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
			return builder$.buildColumn(this, name);
		}
	}

	/**
	 * SELECT 句用
	 */
	public static class SelectQRel extends QRel<SelectQCol, Void> implements SelectRelationship {

		private SelectQRel(AnonymousTable query$, TableFacadeContext<SelectQCol> builder$, CriteriaContext context$) {
			super(query$, builder$, context$);
		}
	}

	/**
	 * WHERE 句用
	 */
	public static class WhereQRel extends QRel<WhereColumn<WhereLogicalOperators>, Void>
		implements WhereRelationship {

		private WhereQRel(
			AnonymousTable query$,
			TableFacadeContext<WhereColumn<WhereLogicalOperators>> builder$,
			CriteriaContext context$) {
			super(query$, builder$, context$);
		}
	}

	/**
	 * GROUB BY 句用
	 */
	public static class GroupByQRel extends QRel<GroupByQCol, Void> implements GroupByRelationship {

		private GroupByQRel(AnonymousTable query$, TableFacadeContext<GroupByQCol> builder$, CriteriaContext context$) {
			super(query$, builder$, context$);
		}
	}

	/**
	 * HAVING 句用
	 */
	public static class HavingQRel extends QRel<HavingColumn<HavingLogicalOperators>, Void>
		implements HavingRelationship {

		private HavingQRel(
			AnonymousTable query$,
			TableFacadeContext<HavingColumn<HavingLogicalOperators>> builder$,
			CriteriaContext context$) {
			super(query$, builder$, context$);
		}
	}

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByQRel extends QRel<OrderByQCol, Void> implements OrderByRelationship {

		private OrderByQRel(AnonymousTable query$, TableFacadeContext<OrderByQCol> builder$, CriteriaContext context$) {
			super(query$, builder$, context$);
		}
	}

	/**
	 * ON 句 (LEFT) 用
	 */
	public static class OnLeftQRel extends QRel<OnLeftColumn<OnLeftLogicalOperators>, Void>
		implements OnLeftRelationship {

		private OnLeftQRel(
			AnonymousTable query$,
			TableFacadeContext<OnLeftColumn<OnLeftLogicalOperators>> builder$,
			CriteriaContext context$) {
			super(query$, builder$, context$);
		}
	}

	/**
	 * ON 句 (RIGHT) 用
	 */
	public static class OnRightQRel extends QRel<OnRightColumn<OnRightLogicalOperators>, Void>
		implements OnRightRelationship {

		private OnRightQRel(
			AnonymousTable query$,
			TableFacadeContext<OnRightColumn<OnRightLogicalOperators>> builder$,
			CriteriaContext context$) {
			super(query$, builder$, context$);
		}
	}

	/**
	 * SELECT 句用
	 */
	public static class SelectQCol extends SelectColumn {

		private SelectQCol(TableFacadeRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * GROUP BY 句用
	 */
	public static class GroupByQCol extends GroupByColumn {

		private GroupByQCol(TableFacadeRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByQCol extends OrderByColumn {

		private OrderByQCol(TableFacadeRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * Query
	 */
	public class AnonymousQuery implements org.blendee.support.Query<Iterator<Void>, Void> {

		private final PlaybackQuery inner;

		private AnonymousQuery(PlaybackQuery inner) {
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
		public AnonymousQuery reproduce(Object... placeHolderValues) {
			return new AnonymousQuery(inner.reproduce(placeHolderValues));
		}
	}
}
