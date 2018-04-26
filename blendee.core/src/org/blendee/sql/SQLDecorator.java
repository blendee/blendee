package org.blendee.sql;

/**
 * SQL 文を各データベース向けに微調整したり拡張する機能を定義したインターフェイスです。
 * @author 千葉 哲嗣
 * @see QueryBuilder#addDecorator(SQLDecorator...)
 */
@FunctionalInterface
public interface SQLDecorator {

	/**
	 * {@link SQLDecorator} の空配列
	 */
	SQLDecorator[] EMPTY_ARRAY = {};

	/**
	 * Blendee が生成した SQL 文を、カスタマイズした SQL 文に変換します。
	 * @param sql 元になる SQL 文
	 * @return カスタマイズした SQL 文
	 */
	String decorate(String sql);
}
