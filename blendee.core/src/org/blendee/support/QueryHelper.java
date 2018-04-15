package org.blendee.support;

import java.util.function.Consumer;
import java.util.function.Function;

import org.blendee.jdbc.BlenConnection;
import org.blendee.jdbc.BlenResultSet;
import org.blendee.jdbc.BlenStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.ResultSetIterator;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataAccessHelper;
import org.blendee.selector.Optimizer;
import org.blendee.selector.RuntimeOptimizer;
import org.blendee.selector.SimpleOptimizer;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.Effector;
import org.blendee.sql.FromClause;
import org.blendee.sql.GroupByClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.QueryBuilder;
import org.blendee.sql.QueryBuilder.UnionOperator;
import org.blendee.sql.SelectClause;
import org.blendee.sql.SelectCountClause;
import org.blendee.sql.SelectDistinctClause;

/**
 * {@link Query} の内部処理を定義したヘルパークラスです。
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public class QueryHelper<S extends SelectQueryRelationship, G extends GroupByQueryRelationship, W, H, O extends OrderByQueryRelationship> {

	private final TablePath table;

	private final S select;

	private final G groupBy;

	private final O orderBy;

	private final LogicalOperators<W> whereOperators;

	private final LogicalOperators<H> havingOperators;

	private boolean rowMode = true;

	private Optimizer optimizer;

	private RuntimeOptimizer optimizerForSelect;

	private SelectClause selectClause;

	private Criteria whereClause;

	private GroupByClause groupByClause;

	private Criteria havingClause;

	private UnionOperator unionOperator;

	private ComposedSQL union;

	private OrderByClause orderByClause;

	public QueryHelper(
		TablePath table,
		S select,
		G groupBy,
		O orderBy,
		LogicalOperators<W> whereOperators,
		LogicalOperators<H> havingOperators) {
		this.table = table;
		this.select = select;
		this.groupBy = groupBy;
		this.orderBy = orderBy;
		this.whereOperators = whereOperators;
		this.havingOperators = havingOperators;
	}

	/**
	 * SELECT 句を記述します。
	 * @param function {@link SelectOfferFunction}
	 */
	public void SELECT(SelectOfferFunction<S> function) {
		Offers<ColumnExpression> offers = function.apply(select);

		if (rowMode) {
			if (optimizerForSelect == null)
				optimizerForSelect = new RuntimeOptimizer(table);

			offers.get().forEach(c -> c.accept(optimizerForSelect));

			optimizer = optimizerForSelect;
		} else {
			if (selectClause == null)
				selectClause = new SelectClause();
			offers.get().forEach(c -> c.accept(selectClause));
		}
	}

	/**
	 * DISTINCT を使用した SELECT 句を記述します。
	 * @param function {@link SelectOfferFunction}
	 */
	public void SELECT_DISTINCT(
		SelectOfferFunction<S> function) {
		quitRowMode();

		Offers<ColumnExpression> offers = function.apply(select);

		SelectDistinctClause mySelectClause = new SelectDistinctClause();
		offers.get().forEach(c -> c.accept(mySelectClause));
		selectClause = mySelectClause;
	}

	/**
	 * COUNT(*) を使用した SELECT 句を記述します。
	 */
	public void SELECT_COUNT() {
		quitRowMode();
		selectClause = new SelectCountClause();
	}

	/**
	 * GROUP BY 句を記述します。
	 * @param function {@link GroupByOfferFunction}
	 */
	public void GROUP_BY(
		GroupByOfferFunction<G> function) {
		function.apply(groupBy).get().forEach(o -> o.offer());
	}

	/**
	 * WHERE 句を記述します。
	 * @param consumer {@link Consumer}
	 */
	public void WHERE(
		Consumer<W> consumer) {
		try {
			Criteria contextCriteria = CriteriaFactory.create();
			QueryCriteriaContext.setContextCriteria(contextCriteria);

			consumer.accept(whereOperators.defaultOperator());

			whereClause().and(contextCriteria);
		} finally {
			QueryCriteriaContext.removeContextCriteria();
		}
	}

	/**
	 * HAVING 句を記述します。
	 * @param consumer {@link Consumer}
	 */
	public void HAVING(
		Consumer<H> consumer) {
		try {
			Criteria contextCriteria = CriteriaFactory.create();
			QueryCriteriaContext.setContextCriteria(contextCriteria);

			consumer.accept(havingOperators.defaultOperator());

			havingClause().and(contextCriteria);
		} finally {
			QueryCriteriaContext.removeContextCriteria();
		}
	}

	/**
	 * ORDER BY 句を記述します。
	 * @param function {@link OrderByOfferFunction}
	 */
	public void ORDER_BY(
		OrderByOfferFunction<O> function) {
		function.apply(orderBy).get().forEach(o -> o.offer());
	}

	public void UNION(ComposedSQL query) {
		quitRowMode();
		unionOperator = UnionOperator.UNION;
		this.union = query;
	}

	public void UNION_ALL(ComposedSQL query) {
		quitRowMode();
		unionOperator = UnionOperator.UNION_ALL;
		this.union = query;
	}

	public void and(Criteria whereClause) {
		whereClause().and(whereClause);
	}

	public void or(Criteria whereClause) {
		whereClause().or(whereClause);
	}

	/**
	 * WHERE 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 */
	public Criteria createWhereCriteria(
		Consumer<W> consumer) {
		try {
			Criteria criteria = CriteriaFactory.create();
			QueryCriteriaContext.setContextCriteria(criteria);

			consumer.accept(whereOperators.defaultOperator());

			return criteria;
		} finally {
			QueryCriteriaContext.removeContextCriteria();
		}
	}

	/**
	 * HAVING 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 */
	public Criteria createHavingCriteria(
		Consumer<H> consumer) {
		try {
			Criteria criteria = CriteriaFactory.create();
			QueryCriteriaContext.setContextCriteria(criteria);

			consumer.accept(havingOperators.defaultOperator());

			return criteria;
		} finally {
			QueryCriteriaContext.removeContextCriteria();
		}
	}

	public void quitRowMode() {
		rowMode = false;
	}

	public boolean rowMode() {
		return rowMode;
	}

	public void checkRowMode() {
		if (!rowMode()) throw new IllegalStateException("集計モードでは実行できない処理です");
	}

	public Optimizer getOptimizer() {
		if (optimizer != null) return optimizer;
		optimizer = new SimpleOptimizer(table);
		return optimizer;
	}

	public void setOptimizer(Optimizer optimizer) {
		this.optimizer = optimizer;
	}

	public SelectClause getSelectClause() {
		return selectClause;
	}

	/**
	 * @return WHERE
	 */
	public Criteria getWhereClause() {
		return whereClause;
	}

	public boolean hasWhereClause() {
		return whereClause != null && whereClause.isAvailable();
	}

	/**
	 * @return GROUP BY
	 */
	public GroupByClause getGroupByClause() {
		if (groupByClause == null) groupByClause = new GroupByClause();
		return groupByClause;
	}

	/**
	 * @param groupByClause GROUP BY
	 */
	public void setGroupByClause(GroupByClause groupByClause) {
		if (groupByClause != null)
			throw new IllegalStateException("既に GROUP BY 句がセットされています");

		quitRowMode();
		this.groupByClause = groupByClause;
	}

	/**
	 * @return HAVING
	 */
	public Criteria getHavingClause() {
		return havingClause;
	}

	/**
	 * @return ORDER BY
	 */
	public OrderByClause getOrderByClause() {
		if (orderByClause == null) orderByClause = new OrderByClause();
		return orderByClause;
	}

	/**
	 * @param orderByClause ORDER BY
	 */
	public void setOrderByClause(OrderByClause orderByClause) {
		if (orderByClause != null)
			throw new IllegalStateException("既に ORDER BY 句がセットされています");
		this.orderByClause = orderByClause;
	}

	/**
	 * 現在保持している WHERE 句をリセットします。
	 */
	public void resetWhere() {
		whereClause = null;
	}

	/**
	 * 現在保持している HAVING 句をリセットします。
	 */
	public void resetHaving() {
		havingClause = null;
	}

	/**
	 * 現在保持している SELECT 句をリセットします。
	 */
	public void resetSelect() {
		optimizer = null;
		optimizerForSelect = null;
		selectClause = null;
	}

	/**
	 * 現在保持している GROUP BY 句をリセットします。
	 */
	public void resetGroupBy() {
		groupByClause = null;
	}

	public void resetUnion() {
		unionOperator = null;
		union = null;
	}

	/**
	 * 現在保持している ORDER BY 句をリセットします。
	 */
	public void resetOrderBy() {
		orderByClause = null;
	}

	/**
	 * 現在保持している条件、並び順をリセットします。
	 */
	public void reset() {
		resetSelect();
		whereClause = null;
		havingClause = null;
		groupByClause = null;
		resetUnion();
		orderByClause = null;
		rowMode = true;
	}

	/**
	 * @param options 検索オプション
	 * @return {@link ComposedSQL}
	 */
	public ComposedSQL composeSQL(Effector[] options) {
		if (rowMode) {
			return new DataAccessHelper().getSelector(
				getOptimizer(),
				whereClause,
				orderByClause,
				options).composeSQL();
		}

		return buildBuilder(options);
	}

	/**
	 * @return {@link QueryBuilder}
	 */
	public Subquery toSubquery(Effector[] options) {
		return new Subquery(buildBuilder(options));
	}

	/**
	 * @param consumer {@link Consumer}
	 */
	public void aggregate(Consumer<BlenResultSet> consumer) {
		ComposedSQL sql = buildBuilder();
		BlenConnection connection = BlendeeManager.getConnection();
		try (BlenStatement statement = connection.getStatement(sql)) {
			try (BlenResultSet result = statement.executeQuery()) {
				consumer.accept(result);
			}
		}
	}

	/**
	 * @param function {@link Function}
	 */
	public <T> T aggregateAndGet(Function<BlenResultSet, T> function) {
		ComposedSQL sql = buildBuilder();
		BlenConnection connection = BlendeeManager.getConnection();
		try (BlenStatement statement = connection.getStatement(sql)) {
			try (BlenResultSet result = statement.executeQuery()) {
				return function.apply(result);
			}
		}
	}

	/**
	 * @param options 検索オプション
	 * @param consumer {@link Consumer}
	 */
	public void aggregate(Effectors options, Consumer<BlenResultSet> consumer) {
		ComposedSQL sql = buildBuilder(options.get());
		BlenConnection connection = BlendeeManager.getConnection();
		try (BlenStatement statement = connection.getStatement(sql)) {
			try (BlenResultSet result = statement.executeQuery()) {
				consumer.accept(result);
			}
		}
	}

	/**
	 * @param options 検索オプション
	 * @param function {@link Function}
	 */
	public <T> T aggregateAndGet(Effectors options, Function<BlenResultSet, T> function) {
		ComposedSQL sql = buildBuilder(options.get());
		BlenConnection connection = BlendeeManager.getConnection();
		try (BlenStatement statement = connection.getStatement(sql)) {
			try (BlenResultSet result = statement.executeQuery()) {
				return function.apply(result);
			}
		}
	}

	/**
	 * @param options 検索オプション
	 * @return {@link ResultSetIterator}
	 */
	public ResultSetIterator aggregate(Effector... options) {
		ComposedSQL sql = buildBuilder(options);
		return new ResultSetIterator(sql);
	}

	private QueryBuilder buildBuilder(Effector... effectors) {
		QueryBuilder builder = new QueryBuilder(new FromClause(table));

		if (selectClause != null) {
			builder.setSelectClause(selectClause);
		} else if (optimizer != null) {
			builder.setSelectClause(optimizer.getOptimizedSelectClause());
		} else {
			throw new IllegalStateException("検索のための SELECT 句が準備されていません");
		}

		if (groupByClause != null) builder.setGroupByClause(groupByClause);
		if (whereClause != null) builder.setWhereClause(whereClause);
		if (havingClause != null) builder.setHavingClause(havingClause);
		if (union != null) builder.union(unionOperator, union);
		if (orderByClause != null) builder.setOrderByClause(orderByClause);

		builder.addEffector(effectors);

		return builder;
	}

	private Criteria whereClause() {
		if (whereClause == null) whereClause = CriteriaFactory.create();
		return whereClause;
	}

	private Criteria havingClause() {
		if (havingClause == null) havingClause = CriteriaFactory.create();
		return havingClause;
	}
}
