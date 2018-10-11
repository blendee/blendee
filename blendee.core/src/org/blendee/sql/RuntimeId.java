package org.blendee.sql;

/**
 * 実行時のテーブル識別用 ID です。<br>
 * テーブル名愛リアスに付与される形で使用されます。
 * @author 千葉 哲嗣
 */
public interface RuntimeId {

	/**
	 * @return id
	 */
	String getId();

	@SuppressWarnings("javadoc")
	default String toString(Relationship relationship) {
		return relationship.getTablePath() + " " + toAlias(relationship);
	}

	@SuppressWarnings("javadoc")
	default String toAlias(Relationship relationship) {
		return getId() + relationship.getId();
	}

	@SuppressWarnings("javadoc")
	default String toComplementedColumnName(String complementedColumn) {
		return getId() + complementedColumn;
	}
}
