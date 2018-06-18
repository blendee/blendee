package org.blendee.sql;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.blendee.sql.Criteria.ProxyCriteria;
import org.blendee.sql.binder.StringBinder;
import org.blendee.support.ComplementerValues;

/**
 * 条件句インスタンスを生成するファクトリクラスです。
 * @author 千葉 哲嗣
 */
public class CriteriaFactory {

	/**
	 * 比較演算子の列挙型です。
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

		private Criteria create(Column column, Bindable bindable) {
			return createCriteria(expression, new Column[] { column }, new Bindable[] { bindable });
		}
	}

	/**
	 * LIKE 検索の列挙型です。
	 */
	public enum Match {

		/**
		 * 後方一致
		 */
		ENDING_WITH {

			@Override
			String decorate(String value) {
				return "%" + value;
			}
		},

		/**
		 * 前方一致
		 */
		STARTING_WITH {

			@Override
			String decorate(String value) {
				return value + "%";
			}
		},

		/**
		 * 部分一致
		 */
		CONTAINING {

			@Override
			String decorate(String value) {
				return "%" + value + "%";
			}
		},

		/**
		 * 任意
		 */
		OPTIONAL {

			@Override
			String decorate(String value) {
				throw new UnsupportedOperationException();
			}

			@Override
			Criteria create(String clause, Column column, String value) {
				return createCriteria(clause, column, new StringBinder(value));
			}
		};

		private static final Pattern pattern = Pattern.compile("([%_!])");

		abstract String decorate(String value);

		private Criteria create(Column column, String value) {
			return create("{0} LIKE ?", column, value);
		}

		private Criteria createNot(Column column, String value) {
			return create("{0} NOT LIKE ?", column, value);
		}

		Criteria create(String clause, Column column, String value) {
			Matcher matcher = pattern.matcher(value);
			if (!matcher.find()) return createCriteria(
				clause,
				column,
				new StringBinder(decorate(value)));

			return createCriteria(
				clause + " ESCAPE '!'",
				column,
				new StringBinder(decorate(matcher.replaceAll("!$1"))));
		}
	}

