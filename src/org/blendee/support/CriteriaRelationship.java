package org.blendee.support;

import java.util.function.Consumer;

import org.blendee.sql.Column;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.Relationship;

/**
 * 自動生成される ConcreteQueryRelationship の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface CriteriaRelationship {

	/**
	 * この句に EXISTS 条件を追加します。
	 * @param subquery サブクエリ
	 */
	default void EXISTS(SelectStatement subquery) {
		Exists.setExists(getStatement().getRuntimeId(), this, subquery, "EXISTS");
	}

	/**
	 * この句に NOT EXISTS 条件を追加します。
	 * @param subquery サブクエリ
	 */
	default void NOT_EXISTS(SelectStatement subquery) {
		Exists.setExists(getStatement().getRuntimeId(), this, subquery, "NOT EXISTS");
	}

	/**
	 * この句に任意のカラムを追加します。
	 * @param template カラムのテンプレート
	 * @return {@link CriteriaColumn}
	 */
	CriteriaColumn<?> any(String template);

	/**
	 * この句に条件を追加します。
	 * @param criteria 追加条件
	 */
	default void with(Criteria criteria) {
		getContext().addCriteria(criteria);
	}

	/**
	 * WHERE 句に任意の条件を追加します。
	 * @param template カラムのテンプレート
	 * @param consumer {@link Consumer}
	 */
	default void with(
		String template,
		Consumer<WithValues> consumer) {
		WithValues values = new WithValues();
		consumer.accept(values);

		getContext().addCriteria(values.createCriteria(getStatement().getRuntimeId(), template));
	}

	/**
	 * WHERE 句に任意の条件を追加します。
	 * @param expression カラムの文字列表現
	 */
	default void with(String expression) {
		getContext().addCriteria(new CriteriaFactory(getStatement().getRuntimeId()).createCriteria(expression));
	}

	/**
	 * この句に IN サブクエリ条件を追加します。
	 * @param mainColumns メイン側クエリの結合カラム
	 * @param subquery 追加条件
	 */
	default void IN(Vargs<CriteriaColumn<?>> mainColumns, SelectStatement subquery) {
		IN(false, mainColumns, subquery);
	}

	/**
	 * この句に IN サブクエリ条件を追加します。
	 * @param notIn NOT IN の場合 true
	 * @param mainColumns メイン側クエリの結合カラム
	 * @param subquery 追加条件
	 */
	default void IN(boolean notIn, Vargs<CriteriaColumn<?>> mainColumns, SelectStatement subquery) {
		CriteriaColumn<?>[] criteriaColumns = mainColumns.get();

		Column[] columns = new Column[criteriaColumns.length];

		for (int i = 0; i < criteriaColumns.length; i++) {
			columns[i] = criteriaColumns[i].column();
		}

		getContext().addCriteria(Subquery.createCriteria(getStatement().getRuntimeId(), subquery.toSQLQueryBuilder(), notIn, columns));
	}

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return QueryRelationship が WHERE 句用の場合、そのタイプに応じた {@link CriteriaContext}
	 */
	CriteriaContext getContext();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスの大元の {@link Statement}
	 */
	default Statement getStatement() {
		return getSelectStatement();
	}

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスの大元の {@link SelectStatement}
	 */
	SelectStatement getSelectStatement();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスが表す {@link Relationship}
	 */
	Relationship getRelationship();
}
