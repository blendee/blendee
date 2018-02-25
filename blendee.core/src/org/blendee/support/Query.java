package org.blendee.support;

import java.util.Optional;
import java.util.function.Consumer;

import org.blendee.jdbc.Result;
import org.blendee.jdbc.ResultSetIterator;
import org.blendee.sql.Relationship;

/**
 * 自動生成される検索ツールの振る舞いを定義したインターフェイスです。<br>
 * このインスタンスは、マルチスレッド環境で使用されることを想定されていません。
 * @author 千葉 哲嗣
 */
public interface Query extends Executor<RowIterator<? extends Row>, Optional<? extends Row>> {

	/**
	 * 現時点での、このインスタンスが検索条件を持つかどうかを調べます。
	 * @return 検索条件を持つ場合、 true
	 */
	boolean hasWhereClause();

	/**
	 * この Query のルート {@link Relationship} を返します。
	 * @return ルート {@link Relationship}
	 */
	Relationship getRootRealtionship();

	/**
	 * この Query の WHERE 句用 {@link LogicalOperators} を返します。
	 * @return {@link LogicalOperators}
	 */
	LogicalOperators getWhereLogicalOperators();

	/**
	 * この Query の HAVING 句用 {@link LogicalOperators} を返します。
	 * @return {@link LogicalOperators}
	 */
	LogicalOperators getHavingLogicalOperators();

	/**
	 * 集合関数を使用するモードになります。
	 */
	void useAggregate();

	/**
	 * 集合関数を使用するモードかどうかを判定します。
	 * @return 集合関数を使用するモードかどうか
	 */
	boolean usesAggregate();

	/**
	 * 集合関数を含む検索を実行します。
	 * @param consumer {@link Consumer}
	 */
	void aggregate(Consumer<Result> consumer);

	/**
	 * 集合関数を含む検索を実行します。
	 * @return {@link ResultSetIterator}
	 */
	ResultSetIterator aggregate();
}
