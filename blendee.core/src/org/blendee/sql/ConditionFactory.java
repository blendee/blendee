package org.blendee.sql;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.blendee.sql.Condition.ProxyCondition;
import org.blendee.sql.binder.StringBinder;

/**
 * 条件句インスタンスを生成するファクトリクラスです。
 *
 * @author 千葉 哲嗣
 */
public class ConditionFactory {

	/**
	 * 比較演算子の列挙型です。
	 *
	 * @author 千葉 哲嗣
	 * @version $Name: v0_4_20090119a $
	 */
	public enum ComparisonOperator {

		/**
		 * 比較演算子 '='
		 */
		EQ("="),

		/**
		 * 比較演算子 '&lt;&gt;'
		 */
		NE("<>"),

		/**
		 * 比較演算子 '&lt;'
		 */
		LT("<"),

		/**
		 * 比較演算子 '&gt;'
		 */
		GT(">"),

		/**
		 * 比較演算子 '&lt;='
		 */
		LE("<="),

		/**
		 * 比較演算子 '&gt;='
		 */
		GE(">=");

		private final String expression;

		private ComparisonOperator(String operator) {
			expression = "{0} " + operator + " ?";
		}

		private Condition create(Column column, Bindable bindable) {
			return createCondition(expression, new Column[] { column }, new Bindable[] { bindable });
		}
	}

	/**
	 * LIKE 検索の列挙型です。
	 *
	 * @author 千葉 哲嗣
	 * @version $Name: v0_4_20090119a $
	 */
	public enum Match {

		/**
		 * 後方一致
		 */
		ENDING_WITH {

			@Override
			String getSearchExpression(String value) {
				return "%" + escape(value);
			}
		},

		/**
		 * 前方一致
		 */
		STARTING_WITH {

			@Override
			String getSearchExpression(String value) {
				return escape(value) + "%";
			}
		},

		/**
		 * 部分一致
		 */
		CONTAINING {

			@Override
			String getSearchExpression(String value) {
				return "%" + escape(value) + "%";
			}
		};

		private Condition create(Column column, String value) {
			return createCondition(
				"{0} LIKE ? ESCAPE '!'",
				column,
				new StringBinder(getSearchExpression(value)));
		}

		private Condition createNot(Column column, String value) {
			return createCondition(
				"{0} NOT LIKE ? ESCAPE '!'",
				column,
				new StringBinder(getSearchExpression(value)));
		}

		abstract String getSearchExpression(String value);
	}

	/**
	 * NULL 検査の列挙型です。
	 *
	 * @author 千葉 哲嗣
	 * @version $Name: v0_4_20090119a $
	 */
	public enum NullComparisonOperator {

		/**
		 * IS NULL
		 */
		IS_NULL("IS NULL"),

		/**
		 * IS NOT NULL
		 */
		IS_NOT_NULL("IS NOT NULL");

		private final String expression;

		private NullComparisonOperator(String expression) {
			this.expression = "{0} " + expression;
		}

		private Condition create(Column column) {
			return createCondition(expression, new Column[] { column });
		}
	}

	private static final Pattern pattern = Pattern.compile("([%_!])");

	private ConditionFactory() {}

