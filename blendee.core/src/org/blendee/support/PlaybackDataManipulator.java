package org.blendee.support;

import java.util.List;
import java.util.Objects;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.sql.Binder;
import org.blendee.sql.ComplementerValues;

class PlaybackDataManipulator implements DataManipulator {

	private final ComposedSQL base;

	PlaybackDataManipulator(String sql, List<Binder> binders) {
		this(sql, binders.toArray(new Binder[binders.size()]));
	}

	PlaybackDataManipulator(String sql, Binder[] binders) {
		Objects.requireNonNull(sql);
		Objects.requireNonNull(binders);

		base = new ComposedSQL() {

			@Override
			public int complement(int done, BPreparedStatement statement) {
				for (Binder binder : binders) {
					binder.bind(++done, statement);
				}

				return done;
			}

			@Override
			public String sql() {
				return sql;
			}
		};
	}

	@Override
	public int execute() {
		return BlendeeManager.getConnection().executeAndGet(this, s -> s.executeUpdate());
	}

	@Override
	public void execute(BatchStatement statement) {
		statement.addBatch(this);
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		return base.complement(done, statement);
	}

	@Override
	public String sql() {
		return base.sql();
	}

	@Override
	public DataManipulator reproduce(Object... placeHolderValues) {
		List<Binder> binders = new ComplementerValues(this).binders();
		return new PlaybackDataManipulator(base.sql(), binders.toArray(new Binder[binders.size()]));
	}

	@Override
	public Binder[] currentBinders() {
		return new ComplementerValues(this).currentBinders();
	}
}
