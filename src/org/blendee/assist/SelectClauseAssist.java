package org.blendee.assist;

import static org.blendee.assist.Helper.AVG_TEMPLATE;
import static org.blendee.assist.Helper.COUNT_TEMPLATE;
import static org.blendee.assist.Helper.MAX_TEMPLATE;
import static org.blendee.assist.Helper.MIN_TEMPLATE;
import static org.blendee.assist.Helper.SUM_TEMPLATE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.blendee.assist.SelectOfferFunction.SelectOffers;
import org.blendee.sql.Column;
import org.blendee.sql.PseudoColumn;
import org.blendee.sql.Relationship;
import org.blendee.sql.SQLQueryBuilder;

/**
 * 自動生成される TableFacade の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface SelectClauseAssist extends ClauseAssist {

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
		SelectOffers visitor = new SelectOffers(getSelectStatement());
		for (SelectOffer offer : offers) {
			offer.get().forEach(c -> visitor.add(c));
		}

		return visitor;
	}

	/**
	 * SELECT 句に、このテーブルのカラムすべてを追加します。
	 * @param assists このテーブルから辿れるテーブル
	 * @return {@link SelectOffer}
	 */
	default SelectOffer all(TableFacadeAssist... assists) {
		Stream<Column> columns;
		if (assists.length == 0) {
			columns = Arrays.stream(getRelationship().getColumns());
		} else {
			columns = Arrays.stream(assists).flatMap(a -> Arrays.stream(a.getRelationship().getColumns()));
		}

		SelectStatement statement = getSelectStatement();
		List<ColumnExpression> result = columns.map(c -> new ColumnExpression(statement, c))
			.collect(Collectors.toList());

		return new SelectOffer() {

			@Override
			public List<ColumnExpression> get() {
				return result;
			}
		};
	}

	/**
	 * SELECT 句用 AVG(column)
	 * @param column {@link SelectColumn}
	 * @return {@link ColumnExpression}
	 */
	default AliasableOffer AVG(AssistColumn column) {
		return any(AVG_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 SUM(column)
	 * @param column {@link AliasableOffer}
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer SUM(AssistColumn column) {
		return any(SUM_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 MAX(column)
	 * @param column {@link AliasableOffer}
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer MAX(AssistColumn column) {
		return any(MAX_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 MIN(column)
	 * @param column {@link AliasableOffer}
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer MIN(AssistColumn column) {
		return any(MIN_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 COUNT(*)
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer COUNT() {
		return new ColumnExpression(getSelectStatement(), COUNT_TEMPLATE, new PseudoColumn(getRelationship(), "*", false));
	}

	/**
	 * SELECT 句用 COUNT(column)
	 * @param column {@link AliasableOffer}
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer COUNT(AssistColumn column) {
		return any(COUNT_TEMPLATE, column);
	}

	/**
	 * SELECT 句用 COALESCE
	 * @param columns {@link AliasableOffer}
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer COALESCE(AssistColumn... columns) {
		return any(Helper.createCoalesceTemplate(columns.length), columns);
	}

	/**
	 * SELECT 句用 COALESCE
	 * @param columns {@link AliasableOffer}
	 * @param stringExpressions カラム以外の要素
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer COALESCE(Vargs<AssistColumn> columns, String... stringExpressions) {
		List<AssistColumn> all = new ArrayList<>();
		for (AssistColumn column : columns.get()) {
			all.add(column);
		}

		for (String expression : stringExpressions) {
			all.add(new ColumnExpression(getSelectStatement(), "{0}", new PseudoColumn(getRelationship(), expression, false)));
		}

		int size = all.size();

		return any(
			Helper.createCoalesceTemplate(size),
			all.toArray(new AliasableOffer[size]));
	}

	/**
	 * SELECT 句用 COALESCE
	 * @param column {@link AliasableOffer}
	 * @param stringExpressions カラム以外の要素
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer COALESCE(AssistColumn column, String... stringExpressions) {
		return COALESCE(Vargs.of(column), stringExpressions);
	}

	/**
	 * SELECT 句用 COALESCE
	 * @param columns {@link AliasableOffer}
	 * @param numbers カラム以外の要素
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer COALESCE(Vargs<AssistColumn> columns, Number... numbers) {
		List<AssistColumn> all = new ArrayList<>();
		for (AssistColumn column : columns.get()) {
			all.add(column);
		}

		for (Number number : numbers) {
			all.add(new ColumnExpression(getSelectStatement(), "{0}", new PseudoColumn(getRelationship(), number.toString(), false)));
		}

		int size = all.size();

		return any(
			Helper.createCoalesceTemplate(size),
			all.toArray(new AliasableOffer[size]));
	}

	/**
	 * SELECT 句用 COALESCE
	 * @param column {@link AliasableOffer}
	 * @param numbers カラム以外の要素
	 * @return {@link AliasableOffer}
	 */
	default AliasableOffer COALESCE(AssistColumn column, Number... numbers) {
		return COALESCE(Vargs.of(column), numbers);
	}

	/**
	 * SELECT 句に任意のカラムを追加します。
	 * @param template カラムのテンプレート
	 * @param selectColumns 使用するカラム
	 * @return {@link AliasableOffer} AS
	 */
	default AliasableOffer any(String template, AssistColumn... selectColumns) {
		getSelectStatement().quitRowMode();

		Column[] columns = new Column[selectColumns.length];
		for (int i = 0; i < selectColumns.length; i++) {
			columns[i] = selectColumns[i].column();
		}

		return new ColumnExpression(getSelectStatement(), template, columns, (done, statement) -> {
			for (AssistColumn column : selectColumns) {
				done = column.complement(done, statement);
			}

			return done;
		});
	}

	/**
	 * SELECT 句に任意の文字列を追加します。
	 * @param expression 文字列表現
	 * @return {@link AliasableOffer} AS
	 */
	default AliasableOffer any(String expression) {
		getSelectStatement().quitRowMode();
		Column[] columns = { new PseudoColumn(getRelationship(), expression, false) };
		return new ColumnExpression(getSelectStatement(), "{0}", columns);
	}

	/**
	 * SELECT 句に任意の数値を追加します。
	 * @param number 文字列表現
	 * @return {@link AliasableOffer} AS
	 */
	default AliasableOffer any(Number number) {
		getSelectStatement().quitRowMode();
		Column[] columns = { new PseudoColumn(getRelationship(), number.toString(), false) };
		return new ColumnExpression(getSelectStatement(), "{0}", columns);
	}

	/**
	 * SELECT 句に任意の数値を追加します。
	 * @param column 任意のカラム
	 * @return {@link AliasableOffer} AS
	 */
	default AliasableOffer any(AssistColumn column) {
		getSelectStatement().quitRowMode();
		return new ColumnExpression(column);
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
		return new ColumnExpression(getSelectStatement(), "({0})", columns, builder);
	}

	/**
	 * SELECT 句に関連したテーブルの * を追加します。
	 * @param assists このテーブルから辿れるテーブル
	 * @return {@link SelectOffer}
	 */
	default SelectOffer asterisk(TableFacadeAssist... assists) {
		SelectStatement statement = getSelectStatement();

		statement.quitRowMode();

		SelectOffers offers = new SelectOffers(statement);

		Stream<Relationship> relationships;
		if (assists.length == 0) {
			relationships = Stream.of(getRelationship());
		} else {
			relationships = Arrays.stream(assists).map(a -> a.getRelationship());
		}

		relationships.forEach(r -> {
			Column[] columns = { new PseudoColumn(r, "*", true) };
			offers.add(new ColumnExpression(statement, "{0}", columns));
		});

		return offers;
	}

	/**
	 * 他のクエリと JOIN した際などの、最終的な順位を指定します。
	 * @param order 最終的な GROUP BY 句内での順序
	 * @param offer 対象カラム
	 * @return {@link GroupByColumn}
	 */
	default SelectOffer order(int order, SelectOffer offer) {
		SelectOffers offers = new SelectOffers(getSelectStatement());
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

	@Override
	default Statement statement() {
		return getSelectStatement();
	}
}