	/**
	 * = を使用した簡単な条件句を生成します。
	 *
	 * @param column 対象となるカラム
	 * @param bindable 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Condition createCondition(Column column, Bindable bindable) {
		return ComparisonOperator.EQ.create(column, bindable);
	}

	/**
	 * = を使用した簡単な条件句を生成します。
	 *
	 * @param column 対象となるカラム
	 * @param value 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Condition createCondition(Column column, String value) {
		return ComparisonOperator.EQ.create(column, new StringBinder(value));
	}

	/**
	 * = を使用した簡単な条件句を生成します。
	 *
	 * @param column 対象となるカラム
	 * @param value 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Condition createCondition(Column column, Number value) {
		return ComparisonOperator.EQ.create(column, BindableConverter.convert(value)[0]);
	}

	/**
	 * = を使用した簡単な条件句を生成します。
	 *
	 * @param columnName 対象となるカラム
	 * @param bindable 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Condition createCondition(String columnName, Bindable bindable) {
		return ComparisonOperator.EQ.create(new PhantomColumn(columnName), bindable);
	}

	/**
	 * = を使用した簡単な条件句を生成します。
	 *
	 * @param columnName 対象となるカラム
	 * @param value 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Condition createCondition(String columnName, String value) {
		return ComparisonOperator.EQ.create(
			new PhantomColumn(columnName),
			new StringBinder(value));
	}

	/**
	 * = を使用した簡単な条件句を生成します。
	 *
	 * @param columnName 対象となるカラム
	 * @param value 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Condition createCondition(String columnName, Number value) {
		return ComparisonOperator.EQ.create(
			new PhantomColumn(columnName),
			BindableConverter.convert(value)[0]);
	}

	/**
	 * 比較演算子を使用した簡単な条件句を生成します。
	 *
	 * @param operator 比較演算子
	 * @param column 対象となるカラム
	 * @param bindable 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Condition createComparisonCondition(
		ComparisonOperator operator,
		Column column,
		Bindable bindable) {
		return operator.create(column, bindable);
	}

	/**
	 * 比較演算子を使用した簡単な条件句を生成します。
	 *
	 * @param operator 比較演算子
	 * @param columnName 対象となるカラム
	 * @param bindable 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Condition createComparisonCondition(
		ComparisonOperator operator,
		String columnName,
		Bindable bindable) {
		return operator.create(new PhantomColumn(columnName), bindable);
	}

	/**
	 * IN を使用した条件句を生成します。
	 *
	 * @param column 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Condition createInCondition(
		Column column,
		String... values) {
		return createCondition(
			buildInClause(values.length),
			new Column[] { column },
			toBindables(values));
	}

	/**
	 * IN を使用した条件句を生成します。
	 *
	 * @param column 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Condition createInCondition(
		Column column,
		Number... values) {
		return createCondition(
			buildInClause(values.length),
			new Column[] { column },
			BindableConverter.convert(values));
	}

	/**
	 * IN を使用した条件句を生成します。
	 *
	 * @param column 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Condition createInCondition(
		Column column,
		Bindable... values) {
		return createCondition(
			buildInClause(values.length),
			new Column[] { column },
			values);
	}

	/**
	 * IN を使用した条件句を生成します。
	 *
	 * @param columnName 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Condition createInCondition(
		String columnName,
		String... values) {
		return createCondition(
			buildInClause(values.length),
			new Column[] { new PhantomColumn(columnName) },
			toBindables(values));
	}

	/**
	 * IN を使用した条件句を生成します。
	 *
	 * @param columnName 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Condition createInCondition(
		String columnName,
		Number... values) {
		return createCondition(
			buildInClause(values.length),
			new Column[] { new PhantomColumn(columnName) },
			BindableConverter.convert(values));
	}

	/**
	 * IN を使用した条件句を生成します。
	 *
	 * @param columnName 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Condition createInCondition(
		String columnName,
		Bindable... values) {
		return createCondition(
			buildInClause(values.length),
			new Column[] { new PhantomColumn(columnName) },
			values);
	}

	/**
	 * LIKE を使用した条件句を生成します。
	 *
	 * @param type LIKE の位置
	 * @param column 対象となるカラム
	 * @param value '%'を含まない値
	 * @return 作成されたインスタンス
	 */
	public static Condition createLikeCondition(
		Match type,
		Column column,
		String value) {
		return type.create(column, value);
	}

	/**
	 * NOT LIKE を使用した条件句を生成します。
	 *
	 * @param type LIKE の位置
	 * @param column 対象となるカラム
	 * @param value '%'を含まない値
	 * @return 作成されたインスタンス
	 */
	public static Condition createNotLikeCondition(
		Match type,
		Column column,
		String value) {
		return type.createNot(column, value);
	}

	/**
	 * LIKE を使用した条件句を生成します。
	 *
	 * @param type LIKE の位置
	 * @param columnName 対象となるカラム
	 * @param value '%'を含まない値
	 * @return 作成されたインスタンス
	 */
	public static Condition createLikeCondition(
		Match type,
		String columnName,
		String value) {
		return type.create(new PhantomColumn(columnName), value);
	}

	/**
	 * NOT LIKE を使用した条件句を生成します。
	 *
	 * @param type LIKE の位置
	 * @param columnName 対象となるカラム
	 * @param value '%'を含まない値
	 * @return 作成されたインスタンス
	 */
	public static Condition createNotLikeCondition(
		Match type,
		String columnName,
		String value) {
		return type.createNot(new PhantomColumn(columnName), value);
	}

	/**
	 * NULL かどうかを判定する条件句を生成します。
	 *
	 * @param type 'IS NULL' または 'IS NOT NULL'
	 * @param column 対象となるカラム
	 * @return 作成されたインスタンス
	 */
	public static Condition createNullCondition(
		NullComparisonOperator type,
		Column column) {
		return type.create(column);
	}

