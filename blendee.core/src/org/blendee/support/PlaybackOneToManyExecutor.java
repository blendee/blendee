package org.blendee.support;

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
import org.blendee.sql.Column;
import org.blendee.sql.ComplementerValues;

/**
 * 再実行可能な {@link OneToManyExecutor} クラスです。<br>
 * @author 千葉 哲嗣
 * @param <O> One 一対多の一側の型
 * @param <M> Many 一対多の多側の型連鎖
 */
class PlaybackOneToManyExecutor<O extends Row, M>
	extends OneToManyExecutor<O, M> {

	private final QueryRelationship root;

	private final List<QueryRelationship> route;

	private final String sql;

	private final String countSQL;

	private final ComplementerValues values;

	private final Column[] selectedColumns;

	private final SelectedValuesConverter converter = new SimpleSelectedValuesConverter();

	PlaybackOneToManyExecutor(
		QueryRelationship self,
		QueryRelationship root,
		List<QueryRelationship> route,
		String sql,
		String countSQL,
		ComplementerValues values,
		Column[] selectedColumns) {
		super(self);
		this.root = root;
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
	List<QueryRelationship> route() {
		return route;
	}

	@Override
	QueryRelationship root() {
		return root;
	};

	@Override
	DataObjectIterator iterator() {
		return DataAccessHelper.select(
			sql,
			values,
			root.getRelationship(),
			selectedColumns,
			converter);
	}

	@Override
	public String sql() {
		return sql;
	}

	@Override
	public ComposedSQL toCountSQL() {
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
	public OneToManyExecutor<O, M> reproduce(Object... placeHolderValues) {
		return new PlaybackOneToManyExecutor<>(
			self(),
			root,
			route,
			sql,
			countSQL,
			values.reproduce(placeHolderValues),
			selectedColumns);
	}
}
