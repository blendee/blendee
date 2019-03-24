package org.blendee.assist;

import java.util.List;
import java.util.Optional;

import org.blendee.internal.U;
import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.orm.DataObjectIterator;
import org.blendee.sql.Bindable;
import org.blendee.sql.Column;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.RuntimeId;

/**
 * 検索条件と並び替え条件を保持した、実際に検索を行うためのクラスです。<br>
 * {@link Query} との違いは、参照する側のテーブルの {@link SelectStatement} を使用し、参照される側を辿り、そこで検索することで {@link Row} を一対多で取得することができるようにするということです。
 * @author 千葉 哲嗣
 * @param <O> One 一対多の一側の型
 * @param <M> Many 一対多の多側の型連鎖
 */
public abstract class OneToManyQuery<O extends Row, M>
	implements Query<Many<O, M>, One<O, M>> {

	private final OneToManyBehavior self;

	/**
	 * 自動生成されたサブクラス用のコンストラクタです。
	 * @param relation 中心となるテーブルを表す
	 */
	OneToManyQuery(OneToManyBehavior relation) {
		self = relation;
	}

	OneToManyBehavior self() {
		return self;
	}

	abstract RuntimeId runtimeId();

	/**
	 * 検索を実行します。
	 * @return 検索結果
	 */
	abstract DataObjectIterator iterator();

	abstract List<OneToManyBehavior> route();

	abstract BStatement createStatementForCount();

	@Override
	public Many<O, M> retrieve() {
		List<OneToManyBehavior> route = route();
		return new Many<>(
			new DataObjectManager(iterator(), route),
			null,
			self,
			route);
	}

	@Override
	public Optional<One<O, M>> fetch(Bindable... primaryKeyMembers) {
		Column[] columns = self.getRelationship().getPrimaryKeyColumns();

		if (columns.length != primaryKeyMembers.length)
			//primaryKeyMembers の数が正しくありません
			throw new IllegalArgumentException("The number of \"primaryKeyMembers\" is incorrect.");

		Criteria criteria = new CriteriaFactory(runtimeId()).create();
		for (int i = 0; i < columns.length; i++) {
			criteria.and(columns[i].getCriteria(runtimeId(), primaryKeyMembers[i]));
		}

		List<OneToManyBehavior> route = route();

		return Helper.unique(
			new Many<>(
				new DataObjectManager(iterator(), route),
				null,
				self,
				route));
	}

	@Override
	public int count() {
		try (BStatement statement = createStatementForCount()) {
			try (BResultSet result = statement.executeQuery()) {
				result.next();
				return result.getInt(1);
			}
		}
	}

	@Override
	public boolean rowMode() {
		return true;
	}

	@Override
	public ComposedSQL aggregateSQL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
