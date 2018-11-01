package org.blendee.sql;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.sql.FromClause.JoinType;

/**
 * SQL の SELECT 文を生成するクラスです。
 * @author 千葉 哲嗣
 */
public class SQLQueryBuilder implements ComposedSQL {

	private final FromClause fromClause;

	private final List<SQLDecorator> decorators = new LinkedList<>();

	private final List<Union> unions = new LinkedList<>();

	private final List<JoinContainer> joins = new LinkedList<>();

	private SelectClause selectClause;

	private Criteria whereClause;

	private GroupByClause groupClause;

	private Criteria havingClause;

	private OrderByClause orderClause;

	private String query;

	/**
	 * 使用可能な UNION キーワードです。
	 */
	public enum UnionOperator {

		/**
		 * UNION
		 */
		UNION("UNION"),

		/**
		 * UNION ALL
		 */
		UNION_ALL("UNION ALL");

		private final String expression;

		private UnionOperator(String expression) {
			this.expression = expression;
		}
	}

	/**
	 * {@link FromClause} が表すテーブルに対する SELECT 文を生成するインスタンスを生成します。
	 * @param fromClause FROM 句
	 */
	public SQLQueryBuilder(FromClause fromClause) {
		this(true, fromClause);
	}

	/**
	 * {@link FromClause} が表すテーブルに対する SELECT 文を生成するインスタンスを生成します。
	 * @param useSelectAsterisk デフォルトで SELCT * とするか
	 * @param fromClause FROM 句
	 */
	public SQLQueryBuilder(boolean useSelectAsterisk, FromClause fromClause) {
		this.fromClause = fromClause.replicate();

		RuntimeId id = fromClause.getRuntimeId();

		selectClause = useSelectAsterisk ? new SelectAsteriskClause() : new SelectClause(id);
		groupClause = new GroupByClause(id);
		orderClause = new OrderByClause(id);

		CriteriaFactory factory = new CriteriaFactory(id);
		whereClause = factory.create();
		havingClause = factory.create();
	}

	/**
	 * 現在設定されている FROM 句を返します。
	 * @return FROM 句
	 */
	public synchronized FromClause getFromClause() {
		return fromClause.replicate();
	}

	/**
	 * SELECT 句を設定します。
	 * @param clause SELECT 句
	 */
	public synchronized void setSelectClause(SelectClause clause) {
		if (selectClause.equals(clause)) return;
		selectClause = clause.replicate();
		query = null;
	}

	/**
	 * 現在設定されている SELECT 句を返します。
	 * @return SELECT 句
	 */
	public synchronized SelectClause getSelectClause() {
		return selectClause.replicate();
	}

	/**
	 * 現在設定されている SELECT 句にカラムが設定されているかどうかを判定します。
	 * @return SELECT 句にカラムが設定されているかどうか
	 */
	public synchronized boolean hasSelectColumns() {
		return selectClause.isValid();
	}

	/**
	 * WHERE 句を設定します。
	 * @param clause WHERE 句
	 */
	public synchronized void setWhereClause(Criteria clause) {
		if (whereClause.equals(clause)) return;
		whereClause = clause.replicate();
		query = null;
	}

	/**
	 * 現在設定されている WHERE 句を返します。
	 * @return WHERE 句
	 */
	public synchronized Criteria getWhereClause() {
		return whereClause.replicate();
	}

	/**
	 * GROUP BY 句を設定します。
	 * @param clause GROUP BY 句
	 */
	public synchronized void setGroupByClause(GroupByClause clause) {
		if (groupClause.equals(clause)) return;
		groupClause = clause.replicate();
		query = null;
	}

	/**
	 * 現在設定されている GROUP BY 句を返します。
	 * @return GROUP BY 句
	 */
	public synchronized GroupByClause getGroupByClause() {
		return groupClause.replicate();
	}

	/**
	 * HAVING 句を設定します。
	 * @param clause HAVING 句
	 */
	public synchronized void setHavingClause(Criteria clause) {
		if (havingClause.equals(clause)) return;
		havingClause = clause.replicate();
		query = null;
	}

	/**
	 * 現在設定されている HAVING 句を返します。
	 * @return HAVING 句
	 */
	public synchronized Criteria getHavingClause() {
		return havingClause.replicate();
	}

	/**
	 * UNION するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param operator UNION の種類
	 * @param query UNION 対象
	 */
	public synchronized void union(UnionOperator operator, ComposedSQL query) {
		unions.add(new Union(operator, query));
		query = null;
	}

	/**
	 * 保持する {@link Union} を返します。
	 * @return {@link Union}
	 */
	public synchronized Union[] getUnions() {
		return unions.toArray(new Union[unions.size()]);
	}

	/**
	 * ORDER BY 句を設定します。
	 * @param clause ORDER BY 句
	 */
	public synchronized void setOrderByClause(OrderByClause clause) {
		if (orderClause.equals(clause)) return;
		orderClause = clause.replicate();
		query = null;
	}

	/**
	 * 現在設定されている ORDER BY 句を返します。
	 * @return ORDER BY 句
	 */
	public synchronized OrderByClause getOrderByClause() {
		return orderClause.replicate();
	}

