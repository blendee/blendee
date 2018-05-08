package org.blendee.support.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 実在しない外部キーをテーブル（ビュー）に付与するためのアノテーションです。
 * @author 千葉 哲嗣
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface PseudoFK {

	/**
	 * 外部キー名
	 * @return 外部キー名
	 */
	String name();

	/**
	 * 参照先
	 * @return 参照先
	 */
	String references();

	/**
	 * 外部キー（参照側）を構成するカラム
	 * @return 外部キーを構成するカラム
	 */
	String[] columns();

	/**
	 * 主キー（参照される側）を構成するカラム<br>
	 * 省略された場合、 PK を構成するカラムが使用されます。
	 * @return 主キーを構成するカラム
	 */
	String[] referredColumns() default {};
}
