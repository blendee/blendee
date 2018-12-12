package org.blendee.assist;

import org.blendee.selector.Optimizer;
import org.blendee.sql.Criteria;
import org.blendee.sql.FromClause.JoinType;
import org.blendee.sql.GroupByClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.Relationship;
import org.blendee.sql.SQLQueryBuilder;

/**
 * 自動生成される検索ツールの振る舞いを定義したインターフェイスです。<br>
 * このインスタンスは、マルチスレッド環境で使用されることを想定されていません。
 * @author 千葉 哲嗣
 */
public interface SelectStatement extends Statement, SQLDecorators {

	/**
	 * 現時点での、このインスタンスが検索条件を持つかどうかを調べます。
	 * @return 検索条件を持つ場合、 true
	 */
	boolean hasWhereClause();

	/**
	 * 内部処理用なので直接使用しないこと。
	 * @return 現在の検索に使用する {@link Optimizer}
	 */
	Optimizer getOptimizer();

	/**
	 * 内部処理用なので直接使用しないこと。
	 * @return 現在の GROUP BY 句
	 */
	GroupByClause getGroupByClause();

	/**
	 * 内部処理用なので直接使用しないこと。
	 * @return 現在の ORDER BY 句
	 */
	OrderByClause getOrderByClause();

	/**
	 * 内部処理用なので直接使用しないこと。
	 * @return 現在の WHERE 句
	 */
	Criteria getWhereClause();

	/**
	 * この Query のルート {@link Relationship} を返します。
	 * @return ルート {@link Relationship}
	 */
	Relationship getRootRealtionship();

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
	 * 代わりに{@link Query#aggregate} で結果を取得することになります。
	 */
	void quitRowMode();

	/**
	 * 検索結果として {@link Row} を使用するモードかどうかを判定します。
	 * @return {@link Row} を使用するモードかどうか
	 */
	boolean rowMode();

	/**
	 * 引数の {@link SQLQueryBuilder} に自身のクエリ内容を JOIN させます。
	 * @param mainBuilder メイン側のクエリ
	 * @param joinType 結合タイプ
	 * @param onCriteria 結合条件
	 */
	void joinTo(SQLQueryBuilder mainBuilder, JoinType joinType, Criteria onCriteria);

	/**
	 * {@link SQLQueryBuilder} を生成します。
	 * @return {@link SQLQueryBuilder}
	 */
	SQLQueryBuilder toSQLQueryBuilder();

	/**
	 * {@link Query} を生成し、返します。
	 * @return {@link Query}
	 */
	Query<?, ?> query();
}
