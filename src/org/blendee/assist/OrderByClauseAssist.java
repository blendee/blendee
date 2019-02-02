package org.blendee.assist;

import static org.blendee.assist.Helper.AVG_TEMPLATE;
import static org.blendee.assist.Helper.COUNT_TEMPLATE;
import static org.blendee.assist.Helper.MAX_TEMPLATE;
import static org.blendee.assist.Helper.MIN_TEMPLATE;
import static org.blendee.assist.Helper.SUM_TEMPLATE;

import java.util.Arrays;

import org.blendee.sql.Column;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.OrderByClause.Direction;

/**
 * 自動生成される TableFacade の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface OrderByClauseAssist extends ColumnMaker {

	/**
	 * {@link OrderByOfferFunction} 内で使用する ORDER BY 句生成用メソッドです。<br>
	 * パラメータの項目と順序を ORDER BY 句に割り当てます。
	 * @param offers ORDER BY 句に含めるテーブルおよびカラム
	 * @return offers
	 */
	default Offers<Offer> list(Offer... offers) {
		return ls(offers);
	}

	/**
	 * {@link #list} の短縮形です。
	 * @param offers ORDER BY 句に含めるテーブルおよびカラム
	 * @return offers
	 */
	default Offers<Offer> ls(Offer... offers) {
		return () -> Arrays.asList(offers);
	}

	/**
	 * ORDER BY 句用 AVG(column)
	 * @param column {@link OrderByColumn}
	 * @return {@link OrderByColumn}
	 */
	default AscDesc AVG(AssistColumn column) {
		return any(AVG_TEMPLATE, column);
	}

	/**
	 * ORDER BY 句用 SUM(column)
	 * @param column {@link OrderByColumn}
	 * @return {@link OrderByColumn}
	 */
	default AscDesc SUM(AssistColumn column) {
		return any(SUM_TEMPLATE, column);
	}

	/**
	 * ORDER BY 句用 MAX(column)
	 * @param column {@link OrderByColumn}
	 * @return {@link OrderByColumn}
	 */
	default AscDesc MAX(AssistColumn column) {
		return any(MAX_TEMPLATE, column);
	}

	/**
	 * ORDER BY 句用 MIN(column)
	 * @param column {@link OrderByColumn}
	 * @return {@link OrderByColumn}
	 */
	default AscDesc MIN(AssistColumn column) {
		return any(MIN_TEMPLATE, column);
	}

	/**
	 * ORDER BY 句用 COUNT(column)
	 * @param column {@link OrderByColumn}
	 * @return {@link OrderByColumn}
	 */
	default AscDesc COUNT(AssistColumn column) {
		return any(COUNT_TEMPLATE, column);
	}

	/**
	 * ORDER BY 句に任意のカラムを追加します。
	 * @param template カラムのテンプレート
	 * @param orderByColumns 使用するカラム
	 * @return {@link AscDesc} ASC か DESC
	 */
	default AscDesc any(String template, AssistColumn... orderByColumns) {
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
	 * @return {@link GroupByColumn}
	 */
	default OrderByOffer order(int order, Offer offer) {
		return new OrderByOffer(offer, order);
	}

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return 現在の ORDER BY 句
	 */
	OrderByClause getOrderByClause();

	@Override
	default Statement statement() {
		return getSelectStatement();
	}

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスの大元の {@link SelectStatement}
	 */
	SelectStatement getSelectStatement();
}
