package org.blendee.assist;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.Batch;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ChainPreparedStatementComplementer;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.sql.Binder;
import org.blendee.sql.ComplementerValues;

class RawDataManipulator implements DataManipulator {

	private final ComposedSQL sql;

	RawDataManipulator(ComposedSQL sql) {
		this.sql = sql;
	}

	RawDataManipulator(String sql, ChainPreparedStatementComplementer complementer) {
		this.sql = new ComposedSQL() {

			@Override
			public int complement(int done, BPreparedStatement statement) {
				return complementer.complement(done, statement);
			}

			@Override
			public String sql() {
				return sql;
			}
		};
	}

	@Override
	public int execute() {
		return BlendeeManager.getConnection().createStatementAndGet(sql, s -> s.executeUpdate());
	}

	@Override
	public void execute(Batch batch) {
		batch.add(sql);
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		return sql.complement(done, statement);
	}

	@Override
	public String sql() {
		return sql.sql();
	}

	@Override
	public DataManipulator reproduce(Object... placeHolderValues) {
		return new PlaybackDataManipulator(
			sql.sql(),
			ComplementerValues.of(sql).reproduce(placeHolderValues).binders());
	}

	@Override
	public DataManipulator reproduce() {
		return new PlaybackDataManipulator(
			sql.sql(),
			ComplementerValues.of(sql).reproduce().binders());
	}

	@Override
	public Binder[] currentBinders() {
		return ComplementerValues.of(sql).currentBinders();
	}

	@Override
	public String toString() {
		return sql();
	}
}
