package org.blendee.jdbc.impl;

import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.PreparedStatementComplementer;

public class ConcreteComposedSQL implements ComposedSQL {

	private final String sql;

	private final PreparedStatementComplementer complementer;

	public ConcreteComposedSQL(String sql, PreparedStatementComplementer complementer) {
		this.sql = sql;
		this.complementer = complementer;
	}

	@Override
	public String sql() {
		return sql;
	}

	@Override
	public int complement(BlenPreparedStatement statement) {
		return complementer.complement(statement);
	}
}
