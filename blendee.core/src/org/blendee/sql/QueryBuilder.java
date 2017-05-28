package org.blendee.sql;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.PreparedStatementComplementer;

/**
 * SQL の SELECT 文を生成するクラスです。
 *
 * @author 千葉 哲嗣
 */
public class QueryBuilder implements PreparedStatementComplementer {

	private final FromClause fromClause;

	private SelectClause selectClause = new SelectAllColumnClause();

	private Condition whereClause = ConditionFactory.createCondition();

	private GroupByClause groupClause = new GroupByClause();

	private Condition havingClause = ConditionFactory.createCondition();

	private OrderByClause orderClause = new OrderByClause();

	private SQLAdjuster adjuster = SQLAdjuster.DISABLED_ADJUSTER;

	private String query;

	/**
	 * {@link FromClause} が表すテーブルに対する SELECT 文を生成するインスタンスを生成します。
	 *
	 * @param fromClause FROM 句
	 */
	public QueryBuilder(FromClause fromClause) {
		this.fromClause = fromClause.replicate();
	}

	/**
	 * SELECT 句を設定します。
	 *
	 * @param clause SELECT 句
	 */
	public synchronized void setSelectClause(SelectClause clause) {
		if (selectClause.equals(clause)) return;
		selectClause = clause.replicate();
		query = null;
	}

	/**
	 * 現在設定されている SELECT 句を返します。
	 *
	 * @return SELECT 句
	 */
	public synchronized SelectClause getSelectClause() {
		return selectClause.replicate();
	}

	/**
	 * WHERE 句を設定します。
	 *
	 * @param clause WHERE 句
	 */
	public synchronized void setWhereClause(Condition clause) {
		whereClause = prepareCondition(whereClause, clause);
	}

	/**
	 * 現在設定されている WHERE 句を返します。
	 *
	 * @return WHERE 句
	 */
	public synchronized Condition getWhereClause() {
		return whereClause.replicate();
	}

	/**
	 * GROUP BY 句を設定します。
	 *
	 * @param clause GROUP BY 句
	 */
	public synchronized void setGroupByClause(GroupByClause clause) {
		if (groupClause.equals(clause)) return;
		groupClause = clause.replicate();
		query = null;
	}

	/**
	 * 現在設定されている GROUP BY 句を返します。
	 *
	 * @return GROUP BY 句
	 */
	public synchronized GroupByClause getGroupByClause() {
		return groupClause.replicate();
	}

	/**
	 * HAVING 句を設定します。
	 *
	 * @param clause HAVING 句
	 */
	public synchronized void setHavingClause(Condition clause) {
		havingClause = prepareCondition(havingClause, clause);
	}

	/**
	 * 現在設定されている HAVING 句を返します。
	 *
	 * @return HAVING 句
	 */
	public synchronized Condition getHavingClause() {
		return havingClause.replicate();
	}

	/**
	 * ORDER BY 句を設定します。
	 *
	 * @param clause ORDER BY 句
	 */
	public synchronized void setOrderByClause(OrderByClause clause) {
		if (orderClause.equals(clause)) return;
		orderClause = clause.replicate();
		query = null;
	}

	/**
	 * 現在設定されている ORDER BY 句を返します。
	 *
	 * @return ORDER BY 句
	 */
	public synchronized OrderByClause getOrderByClause() {
		return orderClause.replicate();
	}

	/**
	 * {@link SQLAdjuster} を設定します。
	 *
	 * @param adjuster {@link SQLAdjuster}
	 */
	public synchronized void setAdjuster(SQLAdjuster adjuster) {
		this.adjuster = adjuster;
		//範囲指定部分を変更しただけのケースがあるので、ここではキャッシュクリアをしない
	}

	/**
	 * 現在設定されている {@link SQLAdjuster} を返します。
	 *
	 * @return {@link SQLAdjuster}
	 */
	public synchronized SQLAdjuster getAdjuster() {
		return adjuster;
	}

	/**
	 * 現在設定されている各句から SELECT 文を生成し返します。
	 *
	 * @return SELECT 文
	 */
	@Override
	public synchronized String toString() {
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

			if (adjuster.canBuildQueryParts()) {
				//SQLAdjuster 自身で組み立てる場合、キャッシュしない
				//SQLAdjuster 内部の値が更新されただけの場合
				//ここを通れないので調節ができないため
				return adjuster.buildQueryParts(
					selectClause.toString(joined).trim(),
					fromClause.toString().trim(),
					whereClause.toString(joined).trim(),
					groupClause.toString(joined).trim(),
					havingClause.toString(joined).trim(),
					orderClause.toString(joined).trim());
			}

			List<String> clauses = new ArrayList<String>(6);
			addClause(clauses, selectClause.toString(joined));
			addClause(clauses, fromClause.toString());
			addClause(clauses, whereClause.toString(joined));
			addClause(clauses, groupClause.toString(joined));
			addClause(clauses, havingClause.toString(joined));
			addClause(clauses, orderClause.toString(joined));
			query = String.join(" ", clauses).trim();
		}

		return adjuster.adjustSQL(query);
	}

	@Override
	public synchronized int complement(BPreparedStatement statement) {
		int complemented = whereClause.getComplementer().complement(statement);
		return complemented + havingClause.getComplementer(complemented).complement(statement);
	}

	private Condition prepareCondition(Condition oldCondition, Condition newCondition) {
		if (oldCondition.equalsWithoutBinders(newCondition)) {
			oldCondition.changeBinders(newCondition.getBinders());
			return oldCondition;
		}

		query = null;
		return newCondition.replicate();
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