	/**
	 * NULL かどうかを判定する条件句を生成します。
	 *
	 * @param type 'IS NULL' または 'IS NOT NULL'
	 * @param columnName 対象となるカラム
	 * @return 作成されたインスタンス
	 */
	public static Condition createNullCondition(
		NullComparisonOperator type,
		String columnName) {
		return type.create(new PhantomColumn(columnName));
	}

	/**
	 * 空の条件句を生成します。
	 *
	 * @return 生成されたインスタンス
	 */
	public static Condition createCondition() {
		return new ProxyCondition();
	}

	/**
	 * 条件句のテンプレートとそれに埋め込むカラムから条件句を生成します。
	 * <br>
	 * このテンプレートにはプレースホルダを含めることはできません。
	 *
	 * @param clause 条件句のテンプレート
	 * @param columns 対象となるカラム
	 * @return 生成されたインスタンス
	 */
	public static Condition createCondition(String clause, Column[] columns) {
		return new Condition(clause, columns, Binder.EMPTY_ARRAY);
	}

	/**
	 * 条件句のテンプレートとそれに埋め込むカラムから条件句を生成します。
	 * <br>
	 * このテンプレートにはプレースホルダを含めることはできません。
	 *
	 * @param clause 条件句のテンプレート
	 * @param columnNames 対象となるカラム
	 * @return 生成されたインスタンス
	 */
	public static Condition createCondition(
		String clause,
		String[] columnNames) {
		return new Condition(clause, PhantomColumn.convert(columnNames), Binder.EMPTY_ARRAY);
	}

	/**
	 * パラメータとして渡された条件句をそのまま使用したインスタンスを生成します。
	 * <br>
	 * 条件句にはプレースホルダを含めることはできません。
	 *
	 * @param clause 条件句
	 * @return 生成されたインスタンス
	 */
	public static Condition createCondition(String clause) {
		return new Condition(clause, Column.EMPTY_ARRAY, Binder.EMPTY_ARRAY);
	}

	/**
	 * プレースホルダを含む条件句のテンプレートとそれに埋め込むカラム、プレースホルダに設定する値から条件句を生成します。
	 *
	 * @param clause プレースホルダを含む条件句のテンプレート
	 * @param column 対象となるカラム
	 * @param bindable プレースホルダに設定する値
	 * @return 生成されたインスタンス
	 */
	public static Condition createCondition(
		String clause,
		Column column,
		Bindable bindable) {
		return createCondition(clause, new Column[] { column }, new Bindable[] { bindable });
	}

	/**
	 * プレースホルダを含む条件句のテンプレートとそれに埋め込むカラム、プレースホルダに設定する値から条件句を生成します。
	 *
	 * @param clause プレースホルダを含む条件句のテンプレート
	 * @param columnName 対象となるカラム
	 * @param bindable プレースホルダに設定する値
	 * @return 生成されたインスタンス
	 */
	public static Condition createCondition(
		String clause,
		String columnName,
		Bindable bindable) {
		return createCondition(clause, new Column[] { new PhantomColumn(columnName) }, new Bindable[] { bindable });
	}

	/**
	 * プレースホルダを含む条件句のテンプレートとそれに埋め込むカラム、プレースホルダに設定する値から条件句を生成します。
	 *
	 * @param clause プレースホルダを含む条件句のテンプレート
	 * @param columns 対象となるカラム
	 * @param bindables プレースホルダに設定する値
	 * @return 生成されたインスタンス
	 */
	public static Condition createCondition(
		String clause,
		Column[] columns,
		Bindable[] bindables) {
		Binder[] binders = new Binder[bindables.length];
		for (int i = 0; i < bindables.length; i++) {
			binders[i] = bindables[i].toBinder();
		}
		return new Condition(clause, columns, binders);
	}

	/**
	 * プレースホルダを含む条件句のテンプレートとそれに埋め込むカラム、プレースホルダに設定する値から条件句を生成します。
	 *
	 * @param clause プレースホルダを含む条件句のテンプレート
	 * @param columnNames 対象となるカラム
	 * @param bindables プレースホルダに設定する値
	 * @return 生成されたインスタンス
	 */
	public static Condition createCondition(
		String clause,
		String[] columnNames,
		Bindable[] bindables) {
		return createCondition(clause, PhantomColumn.convert(columnNames), bindables);
	}

	/**
	 * コピー用メソッドです。
	 *
	 * @param condition コピーしたいインスタンス
	 * @return 生成されたインスタンス
	 */
	public static Condition createCondition(Condition condition) {
		return condition.replicate();
	}

