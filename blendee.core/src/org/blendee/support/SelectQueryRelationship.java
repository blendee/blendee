package org.blendee.support;

import static org.blendee.support.QueryRelationshipConstants.AVG_TEMPLATE;
import static org.blendee.support.QueryRelationshipConstants.COUNT_TEMPLATE;
import static org.blendee.support.QueryRelationshipConstants.MAX_TEMPLATE;
import static org.blendee.support.QueryRelationshipConstants.MIN_TEMPLATE;
import static org.blendee.support.QueryRelationshipConstants.SUM_TEMPLATE;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.blendee.sql.Column;
import org.blendee.sql.PseudoColumn;
import org.blendee.sql.Relationship;
import org.blendee.support.SelectOfferFunction.SelectOffers;

/**
 * 自動生成される ConcreteQueryRelationship の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface SelectQueryRelationship {

	/**
	 * {@link SelectOfferFunction} 内で使用する SELECT 句生成用メソッドです。<br>
	 * パラメータの項目を SELECT 句に割り当てます。
	 * @param offers SELECT 句に含めるテーブルおよびカラム
	 * @return SELECT 句
	 */
	default SelectOffer list(SelectOffer... offers) {
		SelectOffers visitor = new SelectOffers();
		for (SelectOffer offer : offers) {
			offer.get().forEach(c -> visitor.add(c));
		}

		return visitor;
	}

	/**
	 * SELECT 句に、このテーブルのカラムすべてを追加します。
	 * @return {@link SelectOffer}
	 */
	default SelectOffer all() {
		return new SelectOffer() {

			@Override
			public List<ColumnExpression> get() {
				return Arrays.stream(getRelationship().getColumns())
					.map(c -> new ColumnExpression(c))
					.collect(Collectors.toList());
			}

			@Override
			public void accept(SelectOffers offers) {
				offers.add(getRelationship().getColumns());
			}
		};
	}

	/**
	 * SELECT 句用 AVG(column)
	 * @param column {@link SelectQueryColumn}
	 * @return {@link ColumnExpression}
	 */
	default AliasableOffer AVG(SelectQueryColumn column) {
		return any(AVG_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 SUM(column)
	 * @param column {@link SelectQueryColumn}
	 * @return {@link ColumnExpression}
	 */
	default AliasableOffer SUM(SelectQueryColumn column) {
		return any(SUM_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 MAX(column)
	 * @param column {@link SelectQueryColumn}
	 * @return {@link ColumnExpression}
	 */
	default AliasableOffer MAX(SelectQueryColumn column) {
		return any(MAX_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 MIN(column)
	 * @param column {@link SelectQueryColumn}
	 * @return {@link ColumnExpression}
	 */
	default AliasableOffer MIN(SelectQueryColumn column) {
		return any(MIN_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 COUNT(*)
	 * @return {@link ColumnExpression}
	 */
	default AliasableOffer COUNT() {
		return new ColumnExpression(COUNT_TEMPLATE, new PseudoColumn(getRelationship(), "*", false));
	}

	/**
	 * SELECT 句用 COUNT(column)
	 * @param column {@link SelectQueryColumn}
	 * @return {@link ColumnExpression}
	 */
	default AliasableOffer COUNT(SelectQueryColumn column) {
		return any(COUNT_TEMPLATE, column);
	}

	/**
	 * SELECT 句に任意のカラムを追加します。
	 * @param template カラムのテンプレート
	 * @param selectColumns 使用するカラム
	 * @return {@link ColumnExpression} AS
	 */
	default AliasableOffer any(String template, SelectQueryColumn... selectColumns) {
		getRoot().quitRowMode();

		Column[] columns = new Column[selectColumns.length];
		for (int i = 0; i < selectColumns.length; i++) {
			columns[i] = selectColumns[i].column();
		}

		return new ColumnExpression(template, columns);
	}

	/**
	 * SELECT 句に任意の文字列を追加します。
	 * @param expression 文字列表現
	 * @return {@link ColumnExpression} AS
	 */
	default AliasableOffer any(String expression) {
		getRoot().quitRowMode();
		Column[] columns = { new PseudoColumn(getRelationship(), expression, false) };
		return new ColumnExpression("{0}", columns);
	}

	/**
	 * SELECT 句に任意の数値を追加します。
	 * @param number 文字列表現
	 * @return {@link ColumnExpression} AS
	 */
	default AliasableOffer any(Number number) {
		getRoot().quitRowMode();
		Column[] columns = { new PseudoColumn(getRelationship(), number.toString(), false) };
		return new ColumnExpression("{0}", columns);
	}

	/**
	 * SELECT 句に * を追加します。
	 * @return {@link ColumnExpression} AS
	 */
	default SelectOffer asterisk() {
		getRoot().quitRowMode();
		Column[] columns = { new PseudoColumn(getRelationship(), "*", true) };

		SelectOffers offers = new SelectOffers();
		offers.add(new ColumnExpression("{0}", columns));

		return offers;
	}

	/**
	 * SELECT 句に * を追加します。
	 * @return {@link ColumnExpression} AS
	 */
	default SelectOffer asteriskAll() {
		getRoot().quitRowMode();
		Column[] columns = { new PseudoColumn(getRelationship(), "*", false) };

		SelectOffers offers = new SelectOffers();
		offers.add(new ColumnExpression("{0}", columns));

		return offers;
	}

	/**
	 * 他のクエリと JOIN した際などの、最終的な順位を指定します。
	 * @param order 最終的な GROUP BY 句内での順序
	 * @param offer 対象カラム
	 * @return {@link GroupByQueryColumn}
	 */
	default SelectOffer order(int order, SelectOffer offer) {
		SelectOffers offers = new SelectOffers();
		offer.get().forEach(c -> {
			c.order(order);
			offers.add(c);
		});

		return offers;
	}

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスが表す {@link Relationship}
	 */
	Relationship getRelationship();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスの大元の {@link Query}
	 */
	Query getRoot();
}
