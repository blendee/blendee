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
import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.jdbc.BlenResultSet;
import org.blendee.jdbc.BlenStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObjectIterator;
import org.blendee.selector.Optimizer;
import org.blendee.selector.RuntimeOptimizer;
import org.blendee.sql.Column;
import org.blendee.sql.Criteria;
import org.blendee.sql.FromClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.OrderByClause.DirectionalColumn;
import org.blendee.sql.QueryBuilder;
import org.blendee.sql.Relationship;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.SelectClause;

/**
 * 検索条件と並び替え条件を保持した、実際に検索を行うためのクラスです。<br>
 * {@link Executor} との違いは、参照する側のテーブルの {@link Query} を使用し、参照される側を辿り、そこで検索することで {@link Row} を一対多で取得することができるようにするということです。
 * @author 千葉 哲嗣
 * @param <O> One　一対多の一側の型
 * @param <M> Many　一対多の多側の型連鎖
 */
public class InstantOneToManyExecutor<O extends Row, M>
	extends OneToManyExecutor<O, M> {

	private final QueryRelationship self;

	private final Optimizer optimizer;

	private final Criteria criteria;

	private final OrderByClause order;

	private final DataAccessHelper helper = new DataAccessHelper();

	private final LinkedList<QueryRelationship> route;

	private final SQLDecorator[] options;

	/**
	 * 自動生成されたサブクラス用のコンストラクタです。
	 * @param relation 中心となるテーブルを表す
	 * @param options {@link SQLDecorator}
	 */
	protected InstantOneToManyExecutor(QueryRelationship relation, SQLDecorator[] options) {
		super(relation);
		self = relation;
		route = new LinkedList<>();

		QueryRelationship root = root();
		order = convertOrderByClause(route, root.getOrderByClause());
		optimizer = convertOptimizer(route, root);
		criteria = root.getWhereClause();

		this.options = options;

		//1->n順をn->1順に変える
		Collections.reverse(route);
	}

	@Override
	public int count() {
		QueryBuilder builder = new QueryBuilder(new FromClause(optimizer.getTablePath()));

		builder.setSelectClause(createCountClause(self.getRelationship().getPrimaryKeyColumns()));

		if (criteria != null) builder.setWhereClause(criteria);
		try (BlenStatement statement = BlendeeManager
			.getConnection()
			.getStatement(builder)) {
			try (BlenResultSet result = statement.executeQuery()) {
				result.next();
				return result.getInt(1);
			}
		}
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	@Override
	DataObjectIterator getIterator() {
		return helper.getDataObjects(
			optimizer,
			criteria,
			order,
			options);
	}

	private static Optimizer convertOptimizer(List<QueryRelationship> route, QueryRelationship root) {
		Set<Column> selectColumns = new LinkedHashSet<>();
		SelectClause select = root.getOptimizer().getOptimizedSelectClause();
		Arrays.asList(select.getColumns()).forEach(c -> selectColumns.add(c));
		route.forEach(r -> {
			for (Column column : r.getRelationship().getPrimaryKeyColumns()) {
				selectColumns.add(column);
			}
		});

		RuntimeOptimizer optimizer = new RuntimeOptimizer(root.getOptimizer().getTablePath());
		selectColumns.forEach(c -> optimizer.add(c));

		return optimizer;
	}

	private static OrderByClause convertOrderByClause(List<QueryRelationship> route, OrderByClause order) {
		LinkedList<QueryRelationship> relations = new LinkedList<>(route);
		relations.removeLast();

		OrderByClause newOrder = new OrderByClause();

		List<DirectionalColumn> list = Arrays.asList(order.getDirectionalColumns());

		Map<Column, DirectionalColumn> map = new LinkedHashMap<>();
		list.forEach(column -> map.put(column.getColumn(), column));

		for (QueryRelationship queryRelation : relations) {
			Relationship relation = queryRelation.getRelationship();
			Set<Column> pks = new LinkedHashSet<>(Arrays.asList(relation.getPrimaryKeyColumns()));

			list.stream()
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
	public OneToManyExecutor<O, M> reproduce(PreparedStatementComplementer complementer) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public String sql() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public int complement(int done, BlenPreparedStatement statement) {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}
}
