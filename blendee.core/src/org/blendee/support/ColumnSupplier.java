package org.blendee.support;

import org.blendee.sql.Column;

/**
 * {@link Column} を内部に保持するものを表すインターフェイスです。
 * @author 千葉 哲嗣
 */
@FunctionalInterface
public interface ColumnSupplier {

	/**
	 * @return {@link Column}
	 */
	Column column();
}
