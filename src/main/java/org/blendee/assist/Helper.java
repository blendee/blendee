package org.blendee.assist;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.blendee.jdbc.ChainPreparedStatementComplementer;
import org.blendee.sql.Binder;
import org.blendee.sql.Column;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.RuntimeIdColumn;
import org.blendee.sql.SQLQueryBuilder;

@SuppressWarnings("javadoc")
public class Helper {

	static final String AVG_TEMPLATE = "AVG({0})";

	static final String SUM_TEMPLATE = "SUM({0})";

	static final String MAX_TEMPLATE = "MAX({0})";

	static final String MIN_TEMPLATE = "MIN({0})";

	static final String COUNT_TEMPLATE = "COUNT({0})";

	static final ChainPreparedStatementComplementer complementerStub = (done, statement) -> done;

	public static void setExists(RuntimeId main, CriteriaClauseAssist<?> assist, SelectStatement subquery) {
		setExists(main, assist, subquery, "EXISTS");
	}

	public static void setNotExists(RuntimeId main, CriteriaClauseAssist<?> assist, SelectStatement subquery) {
		setExists(main, assist, subquery, "NOT EXISTS");
	}

	private static void setExists(RuntimeId main, CriteriaClauseAssist<?> assist, SelectStatement subquery, String keyword) {
		assist.statement().forSubquery(true);

		var builder = subquery.toSQLQueryBuilder();
		builder.forSubquery(true);

		//サブクエリのFrom句からBinderを取り出す前にSQL化して内部のFrom句をマージしておかないとBinderが準備されないため、先に実行
		var subqueryString = keyword + " (" + builder.sql() + ")";

		assist.getContext()
			.addCriteria(
				new CriteriaFactory(main).createCriteria(subqueryString, Column.EMPTY_ARRAY, builder.currentBinders()));
	}

	public static <A> void paren(RuntimeId id, CriteriaContext context, Consumer<A> consumer, A assist) {
		var current = CriteriaContext.getContextCriteria();

		Objects.requireNonNull(current);

		var contextCriteria = new CriteriaFactory(id).create();
		CriteriaContext.setContextCriteria(contextCriteria);

		consumer.accept(assist);

		CriteriaContext.setContextCriteria(current);
		context.addCriteria(contextCriteria);
	}

	/**
	 * {@link CriteriaClauseAssist} に IN サブクエリ条件を追加します。
	 * @param notIn NOT IN の場合 true
	 * @param mainColumns メイン側クエリの結合カラム
	 * @param subquery 追加条件
	 */
	public static void addInCriteria(CriteriaClauseAssist<?> assist, boolean notIn, Vargs<AssistColumn> mainColumns, SelectStatement subquery) {
		var criteriaColumns = mainColumns.get();

		var columns = new Column[criteriaColumns.length];

		for (int i = 0; i < criteriaColumns.length; i++) {
			columns[i] = criteriaColumns[i].column();
		}

		var values = flatValues(criteriaColumns);

		assist.getContext()
			.addCriteria(
				merge(
					values,
					createSubqueryCriteria(
						assist.statement().getRuntimeId(),
						subquery.toSQLQueryBuilder(),
						notIn,
						columns)));
	}

	/**
	 * このサブクエリから、メインクエリで使用できる {@link Criteria} を生成します。
	 * @param main {@link RuntimeId}
	 * @param builder {@link SQLQueryBuilder}
	 * @param notIn NOT IN の場合 true
	 * @param mainQueryColumn メインクエリ側のカラム
	 * @return {@link Criteria} となったサブクエリ
	 */
	static Criteria createSubqueryCriteria(RuntimeId main, SQLQueryBuilder builder, boolean notIn, Column... mainQueryColumn) {
		return new CriteriaFactory(main)
			//SQLDecoratorでサブクエリのSELECT句自体が変更されている場合を考慮し、SELECT句チェックを行わない
			.createSubqueryWithoutCheck(
				mainQueryColumn,
				builder,
				notIn);
	}

	static <R, I extends Iterator<R>> Optional<R> unique(I iterator) {
		if (!iterator.hasNext()) return Optional.empty();
		var row = iterator.next();
		if (iterator.hasNext()) throw new NotUniqueException();
		return Optional.of(row);
	}

	static String createCoalesceTemplate(int columns) {
		var list = new LinkedList<String>();
		for (var i = 0; i < columns; i++) {
			list.add("{" + i + "}");
		}

		return "COALESCE(" + String.join(", ", list) + ")";
	}

	static Binder[] flatValues(AssistColumn[] columns) {
		return flatValues(Arrays.stream(columns).map(c -> c.values()));
	}

	static Binder[] flatValues(Stream<Binder[]> binders) {
		var list = new LinkedList<Binder>();
		binders.forEach(v -> list.addAll(Arrays.asList(v)));

		return list.toArray(new Binder[list.size()]);
	}

	static Criteria merge(Binder[] values, Criteria criteria) {
		criteria.changeBinders(Helper.flatValues(Vargs.of(values, criteria.getBinders()).stream()));

		return criteria;
	}

	static RuntimeIdColumn buildRuntimeIdColumn(TableFacadeAssist assist, String name) {
		return new RuntimeIdColumn(
			assist.getRelationship().getColumn(name),
			assist.getSelectStatement().getRuntimeId());
	}

	static RuntimeIdColumn buildRuntimeIdColumnForUpdate(TableFacadeAssist assist, String name) {
		return new RuntimeIdColumn(
			assist.getRelationship().getColumn(name),
			assist.getDataManipulationStatement().getRuntimeId());
	}
}
