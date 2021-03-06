package org.blendee.assist.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * テーブルを表すアノテーションです。
 * @author 千葉 哲嗣
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface Table {

	/**
	 * テーブル名
	 * @return テーブル名
	 */
	String name();

	/**
	 * スキーマ名
	 * @return スキーマ名
	 */
	String schema();

	/**
	 * テーブルの型
	 * @return テーブルの型
	 */
	String type();

	/**
	 * 備考
	 * @return 備考
	 */
	String remarks();
}
