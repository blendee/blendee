package org.blendee.sql;

/**
 * SELECT 文の PARTITION BY 句を表すクラスです。
 * @author 千葉 哲嗣
 */
public class PartitionByClause extends ListQueryClause<PartitionByClause> {

	@Override
	protected PartitionByClause createNewInstance() {
		return new PartitionByClause();
	}

	@Override
	String getKeyword() {
		return hasElements() ? "PARTITION BY" : "";
	}
}
