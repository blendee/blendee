package org.blendee.sql;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.blendee.internal.U;

/**
 * SELECT 句で使用するウィンドウ関数を表すクラスです。
 *
 * @author 千葉 哲嗣
 */
public class WindowFunction {

	/**
	 * ウィンドウ関数のファンクション部を表します。
	 */
	public static interface Function {

		/**
		 * ファンクション部のテンプレートを返します。
		 *
		 * @return SQL 文のテンプレート
		 */
		String getTemplate();

		/**
		 * ファンクション部に含まれるカラムを返します。
		 *
		 * @return カラム
		 */
		Column[] getColumns();
	}

	private static abstract class SimpleFunction implements Function {

		private static final Column[] emptyArray = {};

		@Override
		public String getTemplate() {
			return stringExpress();
		}

		@Override
		public Column[] getColumns() {
			return emptyArray;
		}

		abstract String stringExpress();
	}

	/**
	 * ROW_NUMBER()
	 */
	public static final SimpleFunction ROW_NUMBER = new SimpleFunction() {

		@Override
		String stringExpress() {
			return "ROW_NUMBER()";
		}
	};

	/**
	 * RANK()
	 */
	public static final SimpleFunction RANK = new SimpleFunction() {

		@Override
		String stringExpress() {
			return "RANK()";
		}
	};

	/**
	 * DENSE_RANK()
	 */
	public static final SimpleFunction DENSE_RANK = new SimpleFunction() {

		@Override
		String stringExpress() {
			return "DENSE_RANK()";
		}
	};

	/**
	 * CUME_DIST()
	 */
	public static final SimpleFunction CUME_DIST = new SimpleFunction() {

		@Override
		String stringExpress() {
			return "CUME_DIST()";
		}
	};

	private final Function function;

	private final OverClause overClause;

	/**
	 * ウィンドウ関数を作成します。
	 *
	 * @param function ファンクション部
	 * @param overClause {@link OverClause}
	 */
	public WindowFunction(Function function, OverClause overClause) {
		this.function = function;
		this.overClause = overClause;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	String getTemplate() {
		return function.getTemplate() + " " + overClause.getTemplate(function.getColumns().length);
	}

	Column[] getColumns() {
		List<Column> columns = new LinkedList<>();
		columns.addAll(Arrays.asList(function.getColumns()));
		columns.addAll(Arrays.asList(overClause.getColumns()));
		return columns.toArray(new Column[columns.size()]);
	}
}
