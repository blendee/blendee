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
 * @see QueryBuilder#setOrderByClause(OrderByClause)
 */
public class OrderByClause extends SimpleQueryClause<OrderByClause> {

	/**
	 * ソートする方向を表す列挙型です。
	 * @author 千葉 哲嗣
	 */
	public enum Direction {

		/**
		 * 順方向
		 */
		ASC("ASC"),

		/**
		 * 逆方向
		 */
		DESC("DESC");

		private final String value;

		private Direction(String value) {
			this.value = " " + value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	private final List<DirectionalColumn> added = new LinkedList<>();

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
	 * @param columnName 追加するカラム
	 * @param direction 方向
	 */
	public void add(String columnName, Direction direction) {
		add(new PhantomColumn(columnName), direction);
	}

	/**
	 * この ORDER 句にカラムを追加します。
	 * @param column 追加するカラム
	 */
	public void add(DirectionalColumn column) {
		clearCache();
		addColumn(column.column);
		addTemplate("{" + getTemplatesSize() + "}" + column.direction);
		added.add(column);
	}

	/**
	 * 現時点で登録済みのカラムを {@link DirectionalColumn} として返します。
	 * @return {@link DirectionalColumn}
	 */
	public DirectionalColumn[] getDirectionalColumns() {
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
		return getColumnsSize() == 0 ? "" : "ORDER BY";
	}
}