	/**
	 * NULL 検査の列挙型です。
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

		private Criteria create(Column column) {
			return createCriteria(expression, new Column[] { column });
		}
	}

	private CriteriaFactory() {}

	/**
	 * = を使用した簡単な条件句を生成します。
	 * @param column 対象となるカラム
	 * @param bindable 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria create(Column column, Bindable bindable) {
		return ComparisonOperator.EQ.create(column, bindable);
	}

	/**
	 * = を使用した簡単な条件句を生成します。
	 * @param column 対象となるカラム
	 * @param value 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria create(Column column, String value) {
		return ComparisonOperator.EQ.create(column, new StringBinder(value));
	}

	/**
	 * = を使用した簡単な条件句を生成します。
	 * @param column 対象となるカラム
	 * @param value 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria create(Column column, Number value) {
		return ComparisonOperator.EQ.create(column, BindableConverter.convert(value)[0]);
	}

	/**
	 * = を使用した簡単な条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param bindable 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria create(String columnName, Bindable bindable) {
		return ComparisonOperator.EQ.create(new PhantomColumn(columnName), bindable);
	}

	/**
	 * = を使用した簡単な条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param value 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria create(String columnName, String value) {
		return ComparisonOperator.EQ.create(
			new PhantomColumn(columnName),
			new StringBinder(value));
	}

	/**
	 * = を使用した簡単な条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param value 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria create(String columnName, Number value) {
		return ComparisonOperator.EQ.create(
			new PhantomColumn(columnName),
			BindableConverter.convert(value)[0]);
	}

	/**
	 * 比較演算子を使用した簡単な条件句を生成します。
	 * @param operator 比較演算子
	 * @param column 対象となるカラム
	 * @param bindable 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria create(
		ComparisonOperator operator,
		Column column,
		Bindable bindable) {
		return operator.create(column, bindable);
	}

	/**
	 * 比較演算子を使用した簡単な条件句を生成します。
	 * @param operator 比較演算子
	 * @param columnName 対象となるカラム
	 * @param bindable 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria create(
		ComparisonOperator operator,
		String columnName,
		Bindable bindable) {
		return operator.create(new PhantomColumn(columnName), bindable);
	}

	/**
	 * IN を使用した条件句を生成します。
	 * @param column 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createInCriteria(
		Column column,
		String... values) {
		return createCriteria(
			buildInClause(values.length),
			new Column[] { column },
			toBindables(values));
	}

	/**
	 * IN を使用した条件句を生成します。
	 * @param column 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createInCriteria(
		Column column,
		Number... values) {
		return createCriteria(
			buildInClause(values.length),
			new Column[] { column },
			BindableConverter.convert(values));
	}

	/**
	 * IN を使用した条件句を生成します。
	 * @param column 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createInCriteria(
		Column column,
		Timestamp... values) {
		return createCriteria(
			buildInClause(values.length),
			new Column[] { column },
			BindableConverter.convert(values));
	}

	/**
	 * IN を使用した条件句を生成します。
	 * @param column 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createInCriteria(
		Column column,
		Bindable... values) {
		return createCriteria(
			buildInClause(values.length),
			new Column[] { column },
			values);
	}

	/**
	 * IN を使用した条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createInCriteria(
		String columnName,
		String... values) {
		return createCriteria(
			buildInClause(values.length),
			new Column[] { new PhantomColumn(columnName) },
			toBindables(values));
	}

	/**
	 * IN を使用した条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createInCriteria(
		String columnName,
		Number... values) {
		return createCriteria(
			buildInClause(values.length),
			new Column[] { new PhantomColumn(columnName) },
			BindableConverter.convert(values));
	}

	/**
	 * IN を使用した条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createInCriteria(
		String columnName,
		Timestamp... values) {
		return createCriteria(
			buildInClause(values.length),
			new Column[] { new PhantomColumn(columnName) },
			BindableConverter.convert(values));
	}

	/**
	 * IN を使用した条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createInCriteria(
		String columnName,
		Bindable... values) {
		return createCriteria(
			buildInClause(values.length),
			new Column[] { new PhantomColumn(columnName) },
			values);
	}

	/**
	 * IN を使用した条件句を生成します。
	 * @param column 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotInCriteria(
		Column column,
		String... values) {
		return createCriteria(
			buildNotInClause(values.length),
			new Column[] { column },
			toBindables(values));
	}

	/**
	 * IN を使用した条件句を生成します。
	 * @param column 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotInCriteria(
		Column column,
		Number... values) {
		return createCriteria(
			buildNotInClause(values.length),
			new Column[] { column },
			BindableConverter.convert(values));
	}

	/**
	 * IN を使用した条件句を生成します。
	 * @param column 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotInCriteria(
		Column column,
		Timestamp... values) {
		return createCriteria(
			buildNotInClause(values.length),
			new Column[] { column },
			BindableConverter.convert(values));
	}

	/**
	 * IN を使用した条件句を生成します。
	 * @param column 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotInCriteria(
		Column column,
		Bindable... values) {
		return createCriteria(
			buildNotInClause(values.length),
			new Column[] { column },
			values);
	}

	/**
	 * IN を使用した条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotInCriteria(
		String columnName,
		String... values) {
		return createCriteria(
			buildNotInClause(values.length),
			new Column[] { new PhantomColumn(columnName) },
			toBindables(values));
	}

	/**
	 * IN を使用した条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotInCriteria(
		String columnName,
		Number... values) {
		return createCriteria(
			buildNotInClause(values.length),
			new Column[] { new PhantomColumn(columnName) },
			BindableConverter.convert(values));
	}

	/**
	 * IN を使用した条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotInCriteria(
		String columnName,
		Timestamp... values) {
		return createCriteria(
			buildNotInClause(values.length),
			new Column[] { new PhantomColumn(columnName) },
			BindableConverter.convert(values));
	}

	/**
	 * IN を使用した条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param values 比較する値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotInCriteria(
		String columnName,
		Bindable... values) {
		return createCriteria(
			buildNotInClause(values.length),
			new Column[] { new PhantomColumn(columnName) },
			values);
	}

	private static final String BETWEEN_TEMPLATE = "{0} BETWEEN ? AND ?";

	private static final String NOT_BETWEEN_TEMPLATE = "{0} NOT BETWEEN ? AND ?";

	/**
	 * BETWEEN を使用した条件句を生成します。
	 * @param column 対象となるカラム
	 * @param value1 from
	 * @param value2 to
	 * @return 作成されたインスタンス
	 */
	public static Criteria createBetweenCriteria(
		Column column,
		String value1,
		String value2) {
		return createCriteria(
			BETWEEN_TEMPLATE,
			new Column[] { column },
			new Bindable[] {
				new StringBinder(value1),
				new StringBinder(value2) });
	}

