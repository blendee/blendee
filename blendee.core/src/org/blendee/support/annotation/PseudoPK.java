package org.blendee.support.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 実在しない主キーをテーブル（ビュー）に付与するためのアノテーションです。
 * @author 千葉 哲嗣
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface PseudoPK {

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
}
