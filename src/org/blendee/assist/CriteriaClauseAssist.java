package org.blendee.assist;

import java.util.function.Consumer;

import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.Relationship;

/**
 * 自動生成される TableFacade の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @param <R> {@link #paren(Consumer)} 用実装サブクラス
 * @author 千葉 哲嗣
 */
public interface CriteriaClauseAssist<R extends CriteriaClauseAssist<?>> {

	/**
	 * 任意のカラム表現を生成します。
	 * @param expression テンプレート
	 * @param columns テンプレートにセットする {@link CriteriaAssistColumn}
	 * @return 任意のカラム
	 */
	@SuppressWarnings("unchecked")
	default <O extends LogicalOperators<?>> CriteriaAssistColumn<O> expr(
		String expression,
		CriteriaAssistColumn<O>... columns) {
		return new CriteriaAnyColumn<>(statement(), expression, columns);
	}

	/**
	 * 任意のカラム表現を生成します。
	 * @param expression テンプレート
	 * @param columns テンプレートにセットする {@link CriteriaAssistColumn}
	 * @param values プレースホルダの値
	 * @return 任意のカラム
	 */
	default <O extends LogicalOperators<?>> CriteriaAssistColumn<O> expr(
		String expression,
		Vargs<CriteriaAssistColumn<O>> columns,
		Object... values) {
		return new CriteriaAnyColumn<>(statement(), expression, columns.get(), values);
	}

	/**
	 * この句に EXISTS 条件を追加します。
	 * @param subquery サブクエリ
	 * @return {@link LogicalOperators}
	 */
	LogicalOperators<?> EXISTS(SelectStatement subquery);

	/**
	 * この句に NOT EXISTS 条件を追加します。
	 * @param subquery サブクエリ
	 * @return {@link LogicalOperators}
	 */
	LogicalOperators<?> NOT_EXISTS(SelectStatement subquery);

	/**
	 * この句に IN サブクエリ条件を追加します。
	 * @param mainColumns メイン側クエリの結合カラム
	 * @param subquery 追加条件
	 * @return {@link LogicalOperators}
	 */
	LogicalOperators<?> IN(Vargs<AssistColumn> mainColumns, SelectStatement subquery);

	/**
	 * この句に NOT IN サブクエリ条件を追加します。
	 * @param mainColumns メイン側クエリの結合カラム
	 * @param subquery 追加条件
	 * @return {@link LogicalOperators}
	 */
	LogicalOperators<?> NOT_IN(Vargs<AssistColumn> mainColumns, SelectStatement subquery);

	/**
	 * この句に任意のカラムを追加します。
	 * @param template カラムのテンプレート
	 * @return {@link CriteriaColumn}
	 */
	CriteriaColumn<?> any(String template);

	/**
	 * Consumer に渡された条件句を () で囲みます。
	 * @param consumer {@link Consumer}
	 * @return {@link LogicalOperators}
	 */
	LogicalOperators<?> paren(Consumer<R> consumer);

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

		getContext().addCriteria(values.createCriteria(statement().getRuntimeId(), template));
	}

	/**
	 * WHERE 句に任意の条件を追加します。
	 * @param expression カラムの文字列表現
	 */
	default void with(String expression) {
		getContext().addCriteria(new CriteriaFactory(statement().getRuntimeId()).createCriteria(expression));
	}

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return QueryRelationship が WHERE 句用の場合、そのタイプに応じた {@link CriteriaContext}
	 */
	CriteriaContext getContext();

	/**
	 * @return {@link Statement}
	 */
	default Statement statement() {
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
