package org.blendee.support.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 外部キーアノテーションをまとめるためのアノテーションです。
 *
 * @author 千葉 哲嗣
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface FKs {

	/**
	 * 外部キーアノテーション
	 *
	 * @return 外部キーアノテーション
	 */
	PseudoFK[] value();
}
