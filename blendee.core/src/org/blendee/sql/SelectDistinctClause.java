package org.blendee.sql;

/**
 * DISTINCT を使用した SELECT 句を表すクラスです。
 * @author 千葉 哲嗣
 */
public class SelectDistinctClause extends SelectClause {

	@Override
	protected SelectClause createNewInstance() {
		return new SelectDistinctClause();
	}

	@Override
	String getKeyword() {
		return "SELECT DISTINCT";
	}
}
