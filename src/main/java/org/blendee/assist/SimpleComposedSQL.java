package org.blendee.assist;

import java.util.Objects;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.sql.Binder;

/**
 * {@link ComposedSQL} の簡易実装です。
 * @author 千葉 哲嗣
 */
public class SimpleComposedSQL implements ComposedSQL {

	private final String sql;

	private final Binder[] binders;

	/**
	 * @param sql SQL
	 * @param binders {@link Binder}
	 */
	public SimpleComposedSQL(String sql, Binder... binders) {
		this.sql = Objects.requireNonNull(sql);
		this.binders = binders.clone();
	}

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

	/**
	 * 保持している {@link Binder} を返します。
	 * @return {@link Binder} array
	 */
	public Binder[] binders() {
		return binders.clone();
	}
}
