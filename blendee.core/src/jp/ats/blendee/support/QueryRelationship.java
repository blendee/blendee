package jp.ats.blendee.support;

import java.util.Arrays;

import jp.ats.blendee.jdbc.ResourceLocator;
import jp.ats.blendee.orm.DataObject;
import jp.ats.blendee.selector.Optimizer;
import jp.ats.blendee.sql.Condition;
import jp.ats.blendee.sql.OrderByClause;
import jp.ats.blendee.sql.Relationship;
import jp.ats.blendee.support.SelectOfferFunction.SelectOffers;

/**
 * 自動生成される ConcreteQueryRelationship の振る舞いを定義したインターフェイスです。
 * <br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 *
 * @author 千葉 哲嗣
 */
public interface QueryRelationship {

	/**
	 * {@link SelectOfferFunction} 内で使用する SELECT 句生成用メソッドです。
	 *
	 * @param offers SELECT 句に含めるテーブルおよびカラム
	 * @return SELECT 句
	 */
	default SelectOffers assign(SelectOffer... offers) {
		SelectOffers visitor = new SelectOffers();
		Arrays.asList(offers).forEach(offer -> offer.accept(visitor));
		return visitor;
	}

	/**
	 * {@link SelectOfferFunction} 内で使用する SELECT 句生成用メソッドです。
	 *
	 * @param offers SELECT 句に含めるテーブルおよびカラム
	 */
	default void assign(OrderByOffer... offers) {
		Arrays.asList(offers).forEach(offer -> offer.offer());
	}

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 *
	 * @return QueryRelationship が WHERE 句用の場合、そのタイプに応じた {@link QueryConditionContext} 
	 */
	QueryConditionContext getContext();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 *
	 * @return このインスタンスが表す {@link Relationship}
	 */
	Relationship getRelationship();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 *
	 * @return 現在の検索に使用する {@link Optimizer}
	 */
	Optimizer getOptimizer();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 *
	 * @return 現在の ORDER BY 句
	 */
	OrderByClause getOrderByClause();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 *
	 * @param condition 現在の検索に使用する WHERE 句
	 */
	void setWhereClause(Condition condition);

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 *
	 * @return 現在の WHERE 句
	 */
	Condition getWhereClause();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 *
	 * @return このインスタンスをメンバとして保持している親インスタンス
	 */
	QueryRelationship getParent();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 *
	 * @return このインスタンスが表す {@link ResourceLocator}
	 */
	ResourceLocator getResourceLocator();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 *
	 * @return このインスタンスの大元の {@link Query}
	 */
	Query getRoot();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 *
	 * @param data {@link BEntity} の全要素の値を持つ検索結果オブジェクト
	 * @return 生成された {@link BEntity}
	 */
	BEntity createEntity(DataObject data);
}
