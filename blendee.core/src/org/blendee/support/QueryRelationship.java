package org.blendee.support;

import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataObject;
import org.blendee.selector.Optimizer;
import org.blendee.sql.Column;
import org.blendee.sql.Criteria;
import org.blendee.sql.GroupByClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.OrderByClause.Direction;
import org.blendee.sql.Relationship;
import org.blendee.sql.TemplateColumn;
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
		return fn("MAX({0})", column);
	}

	/**
	 * @param column
	 * @return {@link OrderByQueryColumn}
	 */
	default AscDesc MAX(OrderByQueryColumn<?> column) {
		return fn("MAX({0})", column);
	}

	default <O extends LogicalOperators> HavingQueryColumn<O> MAX(HavingQueryColumn<O> column) {
		return fn("MAX({0})", column);
	}

	/**
	 * @param column
	 * @return {@link AliasOffer}
	 */
	default AliasOffer fn(String template, SelectQueryColumn<?>... selectColumns) {
		getRoot().useAggregate();

		Column[] columns = new Column[selectColumns.length];
		for (int i = 0; i < selectColumns.length; i++) {
			columns[i] = selectColumns[i].column;
		}

		return new AliasOffer(new ColumnExpression(template, columns));
	}

	/**
	 * @param column
	 * @return {@link OrderByQueryColumn}
	 */
	default AscDesc fn(String template, OrderByQueryColumn<?>... orderByColumns) {
		getRoot().useAggregate();

		Column[] columns = new Column[orderByColumns.length];
		for (int i = 0; i < orderByColumns.length; i++) {
			columns[i] = orderByColumns[i].column;
		}

		OrderByClause clause = getOrderByClause();
		return new AscDesc(
			() -> clause.add(template, Direction.ASC, columns),
			() -> clause.add(template, Direction.ASC, columns));
	}

	default <O extends LogicalOperators> HavingQueryColumn<O> fn(String template, HavingQueryColumn<O> column) {
		getRoot().useAggregate();
		return new HavingQueryColumn<>(column.relationship, new TemplateColumn(template, column.column()));
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
