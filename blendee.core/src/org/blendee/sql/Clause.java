package org.blendee.sql;

import static org.blendee.sql.FromClause.JoinType.LEFT_OUTER_JOIN;

import java.util.List;

import org.blendee.jdbc.ChainPreparedStatementComplementer;

/**
 * SELECT 文を構成する各句の抽象基底クラスです。
 * @author 千葉 哲嗣
 */
public abstract class Clause implements ChainPreparedStatementComplementer {

	@SuppressWarnings("javadoc")
	protected final QueryId queryId;

	@SuppressWarnings("javadoc")
	protected Clause(QueryId queryId) {
		this.queryId = queryId;
	}

	/**
	 * SQL 文の句を生成します。
	 */
	@Override
	public String toString() {
		return toString(true);
	}

	/**
	 * SQL 文の句を生成します。
	 * @param joining テーブル結合している場合、 true
	 * @return SQL 文の句
	 */
	public String toString(boolean joining) {
		return " " + getKeyword() + " " + SQLFragmentFormat.execute(getTemplate(), toString(joining, getColumns()));
	}

	/**
	 * この句が含むカラムを返します。
	 * @return この句が含むカラム
	 */
	public Column[] getColumns() {
		List<Column> columns = getColumnsInternal();
		return columns.toArray(new Column[columns.size()]);
	}

	/**
	 * このインスタンスが持つ {@link Column} を、パラメータの {@link Relationship} のツリーに含まれるカラムに置き換えます。
	 * @param root このインスタンスが含まれるべきツリーのルート
	 * @throws IllegalStateException 結合できないテーブルのカラムを使用している場合
	 * @throws IllegalStateException ツリー内に同一テーブルが複数あるため、あいまいなカラム指定がされている場合
	 */
	public final void prepareColumns(Relationship root) {
		List<Column> columns = getColumnsInternal();
		columns.forEach(c -> c.prepareForSQL(root));
	}

	/**
	 * この句が含むカラムの個数を返します。
	 * @return この句が含むカラムの個数
	 */
	public abstract int getColumnsSize();

	/**
	 * このインスタンスの複製を生成し、返します。
	 * @return このインスタンスの複製
	 */
	public abstract Clause replicate();

	abstract String getTemplate();

	abstract String getKeyword();

	abstract List<Column> getColumnsInternal();

	void join(FromClause fromClause) {
		Column[] columns = getColumns();
		for (int i = 0; i < columns.length; i++) {
			columns[i].setRelationship(r -> fromClause.join(LEFT_OUTER_JOIN, r));
		}
	}

	private String[] toString(boolean joining, Column[] columns) {
		int length = columns.length;
		String[] columnNames = new String[length];
		for (int i = 0; i < length; i++) {
			if (joining) {
				columnNames[i] = columns[i].getComplementedName(queryId);
			} else {
				columnNames[i] = columns[i].getName();
			}
		}

		return columnNames;
	}
}
