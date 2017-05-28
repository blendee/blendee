package org.blendee.support.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Entity の setter 情報を表すアノテーションです。
 *
 * @author 千葉 哲嗣
 */
@Target({ METHOD })
@Retention(RUNTIME)
public @interface EntitySetter {

	/**
	 * カラム名
	 *
	 * @return カラム名
	 */
	String column();

	/**
	 * 型
	 *
	 * @return 型
	 */
	Class<?> type();
}
