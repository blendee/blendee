package org.blendee.assist;

import java.util.List;
import java.util.Objects;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.Batch;
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
		return BlendeeManager.getConnection().createStatementAndGet(this, s -> s.executeUpdate());
	}

	@Override
	public void execute(Batch batch) {
		batch.add(this);
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
		List<Binder> binders = ComplementerValues.of(this).reproduce(placeHolderValues).binders();
		return new PlaybackDataManipulator(base.sql(), binders.toArray(new Binder[binders.size()]));
	}

	@Override
	public DataManipulator reproduce() {
		return new PlaybackDataManipulator(base.sql(), base.binders());
	}

	@Override
	public Binder[] currentBinders() {
		return base.binders();
	}

	@Override
	public String toString() {
		return sql();
	}
}
