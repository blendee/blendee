package org.blendee.support;

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
import org.blendee.sql.Column;
import org.blendee.sql.ComplementerValues;
import org.blendee.sql.Criteria;
import org.blendee.sql.FromClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.OrderByClause.DirectionalColumn;
import org.blendee.sql.SelectStatementBuilder;
import org.blendee.sql.Relationship;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.SelectClause;

/**
 * 検索条件と並び替え条件を保持した、実際に検索を行うためのクラスです。<br>
 * {@link Query} との違いは、参照する側のテーブルの {@link QueryBuilder} を使用し、参照される側を辿り、そこで検索することで {@link Row} を一対多で取得することができるようにするということです。
 * @author 千葉 哲嗣
 * @param <O> One 一対多の一側の型
 * @param <M> Many 一対多の多側の型連鎖
 */
public class InstantOneToManyQuery<O extends Row, M>
	extends OneToManyQuery<O, M> {

	private final TableFacadeRelationship self;

	private final TableFacadeRelationship root;

	private final Optimizer optimizer;

	private final Criteria criteria;

	private final OrderByClause order;

	private final DataAccessHelper helper = new DataAccessHelper();

	private final List<TableFacadeRelationship> route;

	private final SQLDecorator[] options;

	private ComposedSQL composedSQL;

	/**
	 * 自動生成されたサブクラス用のコンストラクタです。
	 * @param relation 中心となるテーブルを表す
	 * @param options {@link SQLDecorator}
	 */
	public InstantOneToManyQuery(TableFacadeRelationship relation, SQLDecorator[] options) {
		super(relation);

		self = relation;

		List<TableFacadeRelationship> route = new LinkedList<>();

		root = getRoot(relation, route);

		TableFacadeRelationship root = root();
		order = convertOrderByClause(route, root.getOrderByClause());
		optimizer = convertOptimizer(route, root);
		criteria = root.getWhereClause();

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
		return BlendeeManager.getConnection().getStatement(toCountSQL());
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	@Override
	List<TableFacadeRelationship> route() {
		return route;
	}

	@Override
	TableFacadeRelationship root() {
		return root;
	};

	@Override
	DataObjectIterator iterator() {
		return helper.getDataObjects(
			optimizer,
			criteria,
			order,
			options);
	}

	private static TableFacadeRelationship getRoot(TableFacadeRelationship relation, List<TableFacadeRelationship> relations) {
		relations.add(relation);
		TableFacadeRelationship parent = relation.getParent();
		if (parent == null) return relation;
		return getRoot(parent, relations);
	}

	private static Optimizer convertOptimizer(List<TableFacadeRelationship> route, TableFacadeRelationship root) {
		Set<Column> selectColumns = new LinkedHashSet<>();
		SelectClause select = root.getOptimizer().getOptimizedSelectClause();
		Arrays.stream(select.getColumns()).forEach(c -> selectColumns.add(c));
		route.forEach(r -> {
			for (Column column : r.getRelationship().getPrimaryKeyColumns()) {
				selectColumns.add(column);
			}
		});

		RuntimeOptimizer optimizer = new RuntimeOptimizer(root.getOptimizer().getTablePath());
		selectColumns.forEach(c -> optimizer.add(c));

		return optimizer;
	}

	private static OrderByClause convertOrderByClause(List<TableFacadeRelationship> route, OrderByClause order) {
		LinkedList<TableFacadeRelationship> relations = new LinkedList<>(route);
		relations.removeLast();

		OrderByClause newOrder = new OrderByClause();

		List<DirectionalColumn> list = Arrays.asList(order.getDirectionalColumns());

		Map<Column, DirectionalColumn> map = new LinkedHashMap<>();
		list.forEach(column -> map.put(column.getColumn(), column));

		for (TableFacadeRelationship facadeRelation : relations) {
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

	private static SelectClause createCountClause(Column[] columns) {
		List<String> parts = new LinkedList<>();
		for (int i = 0; i < columns.length; i++) {
			parts.add("{" + i + "}");
		}

		SelectClause select = new SelectClause();

		select.add("COUNT(DISTINCT(" + String.join(", ", parts) + "))", columns);

		return select;
	}

	@Override
	public ComposedSQL toCountSQL() {
		SelectStatementBuilder builder = new SelectStatementBuilder(new FromClause(optimizer.getTablePath()));

		builder.setSelectClause(createCountClause(self.getRelationship().getPrimaryKeyColumns()));

		if (criteria != null) builder.setWhereClause(criteria);

		return builder;
	}

	@Override
	public OneToManyQuery<O, M> reproduce(Object... placeHolderValues) {
		return new PlaybackOneToManyQuery<>(
			self,
			root,
			route,
			sql(),
			toCountSQL().sql(),
			new ComplementerValues(composedSQL()).reproduce(placeHolderValues),
			optimizer.getOptimizedSelectClause().getColumns());
	}

	@Override
	public String sql() {
		return composedSQL().sql();
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		return composedSQL().complement(done, statement);
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
