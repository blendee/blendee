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
	default Offers<ColumnExpression> list(SelectOffer... offers) {
		SelectOffers visitor = new SelectOffers();
		for (SelectOffer offer : offers) {
			offer.accept(visitor);
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
				return Arrays.asList(getRelationship().getColumns())
					.stream()
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
	 * @return {@link AliasOffer}
	 */
	default AliasOffer AVG(SelectQueryColumn<?> column) {
		return any(AVG_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 SUM(column)
	 * @param column {@link SelectQueryColumn}
	 * @return {@link AliasOffer}
	 */
	default AliasOffer SUM(SelectQueryColumn<?> column) {
		return any(SUM_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 MAX(column)
	 * @param column {@link SelectQueryColumn}
	 * @return {@link AliasOffer}
	 */
	default AliasOffer MAX(SelectQueryColumn<?> column) {
		return any(MAX_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 MIN(column)
	 * @param column {@link SelectQueryColumn}
	 * @return {@link AliasOffer}
	 */
	default AliasOffer MIN(SelectQueryColumn<?> column) {
		return any(MIN_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 COUNT(column)
	 * @param column {@link SelectQueryColumn}
	 * @return {@link AliasOffer}
	 */
	default AliasOffer COUNT(SelectQueryColumn<?> column) {
		return any(COUNT_TEMPLATE, column);
	}

	/**
	 * SELECT 句に任意のカラムを追加します。
	 * @param template カラムのテンプレート
	 * @param selectColumns 使用するカラム
	 * @return {@link AliasOffer} AS
	 */
	default AliasOffer any(String template, SelectQueryColumn<?>... selectColumns) {
		getRoot().quitRowMode();

		Column[] columns = new Column[selectColumns.length];
		for (int i = 0; i < selectColumns.length; i++) {
			columns[i] = selectColumns[i].column;
		}

		return new AliasOffer(new ColumnExpression(template, columns));
	}

	/**
	 * SELECT 句に任意の文字列を追加します。
	 * @param expression 文字列表現
	 * @return {@link AliasOffer} AS
	 */
	default AliasOffer any(String expression) {
		getRoot().quitRowMode();
		Column[] columns = { new PseudoColumn(getRelationship(), expression, false) };
		return new AliasOffer(new ColumnExpression("{0}", columns));
	}

	/**
	 * SELECT 句に任意の数値を追加します。
	 * @param number 文字列表現
	 * @return {@link AliasOffer} AS
	 */
	default AliasOffer any(Number number) {
		getRoot().quitRowMode();
		Column[] columns = { new PseudoColumn(getRelationship(), number.toString(), false) };
		return new AliasOffer(new ColumnExpression("{0}", columns));
	}

	/**
	 * SELECT 句に * を追加します。
	 * @return {@link AliasOffer} AS
	 */
	default SelectOffers asterisk() {
		getRoot().quitRowMode();
		Column[] columns = { new PseudoColumn(getRelationship(), "*", true) };

		SelectOffers offers = new SelectOffers();
		offers.add(new ColumnExpression("{0}", columns));

		return offers;
	}

	/**
	 * SELECT 句に * を追加します。
	 * @return {@link AliasOffer} AS
	 */
	default SelectOffers asteriskAll() {
		getRoot().quitRowMode();
		Column[] columns = { new PseudoColumn(getRelationship(), "*", false) };

		SelectOffers offers = new SelectOffers();
		offers.add(new ColumnExpression("{0}", columns));

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
