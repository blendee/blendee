package jp.ats.blendee.support;

import java.util.Optional;

import jp.ats.blendee.sql.Condition;
import jp.ats.blendee.sql.OrderByClause;
import jp.ats.blendee.sql.Relationship;

/**
 * 自動生成される検索ツールの振る舞いを定義したインターフェイスです。
 * <br>
 * このインスタンスは、マルチスレッド環境で使用されることを想定されていません。
 *
 * @author 千葉 哲嗣
 */
public interface Query extends Executor<DTOIterator<? extends DTO>, Optional<? extends DTO>> {

	/**
	 * 現時点での、このインスタンスが検索条件を持つかどうかを調べます。
	 *
	 * @return 検索条件を持つ場合、 true
	 */
	public boolean hasCondition();

	/**
	 * 新規に ORDER BY 句をセットします。
	 *
	 * @param clause 新 ORDER BY 句
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public void orderBy(OrderByClause clause);

	/**
	 * 新規に WHERE 句をセットします。
	 *
	 * @param condition 新 WHERE 句
	 * @throws IllegalStateException 既に WHERE 句がセットされている場合
	 */
	public void where(Condition condition);

	/**
	 * 現時点の WHERE 句に新たな条件を AND 結合します。
	 * <br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 *
	 * @param condition AND 結合する新条件
	 */
	public void and(Condition condition);

	/**
	 * 現時点の WHERE 句に新たな条件を OR 結合します。
	 * <br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 *
	 * @param condition OR 結合する新条件
	 */
	public void or(Condition condition);

	/**
	 * 新規にサブクエリを WHERE 句にセットします。
	 *
	 * @param subquery 新 WHERE 句
	 * @throws IllegalStateException 既に WHERE 句がセットされている場合
	 */
	public void where(Subquery subquery);

	/**
	 * 現時点の WHERE 句に新たなサブクエリ条件を AND 結合します。
	 * <br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 *
	 * @param subquery AND 結合するサブクエリ条件
	 */
	public void and(Subquery subquery);

	/**
	 * 現時点の WHERE 句に新たなサブクエリ条件を OR 結合します。
	 * <br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 *
	 * @param subquery OR 結合するサブクエリ条件
	 */
	public void or(Subquery subquery);

	/**
	 * この Query のルート {@link Relationship} を返します。
	 *
	 * @return ルート {@link Relationship}
	 */
	public Relationship getRootRealtionship();
}
