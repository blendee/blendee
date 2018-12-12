package org.blendee.assist;

import org.blendee.sql.SQLDecorator;

/**
 * {@link SQLDecorator} を管理します。
 * @author 千葉 哲嗣
 */
public interface SQLDecorators {

	/**
	 * 生成された SQL 文を加工する {SQLDecorator} を設定します。
	 * @param decorators {@link SQLDecorator}
	 * @return {@link SelectStatement} 自身
	 */
	SelectStatement apply(SQLDecorator... decorators);

	/**
	 * 内部で保持している {@link SQLDecorator} を返します。
	 * @return {@link SQLDecorator}
	 */
	SQLDecorator[] decorators();
}
