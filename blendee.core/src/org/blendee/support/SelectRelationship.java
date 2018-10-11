package org.blendee.support;

import static org.blendee.support.RelationshipConstants.AVG_TEMPLATE;
import static org.blendee.support.RelationshipConstants.COUNT_TEMPLATE;
import static org.blendee.support.RelationshipConstants.MAX_TEMPLATE;
import static org.blendee.support.RelationshipConstants.MIN_TEMPLATE;
import static org.blendee.support.RelationshipConstants.SUM_TEMPLATE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.blendee.sql.Column;
import org.blendee.sql.PseudoColumn;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.Relationship;
import org.blendee.sql.SQLQueryBuilder;
import org.blendee.support.SelectOfferFunction.SelectOffers;

/**
 * 自動生成される ConcreteQueryRelationship の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface SelectRelationship {

	/**
	 * {@link SelectOfferFunction} 内で使用する SELECT 句生成用メソッドです。<br>
	 * パラメータの項目を SELECT 句に割り当てます。
	 * @param offers SELECT 句に含めるテーブルおよびカラム
	 * @return SELECT 句
	 */
	default SelectOffer list(SelectOffer... offers) {
		return ls(offers);
	}

	/**
	 * {@link #list} の短縮形です。
	 * @param offers SELECT 句に含めるテーブルおよびカラム
	 * @return SELECT 句
	 */
	default SelectOffer ls(SelectOffer... offers) {
		SelectOffers visitor = new SelectOffers(getSelectStatement().getQueryId());
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
				RuntimeId id = getSelectStatement().getQueryId();
				return Arrays.stream(getRelationship().getColumns())
					.map(c -> new ColumnExpression(id, c))
					.collect(Collectors.toList());
			}
		};
	}

	/**
	 * SELECT 句に、このテーブルのカラムすべてを追加します。
	 * @param relationship このテーブルから辿れるテーブル
	 * @return {@link SelectOffer}
	 */
	default SelectOffer all(TableFacadeRelationship relationship) {
		return new SelectOffer() {

			@Override
			public List<ColumnExpression> get() {
				RuntimeId id = getSelectStatement().getQueryId();
				return Arrays.stream(relationship.getRelationship().getColumns())
					.map(c -> new ColumnExpression(id, c))
					.collect(Collectors.toList());
			}
		};
	}

	/**
	 * SELECT 句用 AVG(column)
	 * @param column {@link SelectColumn}
	 * @return {@link ColumnExpression}
	 */
	default AliasableOffer AVG(AliasableOffer column) {
		return any(AVG_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 SUM(column)
	 * @param column {@link AliasableOffer}
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer SUM(AliasableOffer column) {
		return any(SUM_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 MAX(column)
	 * @param column {@link AliasableOffer}
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer MAX(AliasableOffer column) {
		return any(MAX_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 MIN(column)
	 * @param column {@link AliasableOffer}
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer MIN(AliasableOffer column) {
		return any(MIN_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 COUNT(*)
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer COUNT() {
		return new ColumnExpression(getSelectStatement().getQueryId(), COUNT_TEMPLATE, new PseudoColumn(getRelationship(), "*", false));
	}

	/**
	 * SELECT 句用 COUNT(column)
	 * @param column {@link AliasableOffer}
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer COUNT(AliasableOffer column) {
		return any(COUNT_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 COALESCE
	 * @param columns {@link AliasableOffer}
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer COALESCE(AliasableOffer... columns) {
		return any(Coalesce.createTemplate(columns.length), columns);
	}

	/**
	 * SELECT 句用 COALESCE
	 * @param columns {@link AliasableOffer}
	 * @param values カラム以外の要素
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer COALESCE(Vargs<AliasableOffer> columns, Object... values) {
		List<AliasableOffer> all = new ArrayList<>();
		for (AliasableOffer column : columns.get()) {
			all.add(column);
		}

		for (Object value : values) {
			all.add(new ColumnExpression(getSelectStatement().getQueryId(), "{0}", new PseudoColumn(getRelationship(), value.toString(), false)));
		}

		int size = all.size();

		return any(
			Coalesce.createTemplate(size),
			all.toArray(new AliasableOffer[size]));
	}

	/**
	 * SELECT 句用 COALESCE
	 * @param column {@link AliasableOffer}
	 * @param values カラム以外の要素
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer COALESCE(AliasableOffer column, Object... values) {
		List<AliasableOffer> all = new ArrayList<>();
		all.add(column);

		for (Object value : values) {
			all.add(new ColumnExpression(getSelectStatement().getQueryId(), "{0}", new PseudoColumn(getRelationship(), value.toString(), false)));
		}

		int size = all.size();

		return any(
			Coalesce.createTemplate(size),
			all.toArray(new AliasableOffer[size]));
	}

	/**
	 * SELECT 句に任意のカラムを追加します。
	 * @param template カラムのテンプレート
	 * @param selectColumns 使用するカラム
	 * @return {@link AliasableOffer} AS
	 */
	default AliasableOffer any(String template, AliasableOffer... selectColumns) {
		getSelectStatement().quitRowMode();

		Column[] columns = new Column[selectColumns.length];
		for (int i = 0; i < selectColumns.length; i++) {
			columns[i] = selectColumns[i].column();
		}

		return new ColumnExpression(getSelectStatement().getQueryId(), template, columns);
	}

	/**
	 * SELECT 句に任意の文字列を追加します。
	 * @param expression 文字列表現
	 * @return {@link AliasableOffer} AS
	 */
	default AliasableOffer any(String expression) {
		getSelectStatement().quitRowMode();
		Column[] columns = { new PseudoColumn(getRelationship(), expression, false) };
		return new ColumnExpression(getSelectStatement().getQueryId(), "{0}", columns);
	}

	/**
	 * SELECT 句に任意の数値を追加します。
	 * @param number 文字列表現
	 * @return {@link AliasableOffer} AS
	 */
	default AliasableOffer any(Number number) {
		getSelectStatement().quitRowMode();
		Column[] columns = { new PseudoColumn(getRelationship(), number.toString(), false) };
		return new ColumnExpression(getSelectStatement().getQueryId(), "{0}", columns);
	}

	/**
	 * SELECT 句に任意のサブクエリを追加します。
	 * @param subquery サブクエリ
	 * @return {@link AliasableOffer} AS
	 */
	default AliasableOffer any(SelectStatement subquery) {
		SelectStatement root = getSelectStatement();

		root.quitRowMode();
		root.forSubquery(true);

		SQLQueryBuilder builder = subquery.toSQLQueryBuilder();
		builder.forSubquery(true);

		Column[] columns = { new PseudoColumn(getRelationship(), builder.sql(), false) };
		return new ColumnExpression(getSelectStatement().getQueryId(), "({0})", columns, builder);
	}

	/**
	 * SELECT 句に * を追加します。
	 * @return {@link SelectOffer}
	 */
	default SelectOffer asterisk() {
		getSelectStatement().quitRowMode();
		Column[] columns = { new PseudoColumn(getRelationship(), "*", true) };

		RuntimeId id = getSelectStatement().getQueryId();

		SelectOffers offers = new SelectOffers(id);
		offers.add(new ColumnExpression(id, "{0}", columns));

		return offers;
	}

	/**
	 * SELECT 句に 関連したテーブルの * を追加します。
	 * @param relationship このテーブルから辿れるテーブル
	 * @return {@link SelectOffer}
	 */
	default SelectOffer asterisk(TableFacadeRelationship relationship) {
		getSelectStatement().quitRowMode();
		Column[] columns = { new PseudoColumn(relationship.getRelationship(), "*", true) };

		RuntimeId id = getSelectStatement().getQueryId();

		SelectOffers offers = new SelectOffers(id);
		offers.add(new ColumnExpression(id, "{0}", columns));

		return offers;
	}

	/**
	 * SELECT 句に * を追加します。
	 * @return {@link SelectOffer}
	 */
	default SelectOffer asteriskAll() {
		getSelectStatement().quitRowMode();
		Column[] columns = { new PseudoColumn(getRelationship(), "*", false) };

		RuntimeId id = getSelectStatement().getQueryId();

		SelectOffers offers = new SelectOffers(id);
		offers.add(new ColumnExpression(id, "{0}", columns));

		return offers;
	}

	/**
	 * 他のクエリと JOIN した際などの、最終的な順位を指定します。
	 * @param order 最終的な GROUP BY 句内での順序
	 * @param offer 対象カラム
	 * @return {@link GroupByColumn}
	 */
	default SelectOffer order(int order, SelectOffer offer) {
		SelectOffers offers = new SelectOffers(getSelectStatement().getQueryId());
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
	 * @return このインスタンスの大元の {@link SelectStatement}
	 */
	SelectStatement getSelectStatement();
}
