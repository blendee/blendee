package org.blendee.support;

import java.util.HashSet;
import java.util.Set;

import org.blendee.internal.Traversable;
import org.blendee.internal.Traverser;
import org.blendee.internal.TraverserOperator;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.Relationship;
import org.blendee.sql.SubqueryException;

/**
 * 自動生成される {@link Query} で使用するサブクエリを表すクラスです。
 * @author 千葉 哲嗣
 */
public class Subquery {

	private final Query subquery;

	private final Relationship joinPoint;

	/**
	 * サブクエリとして使用する {@link Query} からたどれる {@link QueryRelationship} をもとにインスタンスを生成します。<br>
	 * joinPoint の大元となるサブクエリには、あらかじめ条件を設定しておく必要があります。<br>
	 * メインクエリのテーブルが、サブクエリの参照ツリーに複数存在した場合に、明示的に結合ポイントを指定するためのコンストラクタです。
	 * @param joinPoint 条件設定済みサブクエリ
	 */
	public Subquery(QueryRelationship joinPoint) {
		this.subquery = joinPoint.getRoot();
		this.joinPoint = joinPoint.getRelationship();
	}

	/**
	 * サブクエリとして使用する {@link Query} をもとにインスタンスを生成します。<br>
	 * サブクエリには、あらかじめ条件を設定しておく必要があります。<br>
	 * メインクエリのテーブルは、サブクエリの参照をたどって到達できる唯一のテーブルである必要があります。
	 * @param subquery 条件設定済みサブクエリ
	 */
	public Subquery(Query subquery) {
		this.subquery = subquery;
		joinPoint = null;
	}

	/**
	 * このサブクエリから、メインクエリで使用できる {@link Criteria} を生成します。
	 * @param mainquery メインクエリ
	 * @return {@link Criteria} となったサブクエリ
	 */
	public Criteria createCriteria(Query mainquery) {
		Relationship myJoinPoint = joinPoint == null
			? find(
				subquery.getRootRealtionship(),
				mainquery.getRootRealtionship().getTablePath())
			: joinPoint;

		return CriteriaFactory
			.createSubquery(
				mainquery.getRootRealtionship(),
				myJoinPoint,
				subquery.getCriteria());
	}

	private static Relationship find(Relationship root, TablePath target) {
		Set<Relationship> visited = new HashSet<>();

		Relationship[] finded = { null };
		TraverserOperator.operate(new Traverser() {

			@Override
			public TraverserOperator getOperator() {
				return DEPTH_FIRST;
			}

			@Override
			public void execute(Traversable traversable) {
				Relationship relation = (Relationship) traversable;

				if (visited.contains(relation)) throw new IllegalStateException("循環参照があるため、結合箇所が特定できません");
				visited.add(relation);

				if (target.equals(relation.getTablePath()))
					if (finded[0] != null)
						throw new SubqueryException(target + " が複数存在するため、結合箇所が特定できません");
				finded[0] = relation;
			}
		}, root);

		if (finded[0] == null) throw new SubqueryException(target + " との結合箇所が存在しませんでした");

		return finded[0];
	}
}
