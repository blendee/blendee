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
public class SQLQueryBuilder implements ComposedSQL, Reproducible<SQL> {

	private final FromClause fromClause;

	private final List<SQLDecorator> decorators = new LinkedList<>();

	private final List<CombiningQuery> combiningQueries = new LinkedList<>();

	private final List<JoinContainer> joins = new LinkedList<>();

	private SelectClause selectClause;

	private Criteria whereClause;

	private GroupByClause groupClause;

	private Criteria havingClause;

	private OrderByClause orderClause;

	private String query;

	/**
	 * 使用可能な検索結果結合キーワードです。
	 */
	public enum CombineOperator {

		/**
		 * UNION
		 */
		UNION("UNION"),

		/**
		 * UNION ALL
		 */
		UNION_ALL("UNION ALL"),

		/**
		 * INTERSECT
		 */
		INTERSECT("INTERSECT"),

		/**
		 * INTERSECT ALL
		 */
		INTERSECT_ALL("INTERSECT ALL"),

		/**
		 * EXCEPT
		 */
		EXCEPT("EXCEPT"),

		/**
		 * EXCEPT ALL
		 */
		EXCEPT_ALL("EXCEPT ALL");

		private final String expression;

		private CombineOperator(String expression) {
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

		var id = fromClause.getRuntimeId();

		selectClause = useSelectAsterisk ? new SelectAsteriskClause() : new SelectClause(id);
		groupClause = new GroupByClause(id);
		orderClause = new OrderByClause(id);

		var factory = new CriteriaFactory(id);
		whereClause = factory.create();
		havingClause = factory.create();
	}

	/**
	 * 現在設定されている FROM 句を返します。
	 * @return FROM 句
	 */
	public FromClause getFromClause() {
		return fromClause.replicate();
	}

	/**
	 * SELECT 句を設定します。
	 * @param clause SELECT 句
	 */
	public void setSelectClause(SelectClause clause) {
		if (selectClause.equals(clause)) return;
		selectClause = clause.replicate();
		query = null;
	}

	/**
	 * 現在設定されている SELECT 句を返します。
	 * @return SELECT 句
	 */
	public SelectClause getSelectClause() {
		return selectClause.replicate();
	}

	/**
	 * 現在設定されている SELECT 句にカラムが設定されているかどうかを判定します。
	 * @return SELECT 句にカラムが設定されているかどうか
	 */
	public boolean hasSelectColumns() {
		return selectClause.isValid();
	}

	/**
	 * WHERE 句を設定します。
	 * @param clause WHERE 句
	 */
	public void setWhereClause(Criteria clause) {
		if (whereClause.equals(clause)) return;
		whereClause = clause.replicate();
		query = null;
	}

	/**
	 * 現在設定されている WHERE 句を返します。
	 * @return WHERE 句
	 */
	public Criteria getWhereClause() {
		return whereClause.replicate();
	}

	/**
	 * GROUP BY 句を設定します。
	 * @param clause GROUP BY 句
	 */
	public void setGroupByClause(GroupByClause clause) {
		if (groupClause.equals(clause)) return;
		groupClause = clause.replicate();
		query = null;
	}

	/**
	 * 現在設定されている GROUP BY 句を返します。
	 * @return GROUP BY 句
	 */
	public GroupByClause getGroupByClause() {
		return groupClause.replicate();
	}

	/**
	 * HAVING 句を設定します。
	 * @param clause HAVING 句
	 */
	public void setHavingClause(Criteria clause) {
		if (havingClause.equals(clause)) return;
		havingClause = clause.replicate();
		query = null;
	}

	/**
	 * 現在設定されている HAVING 句を返します。
	 * @return HAVING 句
	 */
	public Criteria getHavingClause() {
		return havingClause.replicate();
	}

	/**
	 * UNION するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param operator UNION の種類
	 * @param query UNION 対象
	 */
	public void combine(CombineOperator operator, ComposedSQL query) {
		combiningQueries.add(new CombiningQuery(operator, query));
		query = null;
	}

