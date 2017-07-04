package org.blendee.support.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.blendee.support.Row;

/**
 * Row の 他 Row への参照を表すアノテーションです。
 *
 * @author 千葉 哲嗣
 */
@Target({ METHOD })
@Retention(RUNTIME)
public @interface RowRelationship {

	/**
	 * FK 名
	 *
	 * @return FK 名
	 */
	String fk();

	/**
	 * 参照テーブル
	 *
	 * @return 参照テーブル
	 */
	Class<? extends Row> referenced();
}
