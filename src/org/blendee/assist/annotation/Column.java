package org.blendee.assist.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * カラムを表すアノテーションです。
 * @author 千葉 哲嗣
 */
@Target({ FIELD })
@Retention(RUNTIME)
public @interface Column {

	/**
	 * カラム名
	 * @return カラム名
	 */
	String name();

	/**
	 * 型
	 * @return 型
	 */
	int type();

	/**
	 * データソース依存の型名
	 * @return データソース依存の型名
	 */
	String typeName();

	/**
	 * カラムサイズ
	 * @return カラムサイズ
	 */
	int size();

	/**
	 * 小数点以下の桁数を持つか
	 * @return 小数点以下の桁数を持つ場合、 true
	 */
	boolean hasDecimalDigits() default false;

	/**
	 * 小数点以下の桁数
	 * @return 小数点以下の桁数
	 */
	int decimalDigits() default 0;

	/**
	 * 備考
	 * @return 備考
	 */
	String remarks();

	/**
	 * デフォルト値
	 * @return デフォルト値
	 */
	String defaultValue();

	/**
	 * テーブル内の位置
	 * @return テーブル内の位置 (1 から始まる )
	 */
	int ordinalPosition();

	/**
	 * NOT NULL 制約があるか
	 * @return NOT NULL の場合、 true
	 */
	boolean notNull();
}
