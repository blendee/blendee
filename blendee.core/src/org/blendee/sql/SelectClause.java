package org.blendee.sql;

import org.blendee.jdbc.ChainPreparedStatementComplementer;

/**
 * SELECT 句を表すクラスです。
 * @author 千葉 哲嗣
 * @see SelectStatementBuilder#setSelectClause(SelectClause)
 */
public class SelectClause extends ListClause<SelectClause> {

	/**
	 * この SELECT 句に新しいカラムをエイリアス付きで追加します。
	 * @param columns 追加するカラム
	 */
	@Override
	public void add(Column... columns) {
		add(DEFAULT_ORDER, columns);
	}

	/**
	 * この SELECT 句に新しいカラムをエイリアス付きで追加します。
	 * @param order JOIN したときの順序
	 * @param columns 追加するカラム
	 */
	public void add(int order, Column... columns) {
		clearCache();
		for (Column column : columns) {
			addInternal(order, column, "{0} AS " + column.getId());
		}
	}

	/**
	 * この SELECT 句に {@link Relationship} に属する全てのカラムを追加します。
	 * @param relationship 追加するカラムを持つ {@link Relationship}
	 */
	public void add(Relationship relationship) {
		add(relationship.getColumns());
	}

	/**
	 * この SELECT 句に記述可能な SQL 文のテンプレートを追加します。
	 * @param template SQL 文のテンプレート
	 * @param columns SQL 文に含まれるカラム
	 * @see SQLFragmentFormat
	 */
	public void add(String template, Column... columns) {
		add(DEFAULT_ORDER, template, columns);
	}

	/**
	 * この SELECT 句に記述可能な SQL 文のテンプレートを追加します。
	 * @param order JOIN したときの順序
	 * @param template SQL 文のテンプレート
	 * @param columns SQL 文に含まれるカラム
	 * @see SQLFragmentFormat
	 */
	public void add(int order, String template, Column... columns) {
		add(order, template, columns, null);
	}

	/**
	 * この SELECT 句に記述可能な SQL 文のテンプレートを追加します。
	 * @param order JOIN したときの順序
	 * @param template SQL 文のテンプレート
	 * @param columns SQL 文に含まれるカラム
	 * @param complementer {@link ChainPreparedStatementComplementer}
	 * @see SQLFragmentFormat
	 */
	public void add(
		int order,
		String template,
		Column[] columns,
		ChainPreparedStatementComplementer complementer) {
		clearCache();

		ListQueryBlock block = new ListQueryBlock(order);

		for (Column column : columns)
			block.addColumn(column);

		if (complementer != null)
			block.setComplementer(complementer);

		block.addTemplate(template.trim());

		addBlock(block);
	}

	/**
	 * この SELECT 句に記述可能な SQL 文のテンプレートを追加します。
	 * @param template SQL 文のテンプレート
	 * @param columnNames SQL 文に含まれるカラム
	 * @see SQLFragmentFormat
	 */
	public void addWithTemplate(String template, String... columnNames) {
		add(template, PhantomColumn.convert(columnNames));
	}

	/**
	 * この SELECT 句にウィンドウ関数を追加します。
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
