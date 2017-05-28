package org.blendee.sql;

/**
 * 検索条件として使用可能なクラスのインターフェイスを定義します。
 * <br>
 * このインターフェイスを実装するクラスは、どのカラムに値がマッピングされるかを知っている必要があります。
 *
 * @author 千葉 哲嗣
 */
@FunctionalInterface
public interface Searchable {

	/**
	 * SQL文の検索条件を返します。
	 *
	 * @param relationship 検索条件を取得するための元となる {@link Relationship}
	 * @return relationship からたどれる {@link Relationship} から作成された条件
	 */
	Condition getCondition(Relationship relationship);
}
