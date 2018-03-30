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
import org.blendee.sql.Effector;
import org.blendee.sql.FromClause;
import org.blendee.sql.GroupByClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.QueryBuilder;
import org.blendee.sql.SelectClause;
import org.blendee.sql.SelectCountClause;
import org.blendee.sql.SelectDistinctClause;

/**
 * {@link Query} の内部処理を定義したヘルパークラスです。
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public class QueryHelper<S extends QueryRelationship, G extends QueryRelationship, W, H, O extends QueryRelationship> {

	private final TablePath table;

	private final S select;

	private final G groupBy;

	private final O orderBy;

	private final LogicalOperators<W> whereOperators;

	private final LogicalOperators<H> havingOperators;

	private boolean rowMode = true;

	private SelectOfferFunction<S> selectClauseFunction;

	private GroupByOfferFunction<G> groupByClauseFunction;

	private OrderByOfferFunction<O> orderByClauseFunction;

	private Consumer<W> whereClauseConsumer;

	private Consumer<H> havingClauseConsumer;

	private Optimizer optimizer;

	private SelectClause selectClause;

	private Criteria whereClause;

	private GroupByClause groupByClause;

	private Criteria havingClause;

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
		if (selectClauseFunction == function) return;

		Offers<ColumnExpression> offers = function.apply(select);

		if (rowMode) {
			RuntimeOptimizer myOptimizer = new RuntimeOptimizer(table);
			offers.get().forEach(c -> c.accept(myOptimizer));
			optimizer = myOptimizer;
		} else {
			SelectClause mySelectClause = new SelectClause();
			offers.get().forEach(c -> c.accept(mySelectClause));
			selectClause = mySelectClause;
		}

		selectClauseFunction = function;
	}

	/**
	 * DISTINCT を使用した SELECT 句を記述します。
	 * @param function {@link SelectOfferFunction}
	 */
	public void SELECT_DISTINCT(
		SelectOfferFunction<S> function) {
		if (selectClauseFunction == function) return;

		quitRowMode();

		Offers<ColumnExpression> offers = function.apply(select);

		SelectDistinctClause mySelectClause = new SelectDistinctClause();
		offers.get().forEach(c -> c.accept(mySelectClause));
		selectClause = mySelectClause;

		selectClauseFunction = function;
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
		if (groupByClauseFunction == function) return;

		function.apply(groupBy).get().forEach(o -> o.offer());
		groupByClauseFunction = function;
	}

	/**
	 * WHERE 句を記述します。
	 * @param consumer {@link Consumer}
	 */
	public void WHERE(
		Consumer<W> consumer) {
		if (whereClauseConsumer == consumer) return;

		consumer.accept(whereOperators.AND());
		whereClauseConsumer = consumer;
	}

	/**
	 * HAVING 句を記述します。
	 * @param consumer {@link Consumer}
	 */
	public void HAVING(
		Consumer<H> consumer) {
		if (havingClauseConsumer == consumer) return;

		consumer.accept(havingOperators.AND());
		havingClauseConsumer = consumer;
	}

	/**
	 * ORDER BY 句を記述します。
	 * @param function {@link OrderByOfferFunction}
	 */
	public void ORDER_BY(
		OrderByOfferFunction<O> function) {
		if (orderByClauseFunction == function) return;

		function.apply(orderBy).get().forEach(o -> o.offer());
		orderByClauseFunction = function;
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

	/**
	 * @param whereClause WHERE
	 */
	public void setWhereClause(Criteria whereClause) {
		this.whereClause = whereClause;
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
	 * @param havingClause HAVING
	 */
	public void setHavingClause(Criteria havingClause) {
		this.havingClause = havingClause;
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
		whereClauseConsumer = null;
	}

	/**
	 * 現在保持している HAVING 句をリセットします。
	 */
	public void resetHaving() {
		havingClause = null;
		havingClauseConsumer = null;
	}

	/**
	 * 現在保持している SELECT 句をリセットします。
	 */
	public void resetSelect() {
		optimizer = null;
		selectClauseFunction = null;
	}

	/**
	 * 現在保持している GROUP BY 句をリセットします。
	 */
	public void resetGroupBy() {
		groupByClause = null;
		groupByClauseFunction = null;
	}

	/**
	 * 現在保持している ORDER BY 句をリセットします。
	 */
	public void resetOrderBy() {
		orderByClause = null;
		orderByClauseFunction = null;
	}

	/**
	 * 現在保持している条件、並び順をリセットします。
	 */
	public void reset() {
		optimizer = null;
		selectClause = null;
		whereClause = null;
		havingClause = null;
		groupByClause = null;
		orderByClause = null;
		selectClauseFunction = null;
		groupByClauseFunction = null;
		orderByClauseFunction = null;
		whereClauseConsumer = null;
		havingClauseConsumer = null;
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

		return aggregateInternal(options);
	}

	/**
	 * @param consumer {@link Consumer}
	 */
	public void aggregate(Consumer<BlenResultSet> consumer) {
		ComposedSQL sql = aggregateInternal();
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
		ComposedSQL sql = aggregateInternal();
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
		ComposedSQL sql = aggregateInternal(options.get());
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
		ComposedSQL sql = aggregateInternal(options.get());
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
		ComposedSQL sql = aggregateInternal(options);
		return new ResultSetIterator(sql);
	}

	private ComposedSQL aggregateInternal(Effector... effectors) {
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
		if (orderByClause != null) builder.setOrderByClause(orderByClause);

		builder.addEffector(effectors);

		return builder;
	}
}
