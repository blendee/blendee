package org.blendee.sql;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.blendee.jdbc.ColumnMetadata;

/**
 * カラムの代わりに SQL に組み込むための、複数カラムを使用した疑似カラムクラスです。
 * @author 千葉 哲嗣
 */
public class MultiColumn extends Column {

	private final Relationship relationship;

	private final String template;

	private final Column[] columns;

	/**
	 * コンストラクタです。
	 * @param relationship 属する {@link Relationship}
	 * @param template カラムの代わりに使用する文字列表現のテンプレート
	 * @param columns テンプレートに埋め込まれるカラム
	 */
	public MultiColumn(Relationship relationship, String template, Column[] columns) {
		Objects.requireNonNull(relationship);
		Objects.requireNonNull(template);
		this.relationship = relationship;
		this.template = template;
		this.columns = columns;
	}

	@Override
	public Relationship getRelationship() {
		return relationship;
	}

	@Override
	public String getName() {
		List<String> strings = Arrays.asList(columns)
			.stream()
			.map(c -> c.getName())
			.collect(Collectors.toList());

		return SQLFragmentFormat.execute(template, strings.toArray(new String[strings.size()]));
	}

	@Override
	public String getComplementedName() {
		List<String> strings = Arrays.asList(columns)
			.stream()
			.map(c -> c.getComplementedName())
			.collect(Collectors.toList());

		return SQLFragmentFormat.execute(template, strings.toArray(new String[strings.size()]));
	}

	@Override
	public String getID() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Criteria getCriteria(Bindable bindable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<?> getType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ColumnMetadata getColumnMetadata() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Column findAnotherRootColumn(Relationship another) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isPrimaryKey() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
		return Objects.hash(relationship, template, Arrays.hashCode(columns));
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MultiColumn)) return false;
		MultiColumn another = (MultiColumn) o;
		return relationship.equals(another.relationship)
			&& template.equals(another.template)
			&& Arrays.equals(columns, another.columns);
	}

	@Override
	public int compareTo(Column target) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return getComplementedName();
	}
}
