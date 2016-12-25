package jp.ats.blendee.support.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Optional;

/**
 * DTO の getter 情報を表すアノテーションです。
 *
 * @author 千葉 哲嗣
 */
@Target({ METHOD })
@Retention(RUNTIME)
public @interface DTOGetter {

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

	/**
	 * {@link Optional} かどうか
	 *
	 * @return {@link Optional} かどうか
	 */
	boolean optional();
}
