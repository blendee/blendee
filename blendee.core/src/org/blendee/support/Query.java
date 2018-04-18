package org.blendee.support;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.blendee.jdbc.BlenResultSet;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.ResultSetIterator;
import org.blendee.sql.Criteria;
import org.blendee.sql.Effector;
import org.blendee.sql.FromClause.JoinType;
import org.blendee.sql.QueryBuilder;
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
	LogicalOperators<?> getWhereLogicalOperators();

	/**
	 * この Query の HAVING 句用 {@link LogicalOperators} を返します。
	 * @return {@link LogicalOperators}
	 */
	LogicalOperators<?> getHavingLogicalOperators();

	/**
	 * この Query の ON 句 (LEFT) 用 {@link LogicalOperators} を返します。
	 * @return {@link LogicalOperators}
	 */
	LogicalOperators<?> getOnLeftLogicalOperators();

	/**
	 * この Query の ON 句 (RIGHT) 用 {@link LogicalOperators} を返します。
	 * @return {@link LogicalOperators}
	 */
	LogicalOperators<?> getOnRightLogicalOperators();

	/**
	 * {@link Row} で検索結果を受け取ることができなくなります。<br>
	 * 代わりに{@link #aggregate} で結果を取得することになります。
	 */
	void quitRowMode();

	/**
	 * 検索結果として {@link Row} を使用するモードかどうかを判定します。
	 * @return {@link Row} を使用するモードかどうか
	 */
	boolean rowMode();

	/**
	 * 集合関数を含む検索を実行します。
	 * @param consumer {@link Consumer}
	 */
	void aggregate(Consumer<BlenResultSet> consumer);

	/**
	 * 集合関数を含む検索を実行します。
	 * @param function {@link Function}
	 * @return 任意の型の戻り値
	 */
	<T> T aggregateAndGet(Function<BlenResultSet, T> function);

	/**
	 * 集合関数を含む検索を実行します。
	 * @param options 検索オプション
	 * @param consumer {@link Consumer}
	 */
	void aggregate(Effectors options, Consumer<BlenResultSet> consumer);

	/**
	 * 集合関数を含む検索を実行します。
	 * @param options 検索オプション
	 * @param function {@link Function}
	 * @return 任意の型の戻り値
	 */
	<T> T aggregateAndGet(Effectors options, Function<BlenResultSet, T> function);

	/**
	 * 集合関数を含む検索を実行します。
	 * @param options 検索オプション
	 * @return {@link ResultSetIterator}
	 */
	ResultSetIterator aggregate(Effector... options);

	/**
	 * {@link ComposedSQL} を取得します。
	 * @param options 検索オプション
	 * @return {@link ComposedSQL}
	 */
	ComposedSQL composeSQL(Effector... options);

	/**
	 * 引数の {@link QueryBuilder} に自身のクエリ内容を JOIN させます。
	 * @param mainBuilder メイン側のクエリ
	 * @param joinType 結合タイプ
	 * @param onCriteria 結合条件
	 */
	void joinTo(QueryBuilder mainBuilder, JoinType joinType, Criteria onCriteria);

	/**
	 * {@link Subquery} を生成します。
	 * @param options 検索オプション
	 * @return {@link Subquery}
	 */
	Subquery toSubquery(Effector... options);
}
