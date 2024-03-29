package org.blendee.sql;

import java.util.LinkedList;

class ListClauses {

	private final LinkedList<SelectClause> select = new LinkedList<>();

	private final LinkedList<GroupByClause> group = new LinkedList<>();

	private final LinkedList<OrderByClause> order = new LinkedList<>();

	void addSelect(SelectClause clause) {
		select.addFirst(clause);
	}

	void addGroupBy(GroupByClause clause) {
		group.addFirst(clause);
	}

	void addOrderBy(OrderByClause clause) {
		order.addFirst(clause);
	}

	String toSelectString(boolean joined) {
		return toString(select, joined);
	}

	String toGroupByString(boolean joined) {
		return toString(group, joined);
	}

	String toOrderByString(boolean joined) {
		return toString(order, joined);
	}

	private String toString(LinkedList<? extends ListClause<?>> list, boolean joined) {
		var clause = list.pop();
		list.forEach(e -> clause.merge(e));

		clause.sortBlocks();

		return clause.toString(joined);
	}
}
