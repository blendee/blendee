package org.blendee.sql;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.blendee.jdbc.BPreparedStatement;

/**
 * SQL 文に含まれる条件句を表すクラスです。条件句とは、具体的には WHERE 句と HAVING 句をさしています。
 * @author 千葉 哲嗣
 * @see CriteriaFactory
 * @see SQLQueryBuilder#setWhereClause(Criteria)
 * @see SQLQueryBuilder#setHavingClause(Criteria)
 */
public class Criteria extends Clause {

	//!! このクラスに新たにメソッドを追加する場合は、 ProxyCriteria にも追加すること !!

	/**
	 * 論理演算子の列挙型です。
	 */
	private enum LogicalOperator {

		/**
		 * 論理演算子 'AND'
		 */
		AND("AND", false),

		/**
		 * 論理演算子 'OR'
		 */
		OR("OR", true);

		private final String operator;

		private final boolean weak;

		private LogicalOperator(String operator, boolean weak) {
			this.operator = " " + operator + " ";
			this.weak = weak;
		}

		@Override
		public String toString() {
			return operator;
		}

		private String process(LogicalOperator operator, String clause) {
			if (operator == null || this == operator || weak) return clause;
			return "(" + clause + ")";
		}
	}

	private String clause;

	private final List<Column> columns = new LinkedList<>();

	private final List<Binder> binders = new LinkedList<>();

	private LogicalOperator current;

	private String keyword = "";

	Criteria(RuntimeId id, String clause, Column[] columns, Binder[] binders) {
		super(id);
		this.clause = clause;
		this.columns.addAll(Arrays.asList(columns));
		this.binders.addAll(Arrays.asList(binders));
	}

	private Criteria(RuntimeId id) {
		super(id);
		clause = null;
	}

	/**
	 * このインスタンスが表す条件に、新たな条件を AND 追加します。
	 * @param target 追加する条件
	 * @return このインスタンス
	 */
	public Criteria and(Criteria target) {
		//append が ProxyCriteria でオーバーライドされているので、
		//このメソッドは ProxyCriteria でオーバーライドしなくても OK
		append(LogicalOperator.AND, target);
		return this;
	}

	/**
	 * このインスタンスが表す条件に、新たな条件を OR 追加します。
	 * @param target 追加する条件
	 * @return このインスタンス
	 */
	public Criteria or(Criteria target) {
		//append が ProxyCriteria でオーバーライドされているので、
		//このメソッドは ProxyCriteria でオーバーライドしなくても OK
		append(LogicalOperator.OR, target);
		return this;
	}

	/**
	 * この条件を反転させます。<br>
	 * 具体的には、条件の先頭に NOT を付加します。
	 * @return このインスタンス
	 */
	public Criteria reverse() {
		clause = "NOT (" + clause + ")";
		current = null;
		return this;
	}

	/**
	 * このインスタンスに条件が含まれるかどうかを検査します。
	 * @return 条件が含まれる場合 true
	 */
	public boolean isAvailable() {
		return true;
	}

