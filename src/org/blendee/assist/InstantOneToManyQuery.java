package org.blendee.assist;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.blendee.internal.U;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObjectIterator;
import org.blendee.selector.Optimizer;
import org.blendee.selector.RuntimeOptimizer;
import org.blendee.selector.Selector;
import org.blendee.sql.Binder;
import org.blendee.sql.Column;
import org.blendee.sql.ComplementerValues;
import org.blendee.sql.Criteria;
import org.blendee.sql.FromClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.OrderByClause.DirectionalColumn;
import org.blendee.sql.Relationship;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.SQLQueryBuilder;
import org.blendee.sql.SelectClause;

/**
 * 検索条件と並び替え条件を保持した、実際に検索を行うためのクラスです。<br>
 * {@link Query} との違いは、参照する側のテーブルの {@link SelectStatement} を使用し、参照される側を辿り、そこで検索することで {@link Row} を一対多で取得することができるようにするということです。
 * @author 千葉 哲嗣
 * @param <O> One 一対多の一側の型
 * @param <M> Many 一対多の多側の型連鎖
 */
public class InstantOneToManyQuery<O extends Row, M>
	extends OneToManyQuery<O, M> {

	private final OneToManyBehavior self;

	private final OneToManyBehavior root;

	private final RuntimeId id;

	private final Optimizer optimizer;

	private final Criteria criteria;

	private final OrderByClause order;

	private final DataAccessHelper helper = new DataAccessHelper();

	private final List<OneToManyBehavior> route;

	private final SQLDecorator[] options;

	private ComposedSQL composedSQL;

	/**
	 * 自動生成されたサブクラス用のコンストラクタです。
	 * @param relation 中心となるテーブルを表す
	 * @param options {@link SQLDecorator}
	 */
	public InstantOneToManyQuery(TableFacadeAssist relation, SQLDecorator[] options) {
		super(relation.getOneToManyBehavior());

		self = self();

		List<OneToManyBehavior> route = new LinkedList<>();

		root = getRoot(self, route);

		id = self.getRuntimeId();

		SelectStatement select = relation.getSelectStatement();
		order = convertOrderByClause(id, route, select.getOrderByClause());
		optimizer = convertOptimizer(select.getOptimizer(), id, route, root);
		criteria = select.getWhereClause();

		this.options = options;

		//1->n順をn->1順に変える
		Collections.reverse(route);

		this.route = Collections.unmodifiableList(route);

		Selector selector = helper.getSelector(
			optimizer,
			criteria,
			order,
			options);

		composedSQL = selector.composeSQL();
	}

	@Override
	BStatement createStatementForCount() {
		return BlendeeManager.getConnection().getStatement(countSQL());
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	@Override
	List<OneToManyBehavior> route() {
		return route;
	}

	@Override
	DataObjectIterator iterator() {
		return helper.getDataObjects(
			optimizer,
			criteria,
			order,
			options);
	}

	private static OneToManyBehavior getRoot(OneToManyBehavior relation, List<OneToManyBehavior> relations) {
		relations.add(relation);
		OneToManyBehavior parent = relation.getParent();
		if (parent == null) return relation;
		return getRoot(parent, relations);
	}

	private static Optimizer convertOptimizer(Optimizer optimizer, RuntimeId id, List<OneToManyBehavior> route, OneToManyBehavior root) {
		Set<Column> selectColumns = new LinkedHashSet<>();
		SelectClause select = optimizer.getOptimizedSelectClause();
		Arrays.stream(select.getColumns()).forEach(c -> selectColumns.add(c));
		route.forEach(r -> {
			for (Column column : r.getRelationship().getPrimaryKeyColumns()) {
				selectColumns.add(column);
			}
		});

		RuntimeOptimizer runtimeOptimizer = new RuntimeOptimizer(optimizer.getTablePath(), id);
		selectColumns.forEach(c -> runtimeOptimizer.add(c));

		return runtimeOptimizer;
	}

	private static OrderByClause convertOrderByClause(RuntimeId id, List<OneToManyBehavior> route, OrderByClause order) {
		LinkedList<OneToManyBehavior> relations = new LinkedList<>(route);
		relations.removeLast();

		OrderByClause newOrder = new OrderByClause(id);

		List<DirectionalColumn> list = Arrays.asList(order.getDirectionalColumns());

		Map<Column, DirectionalColumn> map = new LinkedHashMap<>();
		list.forEach(column -> map.put(column.getColumn(), column));

		for (OneToManyBehavior facadeRelation : relations) {
			Relationship relation = facadeRelation.getRelationship();
			Set<Column> pks = new LinkedHashSet<>(Arrays.asList(relation.getPrimaryKeyColumns()));

			list
				.stream()
				.filter(column -> column.getColumn().getRelationship().equals(relation))
				.map(column -> {
					newOrder.add(column);
					Column include = column.getColumn();
					pks.remove(include);

					return include;
				})
				.forEach(map::remove);

			pks.forEach(newOrder::asc);
		}

		map.forEach((key, value) -> newOrder.add(value));

		return newOrder;
	}

	private static SelectClause createCountClause(RuntimeId id, Column[] columns) {
		List<String> parts = new LinkedList<>();
		for (int i = 0; i < columns.length; i++) {
			parts.add("{" + i + "}");
		}

		SelectClause select = new SelectClause(id);

		select.add("COUNT(DISTINCT(" + String.join(", ", parts) + "))", columns);

		return select;
	}

	@Override
	public ComposedSQL countSQL() {
		RuntimeId id = optimizer.getRuntimeId();
		SQLQueryBuilder builder = new SQLQueryBuilder(new FromClause(optimizer.getTablePath(), id));

		builder.setSelectClause(createCountClause(id, self.getRelationship().getPrimaryKeyColumns()));

		if (criteria != null) builder.setWhereClause(criteria);

		return builder;
	}

	@Override
	public OneToManyQuery<O, M> reproduce(Object... placeHolderValues) {
		return new PlaybackOneToManyQuery<>(
			self,
			route,
			sql(),
			countSQL().sql(),
			ComplementerValues.of(composedSQL()).reproduce(placeHolderValues),
			optimizer.getOptimizedSelectClause().getColumns());
	}

	@Override
	public OneToManyQuery<O, M> reproduce() {
		return new PlaybackOneToManyQuery<>(
			self,
			route,
			sql(),
			countSQL().sql(),
			ComplementerValues.of(composedSQL()),
			optimizer.getOptimizedSelectClause().getColumns());
	}

	@Override
	public Binder[] currentBinders() {
		return ComplementerValues.of(composedSQL()).currentBinders();
	}

	@Override
	public String sql() {
		return composedSQL().sql();
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		return composedSQL().complement(done, statement);
	}

	@Override
	RuntimeId runtimeId() {
		return id;
	}

	private ComposedSQL composedSQL() {
		if (composedSQL == null) {
			Selector selector = new DataAccessHelper().getSelector(
				optimizer,
				criteria,
				order,
				options);

			composedSQL = selector.composeSQL();
		}

		return composedSQL;
	}
}
