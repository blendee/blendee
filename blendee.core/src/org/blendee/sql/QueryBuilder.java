package org.blendee.sql;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.jdbc.ComposedSQL;

/**
 * SQL の SELECT 文を生成するクラスです。
 * @author 千葉 哲嗣
 */
public class QueryBuilder implements ComposedSQL {

	private final FromClause fromClause;

	private final List<Effector> effectors = new LinkedList<>();

	private SelectClause selectClause = new SelectAllColumnClause();

	private Criteria whereClause = CriteriaFactory.create();

	private GroupByClause groupClause = new GroupByClause();

	private Criteria havingClause = CriteriaFactory.create();

	private OrderByClause orderClause = new OrderByClause();

	private String query;

	/**
	 * {@link FromClause} が表すテーブルに対する SELECT 文を生成するインスタンスを生成します。
	 * @param fromClause FROM 句
	 */
	public QueryBuilder(FromClause fromClause) {
		this.fromClause = fromClause.replicate();
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
	 * WHERE 句を設定します。
	 * @param clause WHERE 句
	 */
	public synchronized void setWhereClause(Criteria clause) {
		whereClause = prepareCriteria(whereClause, clause);
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
		havingClause = prepareCriteria(havingClause, clause);
	}

	/**
	 * 現在設定されている HAVING 句を返します。
	 * @return HAVING 句
	 */
	public synchronized Criteria getHavingClause() {
		return havingClause.replicate();
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
	 * 現在設定されている各句から SELECT 文を生成し返します。
	 * @return SELECT 文
	 */
	@Override
	public synchronized String sql() {
		if (query == null) {
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
			boolean joined = fromClause.isJoined();

			havingClause.setKeyword("HAVING");
			whereClause.setKeyword("WHERE");

			List<String> clauses = new ArrayList<String>(6);
			addClause(clauses, selectClause.toString(joined));
			addClause(clauses, fromClause.toString());
			addClause(clauses, whereClause.toString(joined));
			addClause(clauses, groupClause.toString(joined));
			addClause(clauses, havingClause.toString(joined));
			addClause(clauses, orderClause.toString(joined));
			query = String.join(" ", clauses).trim();
		}

		String currentQuery = query;
		for (Effector effector : effectors) {
			currentQuery = effector.effect(currentQuery);
		}

		return currentQuery;
	}

	@Override
	public synchronized void complement(BlenPreparedStatement statement) {
		CountingPreparedStatement counter = new CountingPreparedStatement(statement);
		whereClause.getComplementer().complement(counter);
		havingClause.getComplementer(counter.getComplementedCount()).complement(statement);
	}

	@Override
	public String toString() {
		return sql();
	}

	private Criteria prepareCriteria(Criteria oldCriteria, Criteria newCriteria) {
		if (oldCriteria.equalsWithoutBinders(newCriteria)) {
			oldCriteria.changeBinders(newCriteria.getBinders());
			return oldCriteria;
		}

		query = null;
		return newCriteria.replicate();
	}

	private static void addClause(List<String> list, String clause) {
		clause = clause.trim();
		if (clause.length() == 0) return;
		list.add(clause);
	}

	private class SelectAllColumnClause extends SelectClause {

		@Override
		public String toString(boolean joining) {
			List<Relationship> relationships = new LinkedList<>();
			fromClause.addUsingRelationshipsTo(relationships);
			for (Relationship relationship : relationships) {
				for (Column column : relationship.getColumns()) {
					add(column);
				}
			}

			return super.toString(joining);
		}
	}
}
