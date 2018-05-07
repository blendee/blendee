package org.blendee.support;

import java.util.Optional;

import org.blendee.jdbc.ComposedSQL;
import org.blendee.sql.Criteria;
import org.blendee.sql.FromClause.JoinType;
import org.blendee.sql.QueryBuilder;
import org.blendee.sql.Relationship;
import org.blendee.sql.SQLDecorator;

/**
 * 自動生成される検索ツールの振る舞いを定義したインターフェイスです。<br>
 * このインスタンスは、マルチスレッド環境で使用されることを想定されていません。
 * @author 千葉 哲嗣
 */
public interface Query extends Executor<RowIterator<? extends Row>, Optional<? extends Row>>, Aggregator {

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
	 * {@link ComposedSQL} を取得します。
	 * @return {@link ComposedSQL}
	 */
	ComposedSQL composeSQL();

	/**
	 * 引数の {@link QueryBuilder} に自身のクエリ内容を JOIN させます。
	 * @param mainBuilder メイン側のクエリ
	 * @param joinType 結合タイプ
	 * @param onCriteria 結合条件
	 */
	void joinTo(QueryBuilder mainBuilder, JoinType joinType, Criteria onCriteria);

	/**
	 * {@link Subquery} を生成します。
	 * @return {@link Subquery}
	 */
	Subquery toSubquery();

	/**
	 * 内部で保持している {@link SQLDecorator} を返します。
	 * @return {@link SQLDecorator}
	 */
	SQLDecorator[] decorators();

	Executor<RowIterator<? extends Row>, Optional<? extends Row>> executor();

	Aggregator aggregator();
}
