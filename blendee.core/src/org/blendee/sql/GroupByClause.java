package org.blendee.sql;

/**
 * SELECT 文の GROUP BY 句を表すクラスです。
 * @author 千葉 哲嗣
 */
public class GroupByClause extends SimpleQueryClause<GroupByClause> {

	/**
	 * GROUP BY 句に新しいカラムを追加します。
	 * @param columns 新しいカラム
	 */
	public void add(Column... columns) {
		clearCache();
		for (Column column : columns) {
			addColumn(column);
			addTemplate("{" + getTemplatesSize() + "}");
		}
	}

	/**
	 * GROUP BY 句に新しいカラムを追加します。
	 * @param columnNames 新しいカラム
	 */
	public void add(String... columnNames) {
		clearCache();
		for (String columnName : columnNames) {
			addColumn(new PhantomColumn(columnName));
			addTemplate("{" + getTemplatesSize() + "}");
		}
	}

	@Override
	protected GroupByClause createNewInstance() {
		return new GroupByClause();
	}

	@Override
	String getKeyword() {
		return getColumnsSize() == 0 ? "" : "GROUP BY";
	}
}
