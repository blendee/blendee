package org.blendee.support;

import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataObject;
import org.blendee.selector.Optimizer;
import org.blendee.sql.Criteria;
import org.blendee.sql.GroupByClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.OrderByClause.Direction;
import org.blendee.sql.OrderByClause.DirectionalColumn;
import org.blendee.sql.Relationship;
import org.blendee.support.SelectOfferFunction.SelectOffers;

/**
 * 自動生成される ConcreteQueryRelationship の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface QueryRelationship {

	/**
	 * {@link SelectOfferFunction} 内で使用する SELECT 句生成用メソッドです。<br>
	 * パラメータの項目を SELECT 句に割り当てます。
	 * @param offers SELECT 句に含めるテーブルおよびカラム
	 * @return SELECT 句
	 */
	default SelectOffers of(SelectOffer... offers) {
		SelectOffers visitor = new SelectOffers();
		for (SelectOffer offer : offers) {
			offer.accept(visitor);
		}

		return visitor;
	}

	/**
	 * {@link GroupByOfferFunction} 内で使用する GROUP BY 句生成用メソッドです。<br>
	 * パラメータの項目と順序を GROUP BY 句に割り当てます。
	 * @param offers GROUP BY 句に含めるテーブルおよびカラム
	 */
	default void of(GroupByOffer... offers) {
		for (GroupByOffer offer : offers) {
			offer.offer();
		}
	}

	/**
	 * {@link OrderByOfferFunction} 内で使用する ORDER BY 句生成用メソッドです。<br>
	 * パラメータの項目と順序を ORDER BY 句に割り当てます。
	 * @param offers ORDER BY 句に含めるテーブルおよびカラム
	 */
	default void of(OrderByOffer... offers) {
		for (OrderByOffer offer : offers) {
			offer.offer();
		}
	}

	/**
	 * SELECT 句に、このテーブルのカラムすべてを追加します。
	 * @return {@link SelectOffer}
	 */
	default SelectOffer all() {
		return offers -> offers.add(getRelationship().getColumns());
	}

	/**
	 * @param column
	 * @return {@link AliasOffer}
	 */
	default AliasOffer MAX(SelectQueryColumn<?> column) {
		getRoot().useAggregate();
		return new AliasOffer(new ColumnExpression("MAX({0})", column.column));
	}

	/**
	 * @param column
	 * @return
	 */
	default OrderByQueryColumn<?> MAX(OrderByQueryColumn<?> column) {
		getRoot().useAggregate();

		OrderByClause clause = column.relationship.getOrderByClause();
		return new OrderByQueryColumn<>(
			column,
			() -> clause.add(new DirectionalColumn(column.column, Direction.ASC), "MAX({0})"),
			() -> clause.add(new DirectionalColumn(column.column, Direction.DESC), "MAX({0})"));
	}

	default <O extends LogicalOperators> HavingQueryColumn<O> MAX(HavingQueryColumn<O> column) {
		getRoot().useAggregate();

		//TODO HAVING

		return column;
	}

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return QueryRelationship が WHERE 句用の場合、そのタイプに応じた {@link QueryCriteriaContext} 
	 */
	QueryCriteriaContext getContext();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスが表す {@link Relationship}
	 */
	Relationship getRelationship();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return 現在の検索に使用する {@link Optimizer}
	 */
	Optimizer getOptimizer();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return 現在の GROUP BY 句
	 */
	GroupByClause getGroupByClause();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return 現在の ORDER BY 句
	 */
	OrderByClause getOrderByClause();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @param criteria 現在の検索に使用する WHERE 句
	 */
	void setWhereClause(Criteria criteria);

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return 現在の WHERE 句
	 */
	Criteria getWhereClause();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @param criteria 現在の検索に使用する HAVING 句
	 */
	void setHavingClause(Criteria criteria);

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return 現在の HAVING 句
	 */
	Criteria getHavingClause();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスをメンバとして保持している親インスタンス
	 */
	QueryRelationship getParent();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスが表す {@link TablePath}
	 */
	TablePath getTablePath();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスの大元の {@link Query}
	 */
	Query getRoot();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @param data {@link Row} の全要素の値を持つ検索結果オブジェクト
	 * @return 生成された {@link Row}
	 */
	Row createRow(DataObject data);
}
