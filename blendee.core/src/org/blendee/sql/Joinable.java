package org.blendee.sql;

/**
 * JOIN 時に自信の名称を補完することのできるオブジェクトを表すインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface Joinable {

	/**
	 * このオブジェクトの名称を返します。
	 * @return このオブジェクトの名称
	 */
	String getName();

	/**
	 * テーブル別名を含むオブジェクト名を返します。
	 * @return テーブル別名を含むオブジェクト名
	 */
	String getComplementedName();

	/**
	 * 属するテーブルを表す {@link Relationship} を返します。
	 * @return {@link Relationship}
	 */
	Relationship getRelationship();

	/**
	 * インスタンスのコピーを返します。
	 * @return コピー
	 */
	Joinable replicate();

	/**
	 * SQL 化にあたっての検査等を行います。
	 * @param sqlRoot 自動生成クエリのルート
	 */
	default void prepareForSQL(Relationship sqlRoot) {
		if (!sqlRoot.isRoot()) throw new IllegalStateException(sqlRoot + " はルートではありません");
		if (!getRelationship().getRoot().equals(sqlRoot))
			throw new IllegalStateException(getComplementedName() + " は SQL 文の Relationship のツリーに含まれないカラムです");
	}
}
