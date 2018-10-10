package org.blendee.sql;

/**
 * SELECT 文の PARTITION BY 句を表すクラスです。
 * @author 千葉 哲嗣
 */
public class PartitionByClause extends ListClause<PartitionByClause> {

	@SuppressWarnings("javadoc")
	public PartitionByClause(QueryId id) {
		super(id);
	}

	@Override
	protected PartitionByClause createNewInstance(QueryId id) {
		return new PartitionByClause(id);
	}

	@Override
	String getKeyword() {
		return hasElements() ? "PARTITION BY" : "";
	}
}