	/**
	 * BETWEEN を使用した条件句を生成します。
	 * @param column 対象となるカラム
	 * @param value1 from
	 * @param value2 to
	 * @return 作成されたインスタンス
	 */
	public static Criteria createBetweenCriteria(
		Column column,
		Number value1,
		Number value2) {
		return createCriteria(
			BETWEEN_TEMPLATE,
			new Column[] { column },
			BindableConverter.convert(value1, value2));
	}

	/**
	 * BETWEEN を使用した条件句を生成します。
	 * @param column 対象となるカラム
	 * @param value1 from
	 * @param value2 to
	 * @return 作成されたインスタンス
	 */
	public static Criteria createBetweenCriteria(
		Column column,
		Timestamp value1,
		Timestamp value2) {
		return createCriteria(
			BETWEEN_TEMPLATE,
			new Column[] { column },
			BindableConverter.convert(value1, value2));
	}

	/**
	 * BETWEEN を使用した条件句を生成します。
	 * @param column 対象となるカラム
	 * @param value1 from
	 * @param value2 to
	 * @return 作成されたインスタンス
	 */
	public static Criteria createBetweenCriteria(
		Column column,
		Bindable value1,
		Bindable value2) {
		return createCriteria(
			BETWEEN_TEMPLATE,
			new Column[] { column },
			new Bindable[] { value1, value2 });
	}

	/**
	 * BETWEEN を使用した条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param value1 from
	 * @param value2 to
	 * @return 作成されたインスタンス
	 */
	public static Criteria createBetweenCriteria(
		String columnName,
		String value1,
		String value2) {
		return createBetweenCriteria(
			new PhantomColumn(columnName),
			value1,
			value2);
	}

	/**
	 * BETWEEN を使用した条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param value1 from
	 * @param value2 to
	 * @return 作成されたインスタンス
	 */
	public static Criteria createBetweenCriteria(
		String columnName,
		Number value1,
		Number value2) {
		return createBetweenCriteria(
			new PhantomColumn(columnName),
			value1,
			value2);
	}

	/**
	 * BETWEEN を使用した条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param value1 from
	 * @param value2 to
	 * @return 作成されたインスタンス
	 */
	public static Criteria createBetweenCriteria(
		String columnName,
		Timestamp value1,
		Timestamp value2) {
		return createBetweenCriteria(
			new PhantomColumn(columnName),
			value1,
			value2);
	}

	/**
	 * BETWEEN を使用した条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param value1 from
	 * @param value2 to
	 * @return 作成されたインスタンス
	 */
	public static Criteria createBetweenCriteria(
		String columnName,
		Bindable value1,
		Bindable value2) {
		return createBetweenCriteria(
			new PhantomColumn(columnName),
			value1,
			value2);
	}