	/**
	 * サブクエリを使用した条件句を生成します。
	 *
	 * @param mainRelationship 主となるクエリ側のテーブル
	 * @param subqueryRelationship サブクエリ側のテーブル
	 * @param subqueryCondition サブクエリ用条件
	 * @return 生成されたインスタンス
	 * @throws SubqueryException mainRelationship と subQueryRelationship が異なるテーブルを指す場合
	 */
	public static Condition createSubqueryCondition(
		Relationship mainRelationship,
		Relationship subqueryRelationship,
		Condition subqueryCondition) {
		if (!mainRelationship.getTablePath().equals(subqueryRelationship.getTablePath()))
			throw new SubqueryException("異なるテーブルを結合しようとしています");

		return createSubqueryCondition(
			mainRelationship.getPrimaryKeyColumns(),
			subqueryRelationship.getPrimaryKeyColumns(),
			subqueryCondition);
	}

	/**
	 * サブクエリを使用した条件句を生成します。
	 *
	 * @param columns 主となるクエリ側のカラム
	 * @param subqueryColumns サブクエリ側のカラム
	 * @param subqueryCondition サブクエリ用条件
	 * @return 生成されたインスタンス
	 * @throws SubqueryException サブクエリ側のカラムが空の場合
	 * @throws SubqueryException サブクエリ側のカラムが属する {@link Relationship} のルートが異なる場合
	 * @throws SubqueryException サブクエリ用条件内のカラムが属する {@link Relationship} のルートが、サブクエリ側のカラムが属すると異なる場合
	 */
	public static Condition createSubqueryCondition(
		Column[] columns,
		Column[] subqueryColumns,
		Condition subqueryCondition) {
		if (subqueryColumns.length == 0) throw new SubqueryException("subQueryColumns が空です");

		QueryBuilder builder = new QueryBuilder(
			new FromClause(subqueryColumns[0].getRelationship().getRoot().getTablePath()));
		builder.setWhereClause(subqueryCondition);
		SelectClause selectClause = new SelectClause();
		for (Column column : subqueryColumns) {
			selectClause.add(column);
		}

		builder.setSelectClause(selectClause);
		return createSubqueryCondition(columns, builder);
	}

	/**
	 * サブクエリを使用した条件句を生成します。
	 *
	 * @param columns 主となるクエリ側のカラム
	 * @param subquery サブクエリ
	 * @return 生成されたインスタンス
	 * @throws SubqueryException 主となるクエリ側のカラムが空の場合
	 * @throws SubqueryException 主となるクエリ側のカラム数とサブクエリ側の select 句のカラムの数が異なる場合
	 * @throws SubqueryException サブクエリ側の select 句のカラムが主キーに含まれない場合
	 */
	public static Condition createSubqueryCondition(
		Column[] columns,
		QueryBuilder subquery) {
		if (columns.length == 0) throw new SubqueryException("columns が空です");

		Column[] subQueryColumns = subquery.getSelectClause().getColumns();

		if (columns.length != subQueryColumns.length) {
			throw new SubqueryException("項目数が一致しません");
		}

		for (Column column : subQueryColumns) {
			if (!column.getColumnMetadata().isNotNull())
				throw new SubqueryException("IN を使用しているため、サブクエリの select 句のカラムは、すべて NOT NULL である必要があります。");
		}

		List<String> columnPartList = new LinkedList<>();
		for (int i = 0; i < columns.length; i++) {
			columnPartList.add("{" + i + "}");
		}

		List<Binder> binders = new LinkedList<>();
		binders.addAll(Arrays.asList(subquery.getWhereClause().getBinders()));
		binders.addAll(Arrays.asList(subquery.getHavingClause().getBinders()));

		String subqueryString = "(" + String.join(", ", columnPartList) + ") IN (" + subquery.toString() + ")";

		return new Condition(subqueryString, columns, binders.toArray(new Binder[binders.size()]));
	}

	private static String escape(String value) {
		return pattern.matcher(value).replaceAll("!$1");
	}

	private static String buildInClause(int length) {
		return "{0} IN (" + String.join(
			", ",
			Stream.generate(() -> "?").limit(length).collect(Collectors.toList())) + ")";
	}

	private static Bindable[] toBindables(String[] values) {
		List<StringBinder> bindables = Arrays.asList(values).stream().map(v -> new StringBinder(v)).collect(Collectors.toList());
		return bindables.toArray(new Bindable[bindables.size()]);
	}
}
