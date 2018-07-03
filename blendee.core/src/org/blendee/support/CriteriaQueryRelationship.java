package org.blendee.support;

import java.util.function.Consumer;

import org.blendee.sql.Column;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.MultiColumn;
import org.blendee.sql.Relationship;

/**
 * 自動生成される ConcreteQueryRelationship の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface CriteriaQueryRelationship {

	/**
	 * この句に EXISTS 条件を追加します。
	 * @param subquery サブクエリ
	 */
	default void EXISTS(Query subquery) {
		Exists.setExists(this, subquery, "EXISTS");
	}

	/**
	 * この句に NOT EXISTS 条件を追加します。
	 * @param subquery サブクエリ
	 */
	default void NOT_EXISTS(Query subquery) {
		Exists.setExists(this, subquery, "NOT EXISTS");
	}

	/**
	 * この句に任意のカラムを追加します。
	 * @param template カラムのテンプレート
	 * @return AND/OR で継続できないカラム
	 */
	default CriteriaQueryColumn<?> any(String template) {
		return new CriteriaQueryColumn<LogicalOperators<?>>(
			getContext(),
			new MultiColumn(getRelationship(), template)) {

			@Override
			LogicalOperators<?> logocalOperators() {
				return () -> {
					throw new UnsupportedOperationException();
				};
			}
		};
	}

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

		getContext().addCriteria(values.createCriteria(template));
	}

	/**
	 * WHERE 句に任意の条件を追加します。
	 * @param expression カラムの文字列表現
	 */
	default void with(String expression) {
		getContext().addCriteria(CriteriaFactory.createCriteria(expression));
	}

	/**
	 * この句にサブクエリ条件を追加します。
	 * @param subquery 追加条件
	 */
	default void subquery(Query subquery) {
		subquery(false, subquery);
	}

	/**
	 * この句にサブクエリ条件を追加します。
	 * @param subquery 追加条件
	 * @param mainQueryColumns メイン側クエリの結合カラム
	 */
	default void subquery(Query subquery, CriteriaQueryColumn<?>... mainQueryColumns) {
		subquery(false, subquery, mainQueryColumns);
	}

	/**
	 * この句にサブクエリ条件を追加します。
	 * @param notIn NOT IN の場合 true
	 * @param subquery 追加条件
	 */
	default void subquery(boolean notIn, Query subquery) {
		getContext().addCriteria(subquery.toSubquery().createCriteria(notIn, getRoot()));
	}

	/**
	 * この句にサブクエリ条件を追加します。
	 * @param notIn NOT IN の場合 true
	 * @param subquery 追加条件
	 * @param mainQueryColumns メイン側クエリの結合カラム
	 */
	default void subquery(boolean notIn, Query subquery, CriteriaQueryColumn<?>... mainQueryColumns) {
		Column[] columns = new Column[mainQueryColumns.length];

		for (int i = 0; i < mainQueryColumns.length; i++) {
			columns[i] = mainQueryColumns[i].column();
		}

		getContext().addCriteria(subquery.toSubquery().createCriteria(notIn, columns));
	}

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return QueryRelationship が WHERE 句用の場合、そのタイプに応じた {@link QueryCriteriaContext}
	 */
	QueryCriteriaContext getContext();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスの大元の {@link Query}
	 */
	Query getRoot();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスが表す {@link Relationship}
	 */
	Relationship getRelationship();
}
