package org.blendee.sql;

/**
 * エイリアス名です。
 * @author 千葉 哲嗣
 */
public interface QueryId {

	/**
	 * @return alias
	 */
	String getId();

	@SuppressWarnings("javadoc")
	default String toString(Relationship relationship) {
		return relationship.getTablePath() + " " + getId() + relationship.getId();
	}

	@SuppressWarnings("javadoc")
	default String toComplementedColumnString(String complementedColumn) {
		return getId() + complementedColumn;
	}
}
