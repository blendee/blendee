package org.blendee.sql;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.blendee.jdbc.ColumnMetadata;

/**
 * カラムの代わりに SQL に組み込むための、複数カラムを使用した疑似カラムクラスです。
 * @author 千葉 哲嗣
 */
public class MultiColumn implements Column {

	private final Relationship root;

	private final String template;

	private final Column[] columns;

	/**
	 * コンストラクタです。
	 * @param root ルート{@link  Relationship}
	 * @param template カラムの代わりに使用する文字列表現のテンプレート
	 * @param columns テンプレートに埋め込まれるカラム
	 */
	public MultiColumn(Relationship root, String template, Column... columns) {
		Objects.requireNonNull(template);

		this.root = root;
		this.template = template;
		this.columns = columns;
	}

	@Override
	public Relationship getRelationship() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Relationship getRootRelationship() {
		return root;
	}

	@Override
	public void setRelationship(Consumer<Relationship> consumer) {
		for (var column : columns) {
			column.setRelationship(consumer);
		}
	}

	@Override
	public String getName() {
		var strings = Arrays.stream(columns)
			.map(c -> c.getName())
			.collect(Collectors.toList());

		return SQLFragmentFormat.execute(template, strings.toArray(new String[strings.size()]));
	}

	@Override
	public String getComplementedName(RuntimeId id) {
		var strings = Arrays.stream(columns)
			.map(c -> c.getComplementedName(id))
			.collect(Collectors.toList());

		return SQLFragmentFormat.execute(template, strings.toArray(new String[strings.size()]));
	}

	@Override
	public String getId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Criteria getCriteria(RuntimeId id, Bindable bindable) {
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
		return Objects.hash(root, template, Arrays.hashCode(columns));
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MultiColumn)) return false;
		var another = (MultiColumn) o;
		return root.equals(another.root)
			&& template.equals(another.template)
			&& Arrays.equals(columns, another.columns);
	}

	@Override
	public int compareTo(Column target) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Column replicate() {
		return this;
	}
}
