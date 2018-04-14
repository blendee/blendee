package org.blendee.sql;

/**
 * SELECT 文の PARTITION BY 句を表すクラスです。
 * @author 千葉 哲嗣
 */
public class PartitionByClause extends ListQueryClause<PartitionByClause> {

	/**
	 * PARTITION BY 句に新しいカラムを追加します。
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
	 * PARTITION BY 句に新しいカラムを追加します。
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
	protected PartitionByClause createNewInstance() {
		return new PartitionByClause();
	}

	@Override
	String getKeyword() {
		return getColumnsSize() == 0 ? "" : "PARTITION BY";
	}
}
