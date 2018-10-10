package org.blendee.support;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.sql.FromClause;
import org.blendee.sql.QueryId;

class AnonymousFromClause extends FromClause implements ComposedSQL {

	private final AnonymousRelationship relationship;

	AnonymousFromClause(AnonymousRelationship relationship, QueryId id) {
		super(relationship, id);
		this.relationship = relationship;
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		done = relationship.complement(done, statement);
		return super.complement(done, statement);
	}

	@Override
	public String sql() {
		return relationship.sql();
	}

	@Override
	protected FromClause replicate() {
		return this;
	}
}
