package org.blendee.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.blendee.internal.U;
import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.BlendeeContext;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.QueryOption;
import org.blendee.selector.Optimizer;
import org.blendee.sql.Bindable;
import org.blendee.sql.BindableConverter;
import org.blendee.sql.Column;
import org.blendee.sql.Condition;
import org.blendee.sql.ConditionFactory;
import org.blendee.sql.FromClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.QueryBuilder;
import org.blendee.sql.Relationship;
import org.blendee.sql.SelectClause;
import org.blendee.sql.OrderByClause.DirectionalColumn;

/**
 * 検索条件と並び替え条件を保持した、実際に検索を行うためのクラスです。
 * <br>
 * {@link Executor} との違いは、参照する側のテーブルの {@link Query} を使用し、参照される側を辿り、そこで検索することで {@link Row} を一対多で取得することができるようにするということです。
 *
 * @author 千葉 哲嗣
 *
 * @param <O> One　一対多の一側の型
 * @param <M> Many　一対多の多側の型連鎖
 */
public class OneToManyExecutor<O extends Row, M>
	implements Executor<Many<O, M>, Optional<One<O, M>>> {

	private final QueryRelationship self;

	private final Optimizer optimizer;

	private final Condition condition;

	private final OrderByClause order;

	private final DataAccessHelper helper = new DataAccessHelper();

	private final LinkedList<QueryRelationship> route;

	/**
	 * 自動生成されたサブクラス用のコンストラクタです。
	 *
	 * @param relation 中心となるテーブルを表す
	 */
	protected OneToManyExecutor(QueryRelationship relation) {
		self = relation;
		route = new LinkedList<>();
		QueryRelationship root = getRoot(relation, route);
		order = convertOrderByClause(route, root.getOrderByClause());
		optimizer = root.getOptimizer();
		condition = root.getWhereClause();

		//1->n順をn->1順に変える
		Collections.reverse(route);
	}

	@Override
	public Many<O, M> execute() {
		return new Many<>(
			new DataObjectManager(helper.getDataObjects(optimizer, condition, order, null, null), route),
			null,
			self,
			route);
	}

	@Override
	public Many<O, M> execute(QueryOption... options) {
		return new Many<>(
			new DataObjectManager(helper.getDataObjects(optimizer, condition, order, options), route),
			null,
			self,
			route);
	}

	@Override
	public Optional<One<O, M>> willUnique() {
		return getUnique(execute());
	}

	@Override
	public Optional<One<O, M>> willUnique(QueryOption... options) {
		return getUnique(execute(options));
	}

	@Override
	public Optional<One<O, M>> fetch(String... primaryKeyMembers) {
		return fetch(BindableConverter.convert(primaryKeyMembers));
	}

	@Override
	public Optional<One<O, M>> fetch(Number... primaryKeyMembers) {
		return fetch(BindableConverter.convert(primaryKeyMembers));
	}

	@Override
	public Optional<One<O, M>> fetch(Bindable... primaryKeyMembers) {
		return fetch(QueryOptions.EMPTY_OPTIONS, primaryKeyMembers);
	}

	@Override
	public Optional<One<O, M>> fetch(QueryOptions options, String... primaryKeyMembers) {
		return fetch(options, BindableConverter.convert(primaryKeyMembers));
	}

	@Override
	public Optional<One<O, M>> fetch(QueryOptions options, Number... primaryKeyMembers) {
		return fetch(options, BindableConverter.convert(primaryKeyMembers));
	}

	@Override
	public Optional<One<O, M>> fetch(QueryOptions options, Bindable... primaryKeyMembers) {
		Column[] columns = self.getRelationship().getPrimaryKeyColumns();

		if (columns.length != primaryKeyMembers.length)
			throw new IllegalArgumentException("primaryKeyMembers の数が正しくありません");

		Condition condition = ConditionFactory.createCondition();
		for (int i = 0; i < columns.length; i++) {
			condition.and(columns[i].getCondition(primaryKeyMembers[i]));
		}

		return getUnique(
			new Many<>(
				new DataObjectManager(
					helper.getDataObjects(
						optimizer,
						condition,
						order,
						QueryOptions.care(options).get()),
					route),
				null,
				self,
				route));
	}

	@Override
	public int count() {
		QueryBuilder builder = new QueryBuilder(new FromClause(optimizer.getTablePath()));

		builder.setSelectClause(createCountClause(self.getRelationship().getPrimaryKeyColumns()));

		if (condition != null) builder.setWhereClause(condition);
		try (BStatement statement = BlendeeContext.get(BlendeeManager.class)
			.getConnection()
			.getStatement(builder.toString(), builder)) {
			try (BResultSet result = statement.executeQuery()) {
				result.next();
				return result.getInt(1);
			}
		}
	}

	@Override
	public Condition getCondition() {
		return condition;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private static QueryRelationship getRoot(QueryRelationship relation, List<QueryRelationship> relations) {
		relations.add(relation);
		QueryRelationship parent = relation.getParent();
		if (parent == null) return relation;
		return getRoot(parent, relations);
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

	private Optional<One<O, M>> getUnique(Many<O, M> many) {
		if (!many.hasNext()) return Optional.empty();

		One<O, M> one = many.next();

		if (many.hasNext()) throw new NotUniqueException();

		return Optional.of(one);
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
}
