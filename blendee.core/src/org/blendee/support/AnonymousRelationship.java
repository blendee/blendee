package org.blendee.support;

import java.util.Collection;
import java.util.Objects;

import org.blendee.internal.TraversableNode;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.CrossReference;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Column;
import org.blendee.sql.NotFoundException;
import org.blendee.sql.PseudoColumn;
import org.blendee.sql.Relationship;

class AnonymousRelationship implements Relationship, ComposedSQL {

	private static final Relationship[] emptyArray = {};

	private final ComposedSQL sql;

	private final String alias;

	AnonymousRelationship(ComposedSQL sql, String alias) {
		Objects.requireNonNull(alias);
		this.alias = alias;
		this.sql = sql;
	}

	@Override
	public String toString() {
		return "(" + sql.sql() + ") " + alias;
	}

	@Override
	public TraversableNode getSubNode() {
		return new TraversableNode();
	}

	@Override
	public int compareTo(Relationship o) {
		return alias.compareTo(o.getId());
	}

	@Override
	public TablePath getTablePath() {
		return new AnonymousTablePath("(" + sql.sql() + ")");
	}

	@Override
	public Relationship[] getRelationships() {
		return emptyArray;
	}

	@Override
	public String getId() {
		return alias;
	}

	@Override
	public boolean hasColumn(String columnName) {
		return false;
	}

	@Override
	public Column getColumn(String columnName) {
		return new PseudoColumn(this, columnName, true);
	}

	@Override
	public Column[] getColumns() {
		return Column.EMPTY_ARRAY;
	}

	@Override
	public Column[] getPrimaryKeyColumns() {
		return Column.EMPTY_ARRAY;
	}

	@Override
	public boolean belongsPrimaryKey(Column column) {
		return false;
	}

	@Override
	public Relationship find(String foreignKeyName) {
		throw new NotFoundException(foreignKeyName);
	}

	@Override
	public Relationship find(String[] foreignKeyColumnNames) {
		throw new NotFoundException(String.join(", ", foreignKeyColumnNames));
	}

	@Override
	public CrossReference getCrossReference() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Relationship getParent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRoot() {
		return true;
	}

	@Override
	public Relationship getRoot() {
		return this;
	}

	@Override
	public void addParentTo(Collection<Relationship> parents) {}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		return sql.complement(done, statement);
	}

	@Override
	public String sql() {
		return sql.sql();
	}

	private class AnonymousTablePath extends TablePath {

		private AnonymousTablePath(String tableName) {
			super(tableName);
		}

		@Override
		public String toString() {
			return AnonymousRelationship.this.toString();
		}
	}
}