	/**
	 * BETWEEN を使用した条件句を生成します。
	 * @param column 対象となるカラム
	 * @param value1 from
	 * @param value2 to
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotBetweenCriteria(
		Column column,
		String value1,
		String value2) {
		return createCriteria(
			NOT_BETWEEN_TEMPLATE,
			new Column[] { column },
			new Bindable[] {
				new StringBinder(value1),
				new StringBinder(value2) });
	}

	/**
	 * BETWEEN を使用した条件句を生成します。
	 * @param column 対象となるカラム
	 * @param value1 from
	 * @param value2 to
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotBetweenCriteria(
		Column column,
		Number value1,
		Number value2) {
		return createCriteria(
			NOT_BETWEEN_TEMPLATE,
			new Column[] { column },
			BindableConverter.convert(value1, value2));
	}

	/**
	 * BETWEEN を使用した条件句を生成します。
	 * @param column 対象となるカラム
	 * @param value1 from
	 * @param value2 to
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotBetweenCriteria(
		Column column,
		Timestamp value1,
		Timestamp value2) {
		return createCriteria(
			NOT_BETWEEN_TEMPLATE,
			new Column[] { column },
			BindableConverter.convert(value1, value2));
	}

	/**
	 * BETWEEN を使用した条件句を生成します。
	 * @param column 対象となるカラム
	 * @param value1 from
	 * @param value2 to
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotBetweenCriteria(
		Column column,
		Bindable value1,
		Bindable value2) {
		return createCriteria(
			NOT_BETWEEN_TEMPLATE,
			new Column[] { column },
			new Bindable[] { value1, value2 });
	}

	/**
	 * BETWEEN を使用した条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param value1 from
	 * @param value2 to
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotBetweenCriteria(
		String columnName,
		String value1,
		String value2) {
		return createNotBetweenCriteria(
			new PhantomColumn(columnName),
			value1,
			value2);
	}

	/**
	 * BETWEEN を使用した条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param value1 from
	 * @param value2 to
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotBetweenCriteria(
		String columnName,
		Number value1,
		Number value2) {
		return createNotBetweenCriteria(
			new PhantomColumn(columnName),
			value1,
			value2);
	}

	/**
	 * BETWEEN を使用した条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param value1 from
	 * @param value2 to
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotBetweenCriteria(
		String columnName,
		Timestamp value1,
		Timestamp value2) {
		return createNotBetweenCriteria(
			new PhantomColumn(columnName),
			value1,
			value2);
	}

	/**
	 * BETWEEN を使用した条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @param value1 from
	 * @param value2 to
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotBetweenCriteria(
		String columnName,
		Bindable value1,
		Bindable value2) {
		return createNotBetweenCriteria(
			new PhantomColumn(columnName),
			value1,
			value2);
	}

	/**
	 * LIKE を使用した条件句を生成します。
	 * @param type LIKE の位置
	 * @param column 対象となるカラム
	 * @param value '%'を含まない値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createLikeCriteria(
		Match type,
		Column column,
		String value) {
		return type.create(column, value);
	}

	/**
	 * NOT LIKE を使用した条件句を生成します。
	 * @param type LIKE の位置
	 * @param column 対象となるカラム
	 * @param value '%'を含まない値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotLikeCriteria(
		Match type,
		Column column,
		String value) {
		return type.createNot(column, value);
	}

	/**
	 * LIKE を使用した条件句を生成します。
	 * @param type LIKE の位置
	 * @param columnName 対象となるカラム
	 * @param value '%'を含まない値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createLikeCriteria(
		Match type,
		String columnName,
		String value) {
		return type.create(new PhantomColumn(columnName), value);
	}

	/**
	 * NOT LIKE を使用した条件句を生成します。
	 * @param type LIKE の位置
	 * @param columnName 対象となるカラム
	 * @param value '%'を含まない値
	 * @return 作成されたインスタンス
	 */
	public static Criteria createNotLikeCriteria(
		Match type,
		String columnName,
		String value) {
		return type.createNot(new PhantomColumn(columnName), value);
	}

