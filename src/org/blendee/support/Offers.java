package org.blendee.support;

import java.util.List;

/**
 * 対象を束ねるクラスです。
 * @author 千葉 哲嗣
 * @param <T> 対象
 */
@FunctionalInterface
public interface Offers<T> {

	/**
	 * 内部処理用なので直接使用しないこと。
	 * @return {@link SelectOfferFunction} で設定された対象
	 */
	List<T> get();
}
