package org.blendee.sql;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.blendee.internal.U;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.TablePath;

/**
 * SELECT 文の ORDER BY 句を表すクラスです。
 * @author 千葉 哲嗣
 * @see SelectStatementBuilder#setOrderByClause(OrderByClause)
 */
public class OrderByClause extends ListClause<OrderByClause> {

	/**
	 * ソートする方向を表す列挙型です。
	 * @author 千葉 哲嗣
	 */
	public enum Direction {

		/**
		 * 順方向
		 */
		ASC(" ASC"),

		/**
		 * 逆方向
		 */
		DESC(" DESC"),

		/**
		 * 順方向 NULL 先頭
		 */
		ASC_NULLS_FIRST(" ASC NULLS FIRST"),

		/**
		 * 順方向 NULL 末尾
		 */
		ASC_NULLS_LAST(" ASC NULLS LAST"),

		/**
		 * 逆方向 NULL 先頭
		 */
		DESC_NULLS_FIRST(" DESC NULLS FIRST"),

		/**
		 * 逆方向 NULL 末尾
		 */
		DESC_NULLS_LAST(" DESC NULLS LAST"),

		/**
		 * 指定なし
		 */
		NONE("");

		private final String value;

		private Direction(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	private final List<DirectionalColumn> added = new LinkedList<>();

	private boolean canGetDirectionalColumns = true;

	/**
	 * パラメータで示されたテーブルの主キーで ORDER 句を生成します。
	 * @param path 対象テーブル
	 * @param direction 方向
	 * @return 生成されたインスタンス
	 */
	public static OrderByClause createPrimaryKeyOrder(
		TablePath path,
		Direction direction) {
		Column[] columns = ContextManager.get(RelationshipFactory.class).getInstance(path).getPrimaryKeyColumns();
		OrderByClause clause = new OrderByClause();
		for (Column column : columns)
			clause.add(column, direction);
		return clause;
	}

	/**
	 * 順方向でこの ORDER 句にカラムを追加します。
	 * @param columns 追加するカラム
	 */
	public void asc(Column... columns) {
		for (Column column : columns) {
			add(column, Direction.ASC);
		}
	}

	/**
	 * 順方向でこの ORDER 句にカラムを追加します。
	 * @param columnNames 追加するカラム
	 */
	public void asc(String... columnNames) {
		for (String columnName : columnNames) {
			add(columnName, Direction.ASC);
		}
	}

	/**
	 * 逆方向でこの ORDER 句にカラムを追加します。
	 * @param columns 追加するカラム
	 */
	public void desc(Column... columns) {
		for (Column column : columns) {
			add(column, Direction.DESC);
		}
	}

	/**
	 * 逆方向でこの ORDER 句にカラムを追加します。
	 * @param columnNames 追加するカラム
	 */
	public void desc(String... columnNames) {
		for (String columnName : columnNames) {
			add(columnName, Direction.DESC);
		}
	}

	/**
	 * この ORDER 句にカラムを追加します。
	 * @param column 追加するカラム
	 * @param direction 方向
	 */
	public void add(Column column, Direction direction) {
		add(new DirectionalColumn(column, direction));
	}

	/**
	 * この ORDER 句にカラムを追加します。
	 * @param order JOIN したときの順序
	 * @param column 追加するカラム
	 * @param direction 方向
	 */
	public void add(int order, Column column, Direction direction) {
		add(order, new DirectionalColumn(column, direction));
	}

	/**
	 * この ORDER 句にカラムを追加します。
	 * @param columnName 追加するカラム
	 * @param direction 方向
	 */
	public void add(String columnName, Direction direction) {
		add(new PhantomColumn(columnName), direction);
	}

	/**
	 * この ORDER BY 句に記述可能な SQL 文のテンプレートを追加します。
	 * @param template SQL 文のテンプレート
	 * @param direction 方向
	 * @param columns SQL 文に含まれるカラム
	 * @see SQLFragmentFormat
	 */
	public void add(String template, Direction direction, Column... columns) {
		clearCache();

		ListQueryBlock block = new ListQueryBlock();
		for (int i = 0; i < columns.length; i++) {
			block.addColumn(columns[i]);
		}

		block.addTemplate(template + direction);

		addBlock(block);

		canGetDirectionalColumns = false;
	}

	/**
	 * この ORDER BY 句に記述可能な SQL 文のテンプレートを追加します。
	 * @param order JOIN したときの順序
	 * @param template SQL 文のテンプレート
	 * @param direction 方向
	 * @param columns SQL 文に含まれるカラム
	 * @see SQLFragmentFormat
	 */
	public void add(int order, String template, Direction direction, Column... columns) {
		clearCache();

		ListQueryBlock block = new ListQueryBlock(order);
		for (int i = 0; i < columns.length; i++) {
			block.addColumn(columns[i]);
		}

		block.addTemplate(template + direction.value);

		addBlock(block);

		canGetDirectionalColumns = false;
	}

	/**
	 * この ORDER 句にカラムを追加します。
	 * @param column 追加するカラム
	 */
	public void add(DirectionalColumn column) {
		add(DEFAULT_ORDER, column);
	}

	/**
	 * この ORDER 句にカラムを追加します。
	 * @param order JOIN したときの順序
	 * @param column 追加するカラム
	 */
	public void add(int order, DirectionalColumn column) {
		clearCache();
		addInternal(order, column.column, "{0}" + column.direction);
		added.add(column);
	}

	/**
	 * 現時点で登録済みのカラムを {@link DirectionalColumn} として返します。
	 * @return {@link DirectionalColumn}
	 */
	public DirectionalColumn[] getDirectionalColumns() {
		if (!canGetDirectionalColumns)
			throw new IllegalStateException("複数カラムで一つの DIrection の要素が追加されたため、 DirectionalColumn は不完全です。");

		return added.toArray(new DirectionalColumn[added.size()]);
	}

	@Override
	protected OrderByClause createNewInstance() {
		return new OrderByClause();
	}

	/**
	 * {@link OrderByClause} に登録されたカラムとソート方向を持つクラスです。
	 */
	public static class DirectionalColumn {

		private final Column column;

		private final Direction direction;

		/**
		 * 唯一のコンストラクタです。
		 * @param column {@link Column}
		 * @param direction {@link Direction}
		 */
		public DirectionalColumn(Column column, Direction direction) {
			this.column = Objects.requireNonNull(column);
			this.direction = Objects.requireNonNull(direction);
		}

		/**
		 * カラムを返します。
		 * @return {@link Column}
		 */
		public Column getColumn() {
			return column;
		}

		/**
		 * ソート方向を返します。
		 * @return {@link Direction}
		 */
		public Direction getDirection() {
			return direction;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof DirectionalColumn)) return false;
			DirectionalColumn another = (DirectionalColumn) o;
			return column.equals(another.column) && direction.equals(another.direction);
		}

		@Override
		public int hashCode() {
			return Objects.hash(column, direction);
		}

		@Override
		public String toString() {
			return U.toString(this);
		}
	}

	@Override
	String getKeyword() {
		return hasElements() ? "ORDER BY" : "";
	}
}