	/**
	 * IS NULL となる条件句を生成します。
	 * @param column 対象となるカラム
	 * @return 作成されたインスタンス
	 */
	public static Criteria createIsNullCriteria(Column column) {
		return NullComparisonOperator.IS_NULL.create(column);
	}

	/**
	 * IS NULL となる条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @return 作成されたインスタンス
	 */
	public static Criteria createIsNullCriteria(String columnName) {
		return NullComparisonOperator.IS_NULL.create(new PhantomColumn(columnName));
	}

	/**
	 * IS NOT NULL となる条件句を生成します。
	 * @param column 対象となるカラム
	 * @return 作成されたインスタンス
	 */
	public static Criteria createIsNotNullCriteria(Column column) {
		return NullComparisonOperator.IS_NOT_NULL.create(column);
	}

	/**
	 * IS NOT NULL となる条件句を生成します。
	 * @param columnName 対象となるカラム
	 * @return 作成されたインスタンス
	 */
	public static Criteria createIsNotNullCriteria(String columnName) {
		return NullComparisonOperator.IS_NOT_NULL.create(new PhantomColumn(columnName));
	}

	/**
	 * NULL かどうかを判定する条件句を生成します。
	 * @param type 'IS NULL' または 'IS NOT NULL'
	 * @param column 対象となるカラム
	 * @return 作成されたインスタンス
	 */
	public static Criteria create(
		NullComparisonOperator type,
		Column column) {
		return type.create(column);
	}

	/**
	 * NULL かどうかを判定する条件句を生成します。
	 * @param type 'IS NULL' または 'IS NOT NULL'
	 * @param columnName 対象となるカラム
	 * @return 作成されたインスタンス
	 */
	public static Criteria create(
		NullComparisonOperator type,
		String columnName) {
		return type.create(new PhantomColumn(columnName));
	}

	/**
	 * 空の条件句を生成します。
	 * @return 生成されたインスタンス
	 */
	public static Criteria create() {
		return new ProxyCriteria();
	}

	/**
	 * 条件句のテンプレートとそれに埋め込むカラムから条件句を生成します。<br>
	 * このテンプレートにはプレースホルダを含めることはできません。
	 * @param clause 条件句のテンプレート
	 * @param columns 対象となるカラム
	 * @return 生成されたインスタンス
	 */
	public static Criteria createCriteria(String clause, Column[] columns) {
		return new Criteria(clause, columns, Binder.EMPTY_ARRAY);
	}

	/**
	 * 条件句のテンプレートとそれに埋め込むカラムから条件句を生成します。<br>
	 * このテンプレートにはプレースホルダを含めることはできません。
	 * @param clause 条件句のテンプレート
	 * @param columnNames 対象となるカラム
	 * @return 生成されたインスタンス
	 */
	public static Criteria createCriteria(
		String clause,
		String[] columnNames) {
		return new Criteria(clause, PhantomColumn.convert(columnNames), Binder.EMPTY_ARRAY);
	}

	/**
	 * パラメータとして渡された条件句をそのまま使用したインスタンスを生成します。<br>
	 * 条件句にはプレースホルダを含めることはできません。
	 * @param clause 条件句
	 * @return 生成されたインスタンス
	 */
	public static Criteria createCriteria(String clause) {
		return new Criteria(clause, Column.EMPTY_ARRAY, Binder.EMPTY_ARRAY);
	}

