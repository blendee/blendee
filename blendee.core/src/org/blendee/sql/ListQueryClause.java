package org.blendee.sql;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 要素を並列に複数持つクエリの句を表す基底クラスです。
 * @author 千葉 哲嗣
 * @param <T> サブクラスの型
 */
public abstract class ListQueryClause<T extends ListQueryClause<?>> extends QueryClause {

	/**
	 * テンプレート
	 */
	protected final List<String> templates = new LinkedList<>();

	/**
	 * {@link Column}
	 */
	protected final List<Column> columns = new LinkedList<>();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ListQueryClause<?>)) return false;
		if (!getClass().equals(o.getClass())) return false;
		ListQueryClause<?> target = (ListQueryClause<?>) o;
		return templates.equals(target.templates)
			&& columns.equals(target.columns);
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

	/**
	 * @return サブクラスのインスタンス
	 */
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
