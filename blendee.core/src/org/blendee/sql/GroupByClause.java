package org.blendee.sql;

/**
 * SELECT 文の GROUP BY 句を表すクラスです。
 * @author 千葉 哲嗣
 */
public class GroupByClause extends ListClause<GroupByClause> {

	@SuppressWarnings("javadoc")
	public GroupByClause(RuntimeId id) {
		super(id);
	}

	@Override
	protected GroupByClause createNewInstance(RuntimeId id) {
		return new GroupByClause(id);
	}

	/**
	 * この GROUP BY 句にカラムを追加します。
	 * @param order JOIN したときの順序
	 * @param column カラム
	 */
	public void add(int order, Column column) {
		ListQueryBlock block = new ListQueryBlock(queryId, order);
		block.addColumn(column);
		block.addTemplate("{0}");
		addBlock(block);
	}

	@Override
	String getKeyword() {
		return hasElements() ? "GROUP BY" : "";
	}
}
