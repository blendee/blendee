package org.blendee.support;

import java.util.List;

import org.blendee.internal.U;
import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.jdbc.BlenResultSet;
import org.blendee.jdbc.BlenStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObjectIterator;
import org.blendee.selector.SelectedValuesConverter;
import org.blendee.selector.SimpleSelectedValuesConverter;
import org.blendee.sql.Column;

/**
 * 検索条件と並び替え条件を保持した、実際に検索を行うためのクラスです。<br>
 * {@link Executor} との違いは、参照する側のテーブルの {@link Query} を使用し、参照される側を辿り、そこで検索することで {@link Row} を一対多で取得することができるようにするということです。
 * @author 千葉 哲嗣
 * @param <O> One 一対多の一側の型
 * @param <M> Many 一対多の多側の型連鎖
 */
public class PlaybackOneToManyExecutor<O extends Row, M>
	extends OneToManyExecutor<O, M> {

	private final QueryRelationship root;

	private final List<QueryRelationship> route;

	private final String sql;

	private final String countSQL;

	private final PreparedStatementComplementer complementer;

	private final Column[] selectedColumns;

	private final SelectedValuesConverter converter = new SimpleSelectedValuesConverter();

	/**
	 * 自動生成されたサブクラス用のコンストラクタです。
	 * @param self 中心となるテーブルを表す
	 */
	protected PlaybackOneToManyExecutor(
		QueryRelationship self,
		QueryRelationship root,
		List<QueryRelationship> route,
		String sql,
		String countSQL,
		PreparedStatementComplementer complementer,
		Column[] selectedColumns) {
		super(self);
		this.root = root;
		this.route = route;
		this.sql = sql;
		this.countSQL = countSQL;
		this.complementer = complementer;
		this.selectedColumns = selectedColumns;
	}

	@Override
	public int count() {
		try (BlenStatement statement = BlendeeManager
			.getConnection()
			.getStatement(countSQL, complementer)) {
			try (BlenResultSet result = statement.executeQuery()) {
				result.next();
				return result.getInt(1);
			}
		}
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
			countSQL,
			complementer,
			self().getRelationship(),
			selectedColumns,
			converter);
	}

	@Override
	public String sql() {
		return sql;
	}

	@Override
	public int complement(int done, BlenPreparedStatement statement) {
		complementer.complement(statement);
		return Integer.MIN_VALUE;
	}

	@Override
	public OneToManyExecutor<O, M> reproduce(PreparedStatementComplementer complementer) {
		return new PlaybackOneToManyExecutor<>(
			self(),
			root,
			route,
			sql,
			countSQL,
			complementer,
			selectedColumns);
	}
}
