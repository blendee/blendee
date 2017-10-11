package org.blendee.sql;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.PreparedStatementComplementer;

/**
 * SQL 文に含まれる条件句を表すクラスです。条件句とは、具体的には WHERE 句と HAVING 句をさしています。
 * @author 千葉 哲嗣
 * @see ConditionFactory
 * @see QueryBuilder#setWhereClause(Condition)
 * @see QueryBuilder#setHavingClause(Condition)
 */
public class Condition extends QueryClause {

	//!! このクラスに新たにメソッドを追加する場合は、 ProxyCondition にも追加すること !!

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

	Condition(String clause, Column[] columns, Binder[] binders) {
		this.clause = clause;
		this.columns.addAll(Arrays.asList(columns));
		this.binders.addAll(Arrays.asList(binders));
	}

	private Condition() {
		clause = null;
	}

	/**
	 * このインスタンスが表す条件に、新たな条件を AND 追加します。
	 * @param target 追加する条件
	 * @return このインスタンス
	 */
	public Condition and(Condition target) {
		//append が ProxyCondition でオーバーライドされているので、
		//このメソッドは ProxyCondition でオーバーライドしなくても OK
		append(LogicalOperator.AND, target);
		return this;
	}

	/**
	 * このインスタンスが表す条件に、新たな条件を OR 追加します。
	 * @param target 追加する条件
	 * @return このインスタンス
	 */
	public Condition or(Condition target) {
		//append が ProxyCondition でオーバーライドされているので、
		//このメソッドは ProxyCondition でオーバーライドしなくても OK
		append(LogicalOperator.OR, target);
		return this;
	}

	/**
	 * この条件を反転させます。<br>
	 * 具体的には、条件の先頭に NOT を付加します。
	 * @return このインスタンス
	 */
	public Condition reverse() {
		clearCache();
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

	/**
	 * プレースホルダにセットする予定の値を {@link PreparedStatementComplementer} として返します。
	 * @return {@link PreparedStatementComplementer}
	 */
	public PreparedStatementComplementer getComplementer() {
		return new InnerComplementer(0);
	}

	/**
	 * プレースホルダにセットする予定の値を {@link PreparedStatementComplementer} として返します。<br>
	 * 返される PreparedStatementComplementer は、パラメータの skipCount 分だけプレースホルダ位置をスキップして値をセットします。
	 * @param skipCount 飛ばすプレースホルダ数
	 * @return {@link PreparedStatementComplementer}
	 */
	public PreparedStatementComplementer getComplementer(int skipCount) {
		return new InnerComplementer(skipCount);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Condition)) return false;
		Condition target = strip((Condition) o);
		if (target == null) return false;
		if (!equalsWithoutBinders(target) || !binders.equals(target.binders)) return false;
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
	public Condition replicate() {
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

		Condition clone = new Condition(clause, replicateColumns, replicateBinders);
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
		int size = columns.size();
		for (int i = 0; i < size; i++) {
			columns.set(i, columns.get(i).findAnotherRootColumn(another));
		}
	}

	/**
	 * このインスタンスが表す条件に、新たな条件を追加します。
	 * @param operator 論理演算子 AND OR
	 * @param target 追加する条件
	 */
	void append(LogicalOperator operator, Condition target) {
		target = strip(target);
		if (target == null) return;
		clearCache();
		clause = target.delegateProcess(operator, current, clause, columns.size());
		columns.addAll(target.columns);
		binders.addAll(target.binders);
		current = operator;
	}

	boolean equalsWithoutBinders(Condition target) {
		if (this == target) return true;
		target = strip(target);
		if (target == null) return false;
		if (!clause.equals(target.clause)
			|| !columns.equals(target.columns)
			|| binders.size() != target.binders.size()
			|| current != target.current) return false;
		return true;
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

	private static Condition strip(Condition condition) {
		if (condition instanceof ProxyCondition) return strip(((ProxyCondition) condition).inclusion);
		return condition;
	}

	private class InnerComplementer implements PreparedStatementComplementer {

		private final int skipCount;

		private InnerComplementer(int skipCount) {
			this.skipCount = skipCount;
		}

		@Override
		public int complement(BPreparedStatement statement) {
			int counter = skipCount;
			for (Iterator<Binder> i = binders.iterator(); i.hasNext(); counter++) {
				i.next().bind(counter + 1, statement);
			}
			return counter;
		}
	}

	static class ProxyCondition extends Condition {

		private Condition inclusion;

		ProxyCondition() {}

		@Override
		public String toString() {
			if (inclusion == null) return "";
			return inclusion.toString();
		}

		@Override
		public Condition reverse() {
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
		public PreparedStatementComplementer getComplementer() {
			if (inclusion == null) return new ProxyInnerComplementer();
			return inclusion.getComplementer();
		}

		@Override
		public PreparedStatementComplementer getComplementer(int skipCount) {
			if (inclusion == null) return new ProxyInnerComplementer();
			return inclusion.getComplementer(skipCount);

		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Condition)) return false;
			if (inclusion == null) return !((Condition) o).isAvailable();
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
		public Condition replicate() {
			if (inclusion == null) return new ProxyCondition();
			return inclusion.replicate();
		}

		@Override
		public void changeColumnsRelationshipTo(Relationship relationship) {
			if (inclusion == null) return;
			inclusion.changeColumnsRelationshipTo(relationship);
		}

		@Override
		void append(LogicalOperator operator, Condition clause) {
			if (inclusion == null) {
				inclusion = clause.replicate();
			} else {
				inclusion.append(operator, clause);
			}
		}

		@Override
		boolean equalsWithoutBinders(Condition target) {
			if (inclusion == null) return !target.isAvailable();
			return inclusion.equalsWithoutBinders(target);
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
	}

	private static class ProxyInnerComplementer implements PreparedStatementComplementer {

		@Override
		public int complement(BPreparedStatement statement) {
			return 0;
		}
	}
}
