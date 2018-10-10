package org.blendee.sql;

import java.util.Objects;
import java.util.function.Consumer;

import org.blendee.jdbc.ColumnMetadata;

/**
 * カラムの代わりに SQL に組み込むための、疑似カラムクラスです。
 * @author 千葉 哲嗣
 */
public class PseudoColumn implements Column {

	private final Relationship relationship;

	private final String expression;

	private final boolean addsTableId;

	/**
	 * コンストラクタです。
	 * @param relationship 属する {@link Relationship}
	 * @param expression カラムの代わりに使用する文字列表現
	 * @param addsTableId SQL 内でテーブルID補完可能かどうか
	 */
	public PseudoColumn(Relationship relationship, String expression, boolean addsTableId) {
		Objects.requireNonNull(relationship);
		Objects.requireNonNull(expression);
		this.relationship = relationship;
		this.expression = expression;
		this.addsTableId = addsTableId;
	}

	@Override
	public Relationship getRelationship() {
		return relationship;
	}

	@Override
	public Relationship getRootRelationship() {
		return relationship.getRoot();
	}

	@Override
	public void setRelationship(Consumer<Relationship> consumer) {
		consumer.accept(relationship);
	}

	@Override
	public String getName() {
		return expression;
	}

	@Override
	public String getComplementedName(QueryId id) {
		if (!addsTableId) return expression;
		return id.toComplementedColumnString(relationship.getId() + "." + expression);
	}

	@Override
	public String getId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Criteria getCriteria(QueryId id, Bindable bindable) {
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
		return getName();
	}

	@Override
	public Column replicate() {
		return this;
	}
}
