package org.blendee.support.annotation;

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
	Class<?> type();

	/**
	 * NOT NULL 制約があるか
	 * @return NOT NULL の場合、 true
	 */
	boolean notNull();
}
