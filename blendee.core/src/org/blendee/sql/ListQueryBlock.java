package org.blendee.sql;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.blendee.sql.ListQueryClause.WholeCounter;

/**
 * 要素を並列に複数持つクエリの句の一部分を表すクラスです。
 * @author 千葉 哲嗣
 */
class ListQueryBlock implements Comparable<ListQueryBlock> {

	private final int order;

	/**
	 * テンプレート
	 */
	private final List<String> templates = new LinkedList<>();

	/**
	 * {@link Column}
	 */
	private final List<Column> columns = new LinkedList<>();

	ListQueryBlock(int order) {
		this.order = order;
	}

	ListQueryBlock() {
		this.order = ListQueryClause.DEFAULT_ORDER;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ListQueryBlock)) return false;
		if (!getClass().equals(o.getClass())) return false;
		ListQueryBlock target = (ListQueryBlock) o;
		return templates.equals(target.templates)
			&& columns.equals(target.columns);
	}

	@Override
	public int hashCode() {
		return Objects.hash(templates, columns);
	}

	@Override
	public int compareTo(ListQueryBlock another) {
		//オーバーフローは気にしなくてもいいと思う...
		return order - another.order;
	}

	public int getColumnsSize() {
		return columns.size();
	}

	public ListQueryBlock replicate() {
		ListQueryBlock clone = new ListQueryBlock(order);
		clone.templates.addAll(templates);
		columns.forEach(c -> clone.columns.add(c.replicate()));

		return clone;
	}

	void addColumn(Column column) {
		columns.add(column);
	}

	int getTemplatesSize() {
		return templates.size();
	}

	void addTemplate(String template) {
		templates.add(template);
	}

	List<Column> getColumns() {
		return columns;
	}

	String getTemplate(WholeCounter counter) {
		List<String> localTemplates = new LinkedList<>();

		int from = counter.i;
		int to = counter.i += columns.size();

		IntStream.range(from, to).forEach(i -> localTemplates.add("{" + i + "}"));

		return SQLFragmentFormat.execute(
			String.join(", ", templates).trim(),
			localTemplates.toArray(new String[localTemplates.size()]));
	}

}
