package org.blendee.support.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Row のもととなったテーブルを表すアノテーションです。
 *
 * @author 千葉 哲嗣
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface Resource {

	/**
	 * スキーマ名
	 *
	 * @return スキーマ名
	 */
	String schema();

	/**
	 * テーブル名
	 *
	 * @return テーブル名
	 */
	String table();
}
