package org.blendee.sql;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

	private String toString(LinkedList<? extends ListQueryClause<?>> list, boolean joined) {
		List<String> result = new ArrayList<>();

		ListQueryClause<?> clause = list.pop();
		result.add(clause.toString(joined));

		for (ListQueryClause<?> element : list) {
			result.add(element.toStringWithoutKeyword(joined));
		}

		return String.join(
			", ",
			result.stream().filter(e -> e.trim().length() > 0).collect(Collectors.toList()));
	}
}
