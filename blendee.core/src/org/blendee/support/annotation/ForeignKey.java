package org.blendee.support.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 外部キーを表すアノテーションです。
 * @author 千葉 哲嗣
 */
@Target({ FIELD })
@Retention(RUNTIME)
public @interface ForeignKey {

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
	String[] refColumns() default {};

	/**
	 * 疑似FKか
	 * @return true の場合、 疑似FK
	 */
	boolean pseudo() default false;
}