	/**
	 * 保持する {@link CombiningQuery} を返します。
	 * @return {@link CombiningQuery}
	 */
	public CombiningQuery[] getCombiningQueries() {
		return combiningQueries.toArray(new CombiningQuery[combiningQueries.size()]);
	}

	/**
	 * ORDER BY 句を設定します。
	 * @param clause ORDER BY 句
	 */
	public void setOrderByClause(OrderByClause clause) {
		if (orderClause.equals(clause)) return;
		orderClause = clause.replicate();
		query = null;
	}

	/**
	 * 現在設定されている ORDER BY 句を返します。
	 * @return ORDER BY 句
	 */
	public OrderByClause getOrderByClause() {
		return orderClause.replicate();
	}

	/**
	 * {@link SQLDecorator} を設定します。
	 * @param decorators {@link SQLDecorator}
	 */
	public void addDecorator(SQLDecorator... decorators) {
		for (var decorator : decorators) {
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
	public void join(JoinType joinType, SQLQueryBuilder another, Criteria onCriteria) {
		if (combiningQueries.size() > 0 || another.combiningQueries.size() > 0)
			//UNION されたクエリはマージできません
			throw new IllegalArgumentException("UNIONed queries can not be merged.");

		joins.add(new JoinContainer(joinType, another, onCriteria));

		query = null;
	}

	/**
	 * このインスタンスをサブクエリとして使用するかどうかを指定します。<br>
	 * サブクエリで使用すると、すべてのカラムにテーブル ID が補完されます。
	 * @param forSubquery true の場合、サブクエリとして使用
	 */
	public void forSubquery(boolean forSubquery) {
		fromClause.forSubquery(forSubquery);
		query = null;
	}

	/**
	 * @return 現在の設定値
	 */
	public boolean forSubquery() {
		return fromClause.forSubquery();
	}

	/**
	 * 現在設定されている各句から SELECT 文を生成し返します。
	 * @return SELECT 文
	 */
	@Override
	public String sql() {
		prepareForComposedSQL();

		var currentQuery = query;
		for (var decorator : decorators) {
			currentQuery = decorator.decorate(currentQuery);
		}

		return currentQuery;
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		prepareForComposedSQL();

		done = selectClause.complement(done, statement);
		done = fromClause.complement(done, statement);
		done = whereClause.complement(done, statement);
		done = groupClause.complement(done, statement);
		done = havingClause.complement(done, statement);

		for (var union : combiningQueries) {
			done = union.getSQL().complement(done, statement);
		}

		return orderClause.complement(done, statement);
	}

	/**
	 * {@link SQL} を生成します。
	 * @return {@link SQL}
	 */
	public SQL build() {
		return reproduce();
	}

	@Override
	public SQL reproduce(Object... placeHolderValues) {
		return SQL.getInstanceWithPlaceholderValues(sql(), placeHolderValues);
	}

	@Override
	public SQL reproduce() {
		return SQL.getInstance(sql(), ComplementerValues.of(this));
	}

	@Override
	public Binder[] currentBinders() {
		return ComplementerValues.of(this).currentBinders();
	}

	@Override
	public String toString() {
		return sql();
	}

	private void prepareForComposedSQL() {
		if (query == null) {
			prepareFrom();

			var listClauses = new ListClauses();
			merge(listClauses);

			listClauses.addSelect(selectClause);
			listClauses.addGroupBy(groupClause);
			listClauses.addOrderBy(orderClause);

			var joined = fromClause.isJoined();

			havingClause.setKeyword("HAVING");
			whereClause.setKeyword("WHERE");

			var clauses = new ArrayList<String>();
			addClause(clauses, listClauses.toSelectString(joined));
			addClause(clauses, fromClause.toString());
			addClause(clauses, whereClause.toString(joined));
			addClause(clauses, listClauses.toGroupByString(joined));
			addClause(clauses, havingClause.toString(joined));

			combiningQueries.forEach(u -> {
				clauses.add(u.getCombineOperator().expression);
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

		var root = fromClause.getRoot();
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
			super(RuntimeIdFactory.stubInstance());
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