	/**
	 * プレースホルダにセットする予定の値を返します。
	 * @return 値
	 */
	public Binder[] getBinders() {
		return binders.toArray(new Binder[binders.size()]);
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		for (Iterator<Binder> i = binders.iterator(); i.hasNext(); done++) {
			i.next().bind(done + 1, statement);
		}

		return done;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Criteria)) return false;
		Criteria target = strip((Criteria) o);
		if (target == null) return false;

		if (!clause.equals(target.clause)
			|| !columns.equals(target.columns)
			|| binders.size() != target.binders.size()
			|| current != target.current
			|| !binders.equals(target.binders)) return false;
		return true;
	}

	@Override
	public int hashCode() {
		List<Object> members = new LinkedList<>();
		members.add(clause);
		members.add(current);
		members.addAll(columns);
		members.addAll(binders);
		return Objects.hash(members);
	}

	@Override
	public int getColumnsSize() {
		return columns.size();
	}

	@Override
	public Criteria replicate() {
		Binder[] replicateBinders = new Binder[binders.size()];
		{
			int counter = 0;
			for (Iterator<Binder> i = binders.iterator(); i.hasNext(); counter++) {
				replicateBinders[counter] = i.next().replicate();
			}
		}

		Column[] replicateColumns = new Column[columns.size()];
		{
			int counter = 0;
			for (Iterator<Column> i = columns.iterator(); i.hasNext(); counter++) {
				replicateColumns[counter] = i.next().replicate();
			}
		}

		Criteria clone = new Criteria(runtimeId, clause, replicateColumns, replicateBinders);
		clone.current = current;
		clone.keyword = keyword;
		return clone;
	}

	/**
	 * このインスタンスの持つ {@link Column} が属する {@link Relationship} をパラメータのものに変更します。
	 * @param another 新しい {@link Relationship}
	 * @throws NotFoundException このインスタンスが持つカラムの属する {@link Relationship} の参照先に another が含まれない場合
	 */
	public void changeColumnsRelationshipTo(Relationship another) {
		List<Column> buffer = new LinkedList<>();
		columns.forEach(c -> {
			c.findAnotherRootColumn(another);
			buffer.add(c);
		});

		columns.clear();

		columns.addAll(buffer);
	}

	/**
	 * このインスタンスが表す条件に、新たな条件を追加します。
	 * @param operator 論理演算子 AND OR
	 * @param another 追加する条件
	 */
	void append(LogicalOperator operator, Criteria another) {
		Criteria target = strip(another);
		if (target == null) return;
		clause = target.delegateProcess(operator, current, clause, columns.size());

		if (runtimeId.equals(target.runtimeId)) {
			columns.addAll(target.columns);
		} else {
			columns.addAll(target.columns.stream().map(c -> new RuntimeIdColumn(c, target.runtimeId)).collect(Collectors.toList()));
		}

		binders.addAll(target.binders);
		current = operator;
	}

	void changeBinders(Binder[] newBinders) {
		binders.clear();
		binders.addAll(Arrays.asList(newBinders));
	}

	void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	@Override
	String getKeyword() {
		return keyword;
	}

	@Override
	String getTemplate() {
		return clause;
	}

	@Override
	List<Column> getColumnsInternal() {
		return columns;
	}

	String delegateProcess(
		LogicalOperator newOperator,
		LogicalOperator baseCurrentOperator,
		String baseClause,
		int skipCount) {
		int columnsSize = columns.size();
		String[] replace = new String[columnsSize];
		for (int i = 0; i < columnsSize; i++) {
			replace[i] = "{" + (i + skipCount) + "}";
		}
		return newOperator.process(baseCurrentOperator, baseClause)
			+ newOperator
			+ newOperator.process(current, SQLFragmentFormat.execute(getTemplate(), replace));
	}

	private static Criteria strip(Criteria criteria) {
		if (criteria instanceof ProxyCriteria) return strip(((ProxyCriteria) criteria).inclusion);
		return criteria;
	}

	static class ProxyCriteria extends Criteria {

		private Criteria inclusion;

		ProxyCriteria(RuntimeId id) {
			super(id);
		}

		@Override
		public String toString() {
			if (inclusion == null) return "";
			return inclusion.toString();
		}

		@Override
		public Criteria reverse() {
			if (inclusion == null) return this;
			inclusion.reverse();
			return this;
		}

		@Override
		public boolean isAvailable() {
			if (inclusion == null) return false;
			return inclusion.isAvailable();
		}

		@Override
		public Binder[] getBinders() {
			if (inclusion == null) return Binder.EMPTY_ARRAY;
			return inclusion.getBinders();
		}

		@Override
		public int complement(int done, BPreparedStatement statement) {
			if (inclusion == null) return done;
			return inclusion.complement(done, statement);
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Criteria)) return false;
			if (inclusion == null) return !((Criteria) o).isAvailable();
			return inclusion.equals(o);
		}

		@Override
		public int hashCode() {
			if (inclusion == null) return 0;
			return inclusion.hashCode();
		}

		@Override
		public int getColumnsSize() {
			if (inclusion == null) return 0;
			return inclusion.getColumnsSize();
		}

		@Override
		public Criteria replicate() {
			if (inclusion == null) return new ProxyCriteria(runtimeId);
			return inclusion.replicate();
		}

		@Override
		public void changeColumnsRelationshipTo(Relationship relationship) {
			if (inclusion == null) return;
			inclusion.changeColumnsRelationshipTo(relationship);
		}

		@Override
		void append(LogicalOperator operator, Criteria clause) {
			if (inclusion == null) {
				inclusion = clause.replicate();
			} else {
				inclusion.append(operator, clause);
			}
		}

		@Override
		void changeBinders(Binder[] newBinders) {
			if (inclusion == null) return;
			inclusion.changeBinders(newBinders);
		}

		@Override
		void setKeyword(String keyword) {
			if (inclusion == null) return;
			inclusion.setKeyword(keyword);
		}

		@Override
		String getKeyword() {
			if (inclusion == null) return "";
			return inclusion.getKeyword();
		}

		@Override
		String getTemplate() {
			if (inclusion == null) return "";
			return inclusion.getTemplate();
		}

		@Override
		List<Column> getColumnsInternal() {
			if (inclusion == null) return super.columns;
			return inclusion.getColumnsInternal();
		}

		@Override
		void join(FromClause fromClause) {
			if (inclusion == null) return;
			inclusion.join(fromClause);
		}

		@Override
		public String toString(boolean joining) {
			if (inclusion == null) return "";
			return inclusion.toString(joining);
		}
	}
}
