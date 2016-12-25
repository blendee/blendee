package jp.ats.blendee.support.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jp.ats.blendee.support.DTO;

/**
 * DTO の 他 DTO への参照を表すアノテーションです。
 *
 * @author 千葉 哲嗣
 */
@Target({ METHOD })
@Retention(RUNTIME)
public @interface DTORelationship {

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
	Class<? extends DTO> referenced();
}
