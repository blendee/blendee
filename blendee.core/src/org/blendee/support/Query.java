package org.blendee.support;

import java.util.Optional;

import org.blendee.sql.Condition;
import org.blendee.sql.OrderByClause;
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
	public boolean hasCondition();

	/**
	 * 新規に ORDER BY 句をセットします。
	 * @param clause 新 ORDER BY 句
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public void orderBy(OrderByClause clause);

	/**
	 * 現時点の WHERE 句に新たな条件を AND 結合します。<br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 * @param condition AND 結合する新条件
	 * @return {@link Query} 自身
	 */
	public Query and(Condition condition);

	/**
	 * 現時点の WHERE 句に新たな条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param condition OR 結合する新条件
	 * @return {@link Query} 自身
	 */
	public Query or(Condition condition);

	/**
	 * 現時点の WHERE 句に新たなサブクエリ条件を AND 結合します。<br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 * @param subquery AND 結合するサブクエリ条件
	 * @return {@link Query} 自身
	 */
	public Query and(Subquery subquery);

	/**
	 * 現時点の WHERE 句に新たなサブクエリ条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param subquery OR 結合するサブクエリ条件
	 * @return {@link Query} 自身
	 */
	public Query or(Subquery subquery);

	/**
	 * この Query のルート {@link Relationship} を返します。
	 * @return ルート {@link Relationship}
	 */
	public Relationship getRootRealtionship();

	/**
	 * この Query の {@link LogicalOperators} を返します。
	 * @return {@link LogicalOperators}
	 */
	public LogicalOperators getLogicalOperators();
}
