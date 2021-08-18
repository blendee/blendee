package org.blendee.sql;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.ChainPreparedStatementComplementer;
import org.blendee.sql.ListClause.WholeCounter;

/**
 * 要素を並列に複数持つクエリの句の一部分を表すクラスです。
 * @author 千葉 哲嗣
 */
class ListQueryBlock implements Comparable<ListQueryBlock>, ChainPreparedStatementComplementer {

	private final RuntimeId id;

	private final int order;

	/**
	 * テンプレート
	 */
	private final List<String> templates = new LinkedList<>();

	/**
	 * {@link Column}
	 */
	private final List<Column> columns = new LinkedList<>();

	private ComplementerValues complementer;

	ListQueryBlock(RuntimeId id, int order) {
		this.id = id;
		this.order = order;
	}

	ListQueryBlock(RuntimeId id) {
		this.id = id;
		this.order = ListClause.DEFAULT_ORDER;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ListQueryBlock)) return false;
		if (!getClass().equals(o.getClass())) return false;
		var target = (ListQueryBlock) o;
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
		var clone = new ListQueryBlock(id, order);
		clone.templates.addAll(templates);
		columns.forEach(c -> clone.columns.add(c.replicate()));

		//ComplementerValuesは不変なのでそのまま渡す
		if (complementer != null) clone.setComplementer(complementer);

		return clone;
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		return complementer == null ? done : complementer.complement(done, statement);
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

	void setComplementer(ChainPreparedStatementComplementer complementer) {
		this.complementer = ComplementerValues.of(complementer);
	}

	List<Column> getColumns(RuntimeId base) {
		if (id.equals(base)) return columns;

		return columns.stream().map(c -> new RuntimeIdColumn(c, id)).collect(Collectors.toList());
	}

	String getTemplate(WholeCounter counter) {
		var localTemplates = new LinkedList<String>();

		var from = counter.i;
		var to = counter.i += columns.size();

		IntStream.range(from, to).forEach(i -> localTemplates.add("{" + i + "}"));

		return SQLFragmentFormat.execute(
			String.join(", ", templates).trim(),
			localTemplates.toArray(new String[localTemplates.size()]));
	}
}
