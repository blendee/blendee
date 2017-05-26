package jp.ats.blendee.sql;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author 千葉 哲嗣
 */
abstract class SimpleQueryClause<T extends SimpleQueryClause<?>> extends QueryClause {

	protected final List<String> templates = new LinkedList<>();

	protected final List<Column> columns = new LinkedList<>();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SimpleQueryClause<?>)) return false;
		if (!getClass().equals(o.getClass())) return false;
		SimpleQueryClause<?> target = (SimpleQueryClause<?>) o;
		return templates.equals(target.templates) && columns.equals(target.columns);
	}

	@Override
	public int hashCode() {
		return Objects.hash(templates, columns);
	}

	@Override
	public int getColumnsSize() {
		return columns.size();
	}

	@Override
	public T replicate() {
		T clone = createNewInstance();
		clone.templates.addAll(templates);

		columns.forEach(c -> clone.columns.add(c.replicate()));

		return clone;
	}

	protected abstract T createNewInstance();

	void addColumn(Column column) {
		columns.add(column);
	}

	int getTemplatesSize() {
		return templates.size();
	}

	void addTemplate(String template) {
		templates.add(template);
	}

	@Override
	String getTemplate() {
		return String.join(", ", templates);
	}

	@Override
	List<Column> getColumnsInternal() {
		return columns;
	}
}