	/**
	 * プレースホルダを含む条件句のテンプレートとそれに埋め込むカラム、プレースホルダに設定する値から条件句を生成します。
	 * @param clause プレースホルダを含む条件句のテンプレート
	 * @param column 対象となるカラム
	 * @param bindable プレースホルダに設定する値
	 * @return 生成されたインスタンス
	 */
	public static Criteria createCriteria(
		String clause,
		Column column,
		Bindable bindable) {
		return createCriteria(clause, new Column[] { column }, new Bindable[] { bindable });
	}

	/**
	 * プレースホルダを含む条件句のテンプレートとそれに埋め込むカラム、プレースホルダに設定する値から条件句を生成します。
	 * @param clause プレースホルダを含む条件句のテンプレート
	 * @param columnName 対象となるカラム
	 * @param bindable プレースホルダに設定する値
	 * @return 生成されたインスタンス
	 */
	public static Criteria createCriteria(
		String clause,
		String columnName,
		Bindable bindable) {
		return createCriteria(
			clause,
			new Column[] { new PhantomColumn(columnName) },
			new Bindable[] { bindable });
	}

	/**
	 * プレースホルダを含む条件句のテンプレートとそれに埋め込むカラム、プレースホルダに設定する値から条件句を生成します。
	 * @param clause プレースホルダを含む条件句のテンプレート
	 * @param columns 対象となるカラム
	 * @param bindables プレースホルダに設定する値
	 * @return 生成されたインスタンス
	 */
	public static Criteria createCriteria(
		String clause,
		Column[] columns,
		Bindable[] bindables) {
		Binder[] binders = new Binder[bindables.length];
		for (int i = 0; i < bindables.length; i++) {
			binders[i] = bindables[i].toBinder();
		}

		return new Criteria(clause, columns, binders);
	}

	/**
	 * プレースホルダを含む条件句のテンプレートとそれに埋め込むカラム、プレースホルダに設定する値から条件句を生成します。
	 * @param clause プレースホルダを含む条件句のテンプレート
	 * @param columnNames 対象となるカラム
	 * @param bindables プレースホルダに設定する値
	 * @return 生成されたインスタンス
	 */
	public static Criteria createCriteria(
		String clause,
		String[] columnNames,
		Bindable[] bindables) {
		return createCriteria(clause, PhantomColumn.convert(columnNames), bindables);
	}

	/**
	 * コピー用メソッドです。
	 * @param criteria コピーしたいインスタンス
	 * @return 生成されたインスタンス
	 */
	public static Criteria createCriteria(Criteria criteria) {
		return criteria.replicate();
	}

	/**
	 * サブクエリを使用した条件句を生成します。
	 * @param mainRelationship 主となるクエリ側のテーブル
	 * @param subqueryRelationship サブクエリ側のテーブル
	 * @param subquery サブクエリ用条件
	 * @return 生成されたインスタンス
	 * @throws SubqueryException mainRelationship と subQueryRelationship が異なるテーブルを指す場合
	 */
	public static Criteria createSubquery(
		Relationship mainRelationship,
		Relationship subqueryRelationship,
		Criteria subquery) {
		if (!mainRelationship.getTablePath().equals(subqueryRelationship.getTablePath()))
			throw new SubqueryException("異なるテーブルを結合しようとしています");

		return createSubquery(
			mainRelationship.getPrimaryKeyColumns(),
			subqueryRelationship.getPrimaryKeyColumns(),
			subquery);
	}

	/**
	 * サブクエリを使用した条件句を生成します。
	 * @param columns 主となるクエリ側のカラム
	 * @param subqueryColumns サブクエリ側のカラム
	 * @param subquery サブクエリ用条件
	 * @return 生成されたインスタンス
	 * @throws SubqueryException サブクエリ側のカラムが空の場合
	 * @throws SubqueryException サブクエリ側のカラムが属する {@link Relationship} のルートが異なる場合
	 * @throws SubqueryException サブクエリ用条件内のカラムが属する {@link Relationship} のルートが、サブクエリ側のカラムが属すると異なる場合
	 */
	public static Criteria createSubquery(
		Column[] columns,
		Column[] subqueryColumns,
		Criteria subquery) {
		if (subqueryColumns.length == 0) throw new SubqueryException("subQueryColumns が空です");

		QueryBuilder builder = new QueryBuilder(
			new FromClause(subqueryColumns[0].getRootRelationship().getTablePath()));
		builder.setWhereClause(subquery);
		SelectClause selectClause = new SelectClause();
		for (Column column : subqueryColumns) {
			selectClause.add(column);
		}

		builder.setSelectClause(selectClause);
		return createSubquery(columns, builder, false);
	}

