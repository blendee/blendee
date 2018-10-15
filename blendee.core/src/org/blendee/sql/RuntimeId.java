package org.blendee.sql;

/**
 * 実行時のテーブル識別用 ID です。<br>
 * テーブル名エイリアスに付与される形で使用されます。
 * @author 千葉 哲嗣
 */
public interface RuntimeId {

	/**
	 * ID を文字列として返します。
	 * @return id ID
	 */
	String getId();

	/**
	 * FROM 句でのテーブル名部分を返します。
	 * @param relationship 対象テーブル
	 * @return FROM 句で使用するテーブル名部分
	 */
	default String toString(Relationship relationship) {
		return relationship.getTablePath() + " " + toAlias(relationship);
	}

	/**
	 * テーブル名エイリアスを返します。
	 * @param relationship 対象テーブル
	 * @return テーブル名エイリアス
	 */
	default String toAlias(Relationship relationship) {
		return getId() + relationship.getId();
	}

	/**
	 * テーブル名エイリアスが補完されたカラム名を返します。
	 * @param complementedColumn runtime ではないテーブル名エイリアスを持つカラム名
	 * @return カラム名
	 */
	default String toComplementedColumnName(String complementedColumn) {
		return getId() + complementedColumn;
	}
}
