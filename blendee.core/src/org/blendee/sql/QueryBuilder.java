package org.blendee.sql;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.sql.FromClause.JoinType;

/**
 * SQL の SELECT 文を生成するクラスです。
 * @author 千葉 哲嗣
 */
public class QueryBuilder implements ComposedSQL {

	private final FromClause fromClause;

	private final List<Effector> effectors = new LinkedList<>();

	private final List<UnionContainer> unions = new LinkedList<>();;

	private final List<JoinContainer> joins = new LinkedList<>();;

	private SelectClause selectClause = new SelectAllColumnClause();

	private Criteria whereClause = CriteriaFactory.create();

	private GroupByClause groupClause = new GroupByClause();

	private Criteria havingClause = CriteriaFactory.create();

	private OrderByClause orderClause = new OrderByClause();

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
	public QueryBuilder(FromClause fromClause) {
		this.fromClause = fromClause.replicate();
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
		return selectClause.getColumnsSize() > 0;
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
	 * @param operator
	 * @param query UNION 対象
	 */
	public synchronized void union(UnionOperator operator, ComposedSQL query) {
		unions.add(new UnionContainer(operator, query));
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
	 * {@link Effector} を設定します。
	 * @param effectors {@link Effector}
	 */
	public synchronized void addEffector(Effector... effectors) {
		for (Effector effector : effectors) {
			this.effectors.add(effector);
		}
	}

	/**
	 * 他のクエリと JOIN します。<br>
	 * このインスタンスがメインの取り込む側になります。
	 * @param joinType {@link JoinType}
	 * @param another 取り込まれる側
	 * @param onCriteria ON 句
	 */
	public synchronized void join(JoinType joinType, QueryBuilder another, Criteria onCriteria) {
		if (unions.size() > 0 || another.unions.size() > 0)
			throw new IllegalArgumentException("UNION されたクエリはマージできません");

		query = null;
		joins.add(new JoinContainer(joinType, another, onCriteria));
	}

	/**
	 * 現在設定されている各句から SELECT 文を生成し返します。
	 * @return SELECT 文
	 */
	@Override
	public synchronized String sql() {
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
				clauses.add(u.unionOperator.expression);
				clauses.add(u.query.sql());
			});

			addClause(clauses, listClauses.toOrderByString(joined));
			query = String.join(" ", clauses).trim();
		}

		String currentQuery = query;
		for (Effector effector : effectors) {
			currentQuery = effector.effect(currentQuery);
		}

		return currentQuery;
	}

	@Override
	public synchronized int complement(int done, BlenPreparedStatement statement) {
		done = fromClause.complement(done, statement);
		done = whereClause.complement(done, statement);
		done = havingClause.complement(done, statement);

		for (UnionContainer union : unions) {
			done = union.query.complement(done, statement);
		}

		return done;
	}

	@Override
	public String toString() {
		return sql();
	}

	private static void addClause(List<String> list, String clause) {
		clause = clause.trim();
		if (clause.length() == 0) return;
		list.add(clause);
	}

	private void prepareFrom() {
		fromClause.clearRelationships();

		Relationship root = fromClause.getRoot();

		selectClause.adjustColumns(root);
		whereClause.adjustColumns(root);
		groupClause.adjustColumns(root);
		havingClause.adjustColumns(root);
		orderClause.adjustColumns(root);

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

	private class SelectAllColumnClause extends SelectClause {

		@Override
		public String toString(boolean joining) {
			return "SELECT *";
		}
	}

	private static class UnionContainer {

		private final UnionOperator unionOperator;

		private final ComposedSQL query;

		private UnionContainer(UnionOperator unionOperator, ComposedSQL query) {
			this.unionOperator = unionOperator;
			this.query = query;
		}
	}

	private static class JoinContainer {

		private final JoinType joinType;

		private final QueryBuilder another;

		private final Criteria onCriteria;

		private JoinContainer(JoinType joinType, QueryBuilder another, Criteria onCriteria) {
			this.joinType = joinType;
			this.another = another;
			this.onCriteria = onCriteria;
		}
	}
}
