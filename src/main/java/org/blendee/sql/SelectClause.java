package org.blendee.sql;

/**
 * SELECT 句を表すクラスです。
 * @author 千葉 哲嗣
 * @see SQLQueryBuilder#setSelectClause(SelectClause)
 */
public class SelectClause extends ListClause<SelectClause> {

	/**
	 * @param id {@link RuntimeId}
	 */
	public SelectClause(RuntimeId id) {
		super(id);
	}

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
		for (var column : columns) {
			addInternal(order, column, "{0} AS " + runtimeId.toComplementedColumnName(column.getId()));
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
		add(function.getTemplate() + " AS " + alias, function.getColumns());
	}

	@Override
	protected SelectClause createNewInstance(RuntimeId id) {
		return new SelectClause(id);
	}

	@Override
	String getKeyword() {
		return "SELECT";
	}

	boolean isValid() {
		return getColumnsSize() > 0;
	}
}
