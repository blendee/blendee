package org.blendee.sql;

import java.util.Objects;
import java.util.function.Consumer;

import org.blendee.jdbc.ColumnMetadata;

@SuppressWarnings("javadoc")
public class RuntimeIdColumn implements Column {

	private final Column base;

	private final RuntimeId id;

	public RuntimeIdColumn(Column base, RuntimeId id) {
		this.base = base;
		this.id = id;
	}

	@Override
	public int compareTo(Column target) {
		return base.compareTo(target);
	}

	@Override
	public String getId() {
		return base.getId();
	}

	@Override
	public Relationship getRelationship() {
		return base.getRelationship();
	}

	@Override
	public Relationship getRootRelationship() {
		return base.getRootRelationship();
	}

	@Override
	public void setRelationship(Consumer<Relationship> consumer) {
		base.setRelationship(consumer);
	}

	@Override
	public String getName() {
		return base.getName();
	}

	@Override
	public Class<?> getType() {
		return base.getType();
	}

	@Override
	public ColumnMetadata getColumnMetadata() {
		return base.getColumnMetadata();
	}

	@Override
	public String getComplementedName(RuntimeId id) {
		return base.getComplementedName(this.id);
	}

	@Override
	public Column replicate() {
		return new RuntimeIdColumn(base, id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(base, id);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RuntimeIdColumn)) return false;

		RuntimeIdColumn another = (RuntimeIdColumn) o;
		return base.equals(another.base) && id.equals(another.id);
	}
}
