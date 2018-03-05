package org.blendee.sql;

import java.util.Objects;

import org.blendee.jdbc.ColumnMetadata;

/**
 * カラムの代わりに SQL に組み込むための、疑似カラムクラスです。
 * @author 千葉 哲嗣
 */
public class PseudoColumn extends Column {

	private final Relationship relationship;

	private final String expression;

	private final boolean complementable;

	/**
	 * コンストラクタです。
	 * @param relationship 属する {@link Relationship}
	 * @param expression カラムの代わりに使用する文字列表現
	 * @param complementable SQL 内でテーブル名保管可能かどうか
	 */
	public PseudoColumn(Relationship relationship, String expression, boolean complementable) {
		Objects.requireNonNull(relationship);
		Objects.requireNonNull(expression);
		this.relationship = relationship;
		this.expression = expression;
		this.complementable = complementable;
	}

	@Override
	public Relationship getRelationship() {
		return relationship;
	}

	@Override
	public String getName() {
		return expression;
	}

	@Override
	public String getComplementedName() {
		if (!complementable) return expression;
		return relationship.getID() + "." + expression;
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
		return Objects.hash(relationship, expression);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PseudoColumn)) return false;
		PseudoColumn another = (PseudoColumn) o;
		return relationship.equals(another.relationship) && expression.equals(another.expression);
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