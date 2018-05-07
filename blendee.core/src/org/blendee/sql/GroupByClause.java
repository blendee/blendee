package org.blendee.sql;

/**
 * SELECT 文の GROUP BY 句を表すクラスです。
 * @author 千葉 哲嗣
 */
public class GroupByClause extends ListQueryClause<GroupByClause> {

	@Override
	protected GroupByClause createNewInstance() {
		return new GroupByClause();
	}

	/**
	 * この GROUP BY 句にカラムを追加します。
	 * @param order JOIN したときの順序
	 * @param column カラム
	 */
	public void add(int order, Column column) {
		ListQueryBlock block = new ListQueryBlock(order);
		block.addColumn(column);
		block.addTemplate("{0}");
		addBlock(block);
	}

	@Override
	String getKeyword() {
		return hasElements() ? "GROUP BY" : "";
	}
}
