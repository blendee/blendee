package org.blendee.assist;

import org.blendee.sql.Column;
import org.blendee.sql.Relationship;
import org.blendee.sql.RuntimeId;

/**
 * 汎用的な {@link SelectStatement} カラムクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 */
public class TableFacadeColumn implements AssistColumn {

	private final Statement statement;

	private final Column column;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param assist 条件作成に必要な情報を持った {@link TableFacadeAssist}
	 * @param name カラム名
	 */
	public TableFacadeColumn(TableFacadeAssist assist, String name) {
		statement = new TableFacadeStatement(assist.getSelectStatement().getRuntimeId());
		column = Helper.buildRuntimeIdColumn(assist, name);
	}

	@Override
	public Statement statement() {
		return statement;
	}

	/**
	 * このインスタンスが表すカラムを {@link Column} として返します。
	 * @return カラムインスタンス
	 */
	@Override
	public Column column() {
		return column;
	}

	private static class TableFacadeStatement implements Statement {

		private final RuntimeId id;

		private TableFacadeStatement(RuntimeId id) {
			this.id = id;
		}

		@Override
		public Relationship getRootRealtionship() {
			throw new UnsupportedOperationException();
		}

		@Override
		public LogicalOperators<?> getWhereLogicalOperators() {
			throw new UnsupportedOperationException();
		}

		@Override
		public RuntimeId getRuntimeId() {
			return id;
		}

		@Override
		public void forSubquery(boolean forSubquery) {
		}
	}
}
