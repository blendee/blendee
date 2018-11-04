package org.blendee.support.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 主キーを表すアノテーションです。
 * @author 千葉 哲嗣
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface PrimaryKey {

	/**
	 * 主キー名
	 * @return 主キー名
	 */
	String name();

	/**
	 * 主キーを構成するカラム
	 * @return 主キーを構成するカラム
	 */
	String[] columns();

	/**
	 * 疑似PKか
	 * @return true の場合、 疑似PK
	 */
	boolean pseudo() default false;
}
