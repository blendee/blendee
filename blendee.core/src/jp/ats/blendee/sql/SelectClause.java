package jp.ats.blendee.sql;

import java.util.LinkedList;
import java.util.List;

/**
 * SELECT 句を表すクラスです。
 *
 * @author 千葉 哲嗣
 * @see QueryBuilder#setSelectClause(SelectClause)
 */
public class SelectClause extends SimpleQueryClause<SelectClause> {

	/**
	 * 'SELECT *' となる SELECT 句です。
	 */
	public static final SelectClause SELECT_ALL_CLAUSE = new SelectClause() {

		@Override
		public String toString(boolean joining) {
			return "SELECT * ";
		}

		@Override
		public SelectClause replicate() {
			return this;
		}

		@Override
		void addColumn(Column column) {
			throw new UnsupportedOperationException();
		}

		@Override
		void addTemplate(String template) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(this);
		}
	};

	/**
	 * 'SELECT COUNT(*)' となる SELECT 句です。
	 */
	public static final SelectClause COUNT_CLAUSE = new SelectClause() {

		@Override
		public String toString(boolean joining) {
			return "SELECT COUNT(*) ";
		}

		@Override
		public SelectClause replicate() {
			return this;
		}

		@Override
		void addColumn(Column column) {
			throw new UnsupportedOperationException();
		}

		@Override
		void addTemplate(String template) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(this);
		}
	};

	/**
	 * この SELECT 句に新しいカラムを追加します。
	 *
	 * @param columns 追加するカラム
	 */
	public void add(Column... columns) {
		clearCache();
		for (Column column : columns) {
			addColumn(column);
			addTemplate("{" + getTemplatesSize() + "} AS " + column.getID());
		}
	}

	/**
	 * この SELECT 句に新しいカラムを追加します。
	 * <br>
	 * ただし項目名のエイリアスは、生成される SQL 文には追加されません。
	 *
	 * @param columnNames 追加するカラム
	 */
	public void add(String... columnNames) {
		clearCache();
		for (String columnName : columnNames) {
			PhantomColumn column = new PhantomColumn(columnName);
			addColumn(column);
			addTemplate("{" + getTemplatesSize() + "}");
		}
	}

	/**
	 * この SELECT 句に {@link Relationship} に属する全てのカラムを追加します。
	 *
	 * @param relationship 追加するカラムを持つ {@link Relationship}
	 */
	public void add(Relationship relationship) {
		add(relationship.getColumns());
	}

	/**
	 * この SELECT 句に記述可能な SQL 文のテンプレートを追加します。
	 *
	 * @param template SQL 文のテンプレート
	 * @param columns SQL 文に含まれるカラム
	 * @see SQLFragmentFormat
	 */
	public void add(String template, Column... columns) {
		clearCache();
		List<String> localTemplates = new LinkedList<>();
		for (int i = 0; i < columns.length; i++) {
			localTemplates.add("{" + getColumnsSize() + "}");
			addColumn(columns[i]);
		}

		addTemplate(
			SQLFragmentFormat.execute(
				template.trim(),
				localTemplates.toArray(new String[localTemplates.size()])));
	}

	/**
	 * この SELECT 句に記述可能な SQL 文のテンプレートを追加します。
	 *
	 * @param template SQL 文のテンプレート
	 * @param columnNames SQL 文に含まれるカラム
	 * @see SQLFragmentFormat
	 */
	public void add(String template, String... columnNames) {
		add(template, PhantomColumn.convert(columnNames));
	}

	/**
	 * この SELECT 句にウィンドウ関数を追加します。
	 *
	 * @param function {@link WindowFunction}
	 * @param alias 別名
	 */
	public void add(WindowFunction function, String alias) {
		clearCache();
		add(function.getTemplate() + " AS " + alias, function.getColumns());
	}

	@Override
	protected SelectClause createNewInstance() {
		return new SelectClause();
	}

	@Override
	String getKeyword() {
		return "SELECT";
	}
}
