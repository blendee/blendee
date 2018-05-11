package org.blendee.support;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.blendee.internal.U;
import org.blendee.orm.DataObjectIterator;
import org.blendee.sql.Bindable;
import org.blendee.sql.Column;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;

/**
 * 検索条件と並び替え条件を保持した、実際に検索を行うためのクラスです。<br>
 * {@link Executor} との違いは、参照する側のテーブルの {@link Query} を使用し、参照される側を辿り、そこで検索することで {@link Row} を一対多で取得することができるようにするということです。
 * @author 千葉 哲嗣
 * @param <O> One　一対多の一側の型
 * @param <M> Many　一対多の多側の型連鎖
 */
abstract class OneToManyExecutor<O extends Row, M>
	implements Executor<Many<O, M>, One<O, M>> {

	private final QueryRelationship root;

	private final QueryRelationship self;

	private final LinkedList<QueryRelationship> route;

	/**
	 * 自動生成されたサブクラス用のコンストラクタです。
	 * @param relation 中心となるテーブルを表す
	 */
	protected OneToManyExecutor(QueryRelationship relation) {
		self = relation;
		route = new LinkedList<>();
		root = getRoot(relation, route);

		//1->n順をn->1順に変える
		Collections.reverse(route);
	}

	/**
	 * 検索を実行します。
	 * @return 検索結果
	 */
	abstract DataObjectIterator getIterator();

	@Override
	public Many<O, M> execute() {
		return new Many<>(
			new DataObjectManager(getIterator(), route),
			null,
			self,
			route);
	}

	@Override
	public Optional<One<O, M>> fetch(Bindable... primaryKeyMembers) {
		Column[] columns = self.getRelationship().getPrimaryKeyColumns();

		if (columns.length != primaryKeyMembers.length)
			throw new IllegalArgumentException("primaryKeyMembers の数が正しくありません");

		Criteria criteria = CriteriaFactory.create();
		for (int i = 0; i < columns.length; i++) {
			criteria.and(columns[i].getCriteria(primaryKeyMembers[i]));
		}

		return Unique.get(
			new Many<>(
				new DataObjectManager(getIterator(), route),
				null,
				self,
				route));
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	protected QueryRelationship root() {
		return root;
	}

	private static QueryRelationship getRoot(QueryRelationship relation, List<QueryRelationship> relations) {
		relations.add(relation);
		QueryRelationship parent = relation.getParent();
		if (parent == null) return relation;
		return getRoot(parent, relations);
	}
}
