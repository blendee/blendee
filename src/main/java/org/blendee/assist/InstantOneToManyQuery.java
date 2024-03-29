package org.blendee.assist;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.blendee.internal.U;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObjectIterator;
import org.blendee.orm.SelectContext;
import org.blendee.orm.SimpleSelectContext;
import org.blendee.sql.Binder;
import org.blendee.sql.Column;
import org.blendee.sql.ComplementerValues;
import org.blendee.sql.Criteria;
import org.blendee.sql.FromClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.OrderByClause.DirectionalColumn;
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

	private final SelectContext optimizer;

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

		var route = new LinkedList<OneToManyBehavior>();

		root = getRoot(self, route);

		id = self.getRuntimeId();

		var select = relation.getSelectStatement();
		order = convertOrderByClause(id, route, select.getOrderByClause());
		optimizer = convertOptimizer(select.getSelectContext(), id, route, root);
		criteria = select.getWhereClause();

		this.options = options;

		//1->n順をn->1順に変える
		Collections.reverse(route);

		this.route = Collections.unmodifiableList(route);

		composedSQL = helper.buildSQLQueryBuilder(optimizer, criteria, order, options);
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
		var parent = relation.getParent();
		if (parent == null) return relation;
		return getRoot(parent, relations);
	}

	private static SelectContext convertOptimizer(SelectContext optimizer, RuntimeId id, List<OneToManyBehavior> route, OneToManyBehavior root) {
		var selectColumns = new LinkedHashSet<Column>();
		var select = optimizer.selectClause();
		Arrays.stream(select.getColumns()).forEach(c -> selectColumns.add(c));
		route.forEach(r -> {
			for (Column column : r.getRelationship().getPrimaryKeyColumns()) {
				selectColumns.add(column);
			}
		});

		var runtimeOptimizer = new SimpleSelectContext(optimizer.tablePath(), id);
		selectColumns.forEach(c -> runtimeOptimizer.add(c));

		return runtimeOptimizer;
	}

	private static OrderByClause convertOrderByClause(RuntimeId id, List<OneToManyBehavior> route, OrderByClause order) {
		var relations = new LinkedList<OneToManyBehavior>(route);
		relations.removeLast();

		var newOrder = new OrderByClause(id);

		var list = Arrays.asList(order.getDirectionalColumns());

		var map = new LinkedHashMap<Column, DirectionalColumn>();
		list.forEach(column -> map.put(column.getColumn(), column));

		for (var facadeRelation : relations) {
			var relation = facadeRelation.getRelationship();
			var pks = new LinkedHashSet<>(Arrays.asList(relation.getPrimaryKeyColumns()));

			list
				.stream()
				.filter(column -> column.getColumn().getRelationship().equals(relation))
				.map(column -> {
					newOrder.add(column);
					var include = column.getColumn();
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
		var parts = new LinkedList<String>();
		for (int i = 0; i < columns.length; i++) {
			parts.add("{" + i + "}");
		}

		var select = new SelectClause(id);

		select.add("COUNT(DISTINCT(" + String.join(", ", parts) + "))", columns);

		return select;
	}

	@Override
	public ComposedSQL countSQL() {
		var id = optimizer.runtimeId();
		var builder = new SQLQueryBuilder(new FromClause(optimizer.tablePath(), id));

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
			optimizer.selectClause().getColumns());
	}

	@Override
	public OneToManyQuery<O, M> reproduce() {
		return new PlaybackOneToManyQuery<>(
			self,
			route,
			sql(),
			countSQL().sql(),
			ComplementerValues.of(composedSQL()),
			optimizer.selectClause().getColumns());
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
			composedSQL = new DataAccessHelper().buildSQLQueryBuilder(
				optimizer,
				criteria,
				order,
				options);
		}

		return composedSQL;
	}
}
