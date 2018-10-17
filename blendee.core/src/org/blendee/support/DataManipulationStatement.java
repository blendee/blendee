package org.blendee.support;

import org.blendee.sql.Column;

/**
 * INSERT, UPDATE, DELETE 文を生成するための機能を持つものを表すインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface DataManipulationStatement extends Statement {

	/**
	 * INSERT 文に加える {@link Column} を追加します。
	 * @param column column
	 */
	void addInsertColumns(Column column);

	/**
	 * SET 文に加える {@link SetElement} を追加します。
	 * @param element element
	 */
	void addSetElement(SetElement element);
}
