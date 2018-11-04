package org.blendee.support;

import java.util.List;
import java.util.Objects;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.sql.Binder;
import org.blendee.sql.ComplementerValues;

class PlaybackDataManipulator implements DataManipulator {

	private final SimpleComposedSQL base;

	PlaybackDataManipulator(String sql, List<Binder> binders) {
		this(sql, binders.toArray(new Binder[binders.size()]));
	}

	PlaybackDataManipulator(String sql, Binder[] binders) {
		Objects.requireNonNull(sql);
		Objects.requireNonNull(binders);

		base = new SimpleComposedSQL(sql, binders);
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
	public PlaybackDataManipulator reproduce(Object... placeHolderValues) {
		List<Binder> binders = new ComplementerValues(this).reproduce(placeHolderValues).binders();
		return new PlaybackDataManipulator(base.sql(), binders.toArray(new Binder[binders.size()]));
	}

	@Override
	public PlaybackDataManipulator reproduce() {
		return new PlaybackDataManipulator(base.sql(), base.binders());
	}

	@Override
	public Binder[] currentBinders() {
		return new ComplementerValues(this).currentBinders();
	}

	@Override
	public String toString() {
		return sql();
	}
}
