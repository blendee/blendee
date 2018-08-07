package org.blendee.support;

/**
 * JOIN の対象を表すインターフェイスです。
 * @author 千葉 哲嗣
 * @param <R> joint の型
 */
public interface RightQuery<R extends OnRightQueryRelationship> {

	/**
	 * JOIN の右側を構成する {@link OnRightQueryRelationship} を返します。
	 * @return joint
	 */
	R joint();
}
