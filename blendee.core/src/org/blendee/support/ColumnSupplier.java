package org.blendee.support;

import org.blendee.sql.Column;
import org.blendee.sql.RuntimeId;

/**
 * {@link Column} を内部に保持するものを表すインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface ColumnSupplier {

	/**
	 * @return {@link Column}
	 */
	Column column();

	@SuppressWarnings("javadoc")
	RuntimeId queryId();
}
