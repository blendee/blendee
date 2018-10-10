package org.blendee.sql;

/**
 * DISTINCT を使用した SELECT 句を表すクラスです。
 * @author 千葉 哲嗣
 */
public class SelectDistinctClause extends SelectClause {

	@SuppressWarnings("javadoc")
	public SelectDistinctClause(QueryId id) {
		super(id);
	}

	@Override
	protected SelectClause createNewInstance(QueryId id) {
		return new SelectDistinctClause(id);
	}

	@Override
	String getKeyword() {
		return "SELECT DISTINCT";
	}
}
