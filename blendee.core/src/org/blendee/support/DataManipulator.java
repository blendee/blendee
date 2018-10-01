package org.blendee.support;

import java.util.Objects;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.sql.Binder;

public class DataManipulator implements ComposedSQL {

	private final ComposedSQL base;

	public DataManipulator(String sql, Binder[] binders) {
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

	public DataManipulator(ComposedSQL base) {
		Objects.requireNonNull(base);
		this.base = base;
	}

	public int execute() {
		return BlendeeManager.getConnection().executeAndGet(this, s -> s.executeUpdate());
	}

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
}