	/**
	 * サブクエリを使用した条件句を生成します。
	 * @param columns 主となるクエリ側のカラム
	 * @param subquery サブクエリ
	 * @param notIn NOT IN の場合 true
	 * @return 生成されたインスタンス
	 * @throws SubqueryException 主となるクエリ側のカラムが空の場合
	 * @throws SubqueryException 主となるクエリ側のカラム数とサブクエリ側の select 句のカラムの数が異なる場合
	 * @throws SubqueryException サブクエリ側の select 句のカラムが主キーに含まれない場合
	 */
	public static Criteria createSubquery(
		Column[] columns,
		QueryBuilder subquery,
		boolean notIn) {
		if (columns.length == 0) throw new SubqueryException("columns が空です");

		Column[] subQueryColumns = subquery.getSelectClause().getColumns();

		if (columns.length != subQueryColumns.length) {
			throw new SubqueryException("項目数が一致しません");
		}

		for (Column column : subQueryColumns) {
			if (!column.getColumnMetadata().isNotNull())
				throw new SubqueryException("IN を使用しているため、サブクエリの select 句のカラムは、すべて NOT NULL である必要があります。");
		}

		return createSubqueryWithoutCheck(columns, subquery, notIn);
	}

	/**
	 * サブクエリを使用した条件句を生成します。
	 * @param columns 主となるクエリ側のカラム
	 * @param subquery サブクエリ
	 * @param notIn NOT IN の場合 true
	 * @return 生成されたインスタンス
	 * @throws SubqueryException 主となるクエリ側のカラムが空の場合
	 * @throws SubqueryException 主となるクエリ側のカラム数とサブクエリ側の select 句のカラムの数が異なる場合
	 * @throws SubqueryException サブクエリ側の select 句のカラムが主キーに含まれない場合
	 */
	public static Criteria createSubqueryWithoutCheck(
		Column[] columns,
		QueryBuilder subquery,
		boolean notIn) {
		if (columns.length == 0) throw new SubqueryException("columns が空です");

		List<String> columnPartList = new LinkedList<>();
		for (int i = 0; i < columns.length; i++) {
			columnPartList.add("{" + i + "}");
		}

		//サブクエリのFrom句からBinderを取り出す前にsql化して内部のFrom句をマージしておかないとBinderが準備されないため、先に実行
		String subqueryString = "(" + String.join(", ", columnPartList) + ") " + (notIn ? "NOT IN" : "IN") + " (" + subquery.sql() + ")";

		List<Binder> binders = new ComplementerValues(subquery).binders();
		return new Criteria(subqueryString, columns, binders.toArray(new Binder[binders.size()]));
	}

	private static String buildInClause(int length) {
		return "{0} IN ("
			+ String.join(
				", ",
				Stream.generate(() -> "?").limit(length).collect(Collectors.toList()))
			+ ")";
	}

	private static String buildNotInClause(int length) {
		return "{0} NOT IN ("
			+ String.join(
				", ",
				Stream.generate(() -> "?").limit(length).collect(Collectors.toList()))
			+ ")";
	}

	private static Bindable[] toBindables(String[] values) {
		List<StringBinder> bindables = Arrays.stream(values)
			.map(v -> new StringBinder(v))
			.collect(Collectors.toList());
		return bindables.toArray(new Bindable[bindables.size()]);
	}
}
