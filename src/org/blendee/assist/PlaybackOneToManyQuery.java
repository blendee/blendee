package org.blendee.assist;

import java.util.List;

import org.blendee.internal.U;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObjectIterator;
import org.blendee.selector.SelectedValuesConverter;
import org.blendee.selector.SimpleSelectedValuesConverter;
import org.blendee.sql.Binder;
import org.blendee.sql.Column;
import org.blendee.sql.ComplementerValues;
import org.blendee.sql.RuntimeId;

/**
 * 再実行可能な {@link OneToManyQuery} クラスです。<br>
 * @author 千葉 哲嗣
 * @param <O> One 一対多の一側の型
 * @param <M> Many 一対多の多側の型連鎖
 */
class PlaybackOneToManyQuery<O extends Row, M>
	extends OneToManyQuery<O, M> {

	private final List<OneToManyBehavior> route;

	private final String sql;

	private final String countSQL;

	private final ComplementerValues values;

	private final Column[] selectedColumns;

	private final SelectedValuesConverter converter = new SimpleSelectedValuesConverter();

	PlaybackOneToManyQuery(
		OneToManyBehavior self,
		List<OneToManyBehavior> route,
		String sql,
		String countSQL,
		ComplementerValues values,
		Column[] selectedColumns) {
		super(self);
		this.route = route;
		this.sql = sql;
		this.countSQL = countSQL;
		this.values = values;
		this.selectedColumns = selectedColumns;
	}

	@Override
	BStatement createStatementForCount() {
		return BlendeeManager
			.getConnection()
			.getStatement(countSQL, values);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	@Override
	List<OneToManyBehavior> route() {
		return route;
	}

	@Override
	DataObjectIterator iterator() {
		return DataAccessHelper.select(
			sql,
			values,
			self().getRelationship().getRoot(),
			selectedColumns,
			converter);
	}

	@Override
	public String sql() {
		return sql;
	}

	@Override
	public ComposedSQL countSQL() {
		return new ComposedSQL() {

			@Override
			public String sql() {
				return countSQL;
			}

			@Override
			public int complement(int done, BPreparedStatement statement) {
				return values.complement(done, statement);
			}
		};
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		return values.complement(done, statement);
	}

	@Override
	public OneToManyQuery<O, M> reproduce(Object... placeHolderValues) {
		return new PlaybackOneToManyQuery<>(
			self(),
			route,
			sql,
			countSQL,
			values.reproduce(placeHolderValues),
			selectedColumns);
	}

	@Override
	public OneToManyQuery<O, M> reproduce() {
		return new PlaybackOneToManyQuery<>(
			self(),
			route,
			sql,
			countSQL,
			values.reproduce(),
			selectedColumns);
	}

	@Override
	public Binder[] currentBinders() {
		return values.currentBinders();
	}

	@Override
	RuntimeId runtimeId() {
		return self().getRuntimeId();
	}
}
