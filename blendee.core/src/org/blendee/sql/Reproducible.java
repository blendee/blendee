package org.blendee.sql;

/**
 * @author 千葉 哲嗣
 * @param <R> 複製物
 */
public interface Reproducible<R extends Reproducible<?>> {

	/**
	 * 新しいプレースホルダの値を持つ複製を作成します。
	 * @param placeHolderValues 新しいプレースホルダの値
	 * @return 複製
	 */
	R reproduce(Object... placeHolderValues);
}
