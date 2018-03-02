package org.blendee.sql;

/**
 * SQL 文を各データベース向けに微調整したり拡張する機能を定義したインターフェイスです。
 * @author 千葉 哲嗣
 * @see QueryBuilder#addEffector(Effector...)
 */
@FunctionalInterface
public interface Effector {

	/**
	 * {@link Effector} の空配列
	 */
	Effector[] EMPTY_ARRAY = {};

	/**
	 * Blendee が生成した SQL 文を、カスタマイズした SQL 文に変換します。
	 * @param sql 元になる SQL 文
	 * @return カスタマイズした SQL 文
	 */
	String effect(String sql);
}