	/**
	 * {@link SQLDecorator} を設定します。
	 * @param decorators {@link SQLDecorator}
	 */
	public synchronized void addDecorator(SQLDecorator... decorators) {
		for (SQLDecorator decorator : decorators) {
			this.decorators.add(decorator);
		}

		query = null;
	}

	/**
	 * 他のクエリと JOIN します。<br>
	 * このインスタンスがメインの取り込む側になります。
	 * @param joinType {@link JoinType}
	 * @param another 取り込まれる側
	 * @param onCriteria ON 句
	 */
	public synchronized void join(JoinType joinType, SQLQueryBuilder another, Criteria onCriteria) {
		if (unions.size() > 0 || another.unions.size() > 0)
			throw new IllegalArgumentException("UNION されたクエリはマージできません");

		joins.add(new JoinContainer(joinType, another, onCriteria));

		query = null;
	}

	/**
	 * このインスタンスをサブクエリとして使用するかどうかを指定します。<br>
	 * サブクエリで使用すると、すべてのカラムにテーブル ID が補完されます。
	 * @param forSubquery true の場合、サブクエリとして使用
	 */
	public synchronized void forSubquery(boolean forSubquery) {
		fromClause.forSubquery(forSubquery);
		query = null;
	}

	/**
	 * @return 現在の設定値
	 */
	public synchronized boolean forSubquery() {
		return fromClause.forSubquery();
	}

	/**
	 * 現在設定されている各句から SELECT 文を生成し返します。
	 * @return SELECT 文
	 */
	@Override
	public synchronized String sql() {
		prepareForComposedSQL();

		String currentQuery = query;
		for (SQLDecorator decorator : decorators) {
			currentQuery = decorator.decorate(currentQuery);
		}

		return currentQuery;
	}

	@Override
	public synchronized int complement(int done, BPreparedStatement statement) {
		prepareForComposedSQL();

		done = selectClause.complement(done, statement);
		done = fromClause.complement(done, statement);
		done = whereClause.complement(done, statement);
		done = groupClause.complement(done, statement);
		done = havingClause.complement(done, statement);

		for (Union union : unions) {
			done = union.getSQL().complement(done, statement);
		}

		return orderClause.complement(done, statement);
	}

	@Override
	public String toString() {
		return sql();
	}

	private void prepareForComposedSQL() {
		if (query == null) {
			prepareFrom();

			ListClauses listClauses = new ListClauses();
			merge(listClauses);

			listClauses.addSelect(selectClause);
			listClauses.addGroupBy(groupClause);
			listClauses.addOrderBy(orderClause);

			boolean joined = fromClause.isJoined();

			havingClause.setKeyword("HAVING");
			whereClause.setKeyword("WHERE");

			List<String> clauses = new ArrayList<String>();
			addClause(clauses, listClauses.toSelectString(joined));
			addClause(clauses, fromClause.toString());
			addClause(clauses, whereClause.toString(joined));
			addClause(clauses, listClauses.toGroupByString(joined));
			addClause(clauses, havingClause.toString(joined));

			unions.forEach(u -> {
				clauses.add(u.getUnionOperator().expression);
				clauses.add(u.getSQL().sql());
			});

			addClause(clauses, listClauses.toOrderByString(joined));
			query = String.join(" ", clauses).trim();
		}
	}

	private static void addClause(List<String> list, String clause) {
		clause = clause.trim();
		if (clause.length() == 0) return;
		list.add(clause);
	}

	private void prepareFrom() {
		fromClause.clearRelationships();

		Relationship root = fromClause.getRoot();
		selectClause.prepareColumns(root);
		whereClause.prepareColumns(root);
		groupClause.prepareColumns(root);
		havingClause.prepareColumns(root);
		orderClause.prepareColumns(root);

		selectClause.join(fromClause);
		whereClause.join(fromClause);
		groupClause.join(fromClause);
		havingClause.join(fromClause);
		orderClause.join(fromClause);
	}

	private void merge(ListClauses clauses) {
		joins.forEach(c -> {
			c.another.prepareFrom();
			c.another.merge(clauses);

			clauses.addSelect(c.another.selectClause);
			fromClause.join(c.joinType, c.another.fromClause, c.onCriteria);
			whereClause.and(c.another.whereClause);
			clauses.addGroupBy(c.another.groupClause);
			havingClause.and(c.another.havingClause);
			clauses.addOrderBy(c.another.orderClause);
		});
	}

	private class SelectAsteriskClause extends SelectClause {

		private SelectAsteriskClause() {
			super(RuntimeIdFactory.getInstance());
		}

		@Override
		public String toString(boolean joining) {
			return "SELECT *";
		}
	}

	private static class JoinContainer {

		private final JoinType joinType;

		private final SQLQueryBuilder another;

		private final Criteria onCriteria;

		private JoinContainer(JoinType joinType, SQLQueryBuilder another, Criteria onCriteria) {
			this.joinType = joinType;
			this.another = another;
			this.onCriteria = onCriteria;
		}
	}
}
