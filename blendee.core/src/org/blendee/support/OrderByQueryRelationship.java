package org.blendee.support;

import static org.blendee.support.QueryRelationshipConstants.AVG_TEMPLATE;
import static org.blendee.support.QueryRelationshipConstants.COUNT_TEMPLATE;
import static org.blendee.support.QueryRelationshipConstants.MAX_TEMPLATE;
import static org.blendee.support.QueryRelationshipConstants.MIN_TEMPLATE;
import static org.blendee.support.QueryRelationshipConstants.SUM_TEMPLATE;

import java.util.Arrays;
import java.util.Objects;

import org.blendee.sql.Column;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.OrderByClause.Direction;

/**
 * 自動生成される ConcreteQueryRelationship の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface OrderByQueryRelationship {

	/**
	 * {@link OrderByOfferFunction} 内で使用する ORDER BY 句生成用メソッドです。<br>
	 * パラメータの項目と順序を ORDER BY 句に割り当てます。
	 * @param offers ORDER BY 句に含めるテーブルおよびカラム
	 * @return offers
	 */
	default Offers<Offerable> list(Offerable... offers) {
		return ls(offers);
	}

	/**
	 * {@link #list} の短縮形です。
	 * @param offers ORDER BY 句に含めるテーブルおよびカラム
	 * @return offers
	 */
	default Offers<Offerable> ls(Offerable... offers) {
		return () -> Arrays.asList(offers);
	}

	/**
	 * ORDER BY 句用 AVG(column)
	 * @param column {@link OrderByQueryColumn}
	 * @return {@link OrderByQueryColumn}
	 */
	default AscDesc AVG(OrderByQueryColumn column) {
		return any(AVG_TEMPLATE, column);
	}

	/**
	 * ORDER BY 句用 SUM(column)
	 * @param column {@link OrderByQueryColumn}
	 * @return {@link OrderByQueryColumn}
	 */
	default AscDesc SUM(OrderByQueryColumn column) {
		return any(SUM_TEMPLATE, column);
	}

	/**
	 * ORDER BY 句用 MAX(column)
	 * @param column {@link OrderByQueryColumn}
	 * @return {@link OrderByQueryColumn}
	 */
	default AscDesc MAX(OrderByQueryColumn column) {
		return any(MAX_TEMPLATE, column);
	}

	/**
	 * ORDER BY 句用 MIN(column)
	 * @param column {@link OrderByQueryColumn}
	 * @return {@link OrderByQueryColumn}
	 */
	default AscDesc MIN(OrderByQueryColumn column) {
		return any(MIN_TEMPLATE, column);
	}

	/**
	 * ORDER BY 句用 COUNT(column)
	 * @param column {@link OrderByQueryColumn}
	 * @return {@link OrderByQueryColumn}
	 */
	default AscDesc COUNT(OrderByQueryColumn column) {
		return any(COUNT_TEMPLATE, column);
	}

	/**
	 * ORDER BY 句に任意のカラムを追加します。
	 * @param template カラムのテンプレート
	 * @param orderByColumns 使用するカラム
	 * @return {@link AscDesc} ASC か DESC
	 */
	default AscDesc any(String template, OrderByQueryColumn... orderByColumns) {
		Column[] columns = new Column[orderByColumns.length];
		for (int i = 0; i < orderByColumns.length; i++) {
			columns[i] = orderByColumns[i].column();
		}

		OrderByClause clause = getOrderByClause();
		return new AscDesc(
			new OrderByOffer(order -> clause.add(order, template, Direction.ASC, columns)),
			new OrderByOffer(order -> clause.add(order, template, Direction.DESC, columns)),
			new OrderByOffer(order -> clause.add(order, template, Direction.ASC_NULLS_FIRST, columns)),
			new OrderByOffer(order -> clause.add(order, template, Direction.ASC_NULLS_LAST, columns)),
			new OrderByOffer(order -> clause.add(order, template, Direction.DESC_NULLS_FIRST, columns)),
			new OrderByOffer(order -> clause.add(order, template, Direction.DESC_NULLS_LAST, columns)),
			new OrderByOffer(order -> clause.add(order, template, Direction.NONE, columns)));
	}

	/**
	 * 他のクエリと JOIN した際などの、最終的な順位を指定します。
	 * @param order 最終的な GROUP BY 句内での順序
	 * @param offer 対象カラム
	 * @return {@link GroupByQueryColumn}
	 */
	default OrderByOffer order(int order, Offerable offer) {
		Objects.requireNonNull(offer);

		return new OrderByOffer(offer, order);
	}

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return 現在の ORDER BY 句
	 */
	OrderByClause getOrderByClause();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスの大元の {@link Query}
	 */
	Query getRoot();
}
