package org.blendee.support;

import org.blendee.sql.Column;

/**
 * WHERE 句に新しい条件を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 * @param <O> 論理演算用 {@link LogicalOperators}
 */
public abstract class OnColumn<O extends LogicalOperators<?>> extends CriteriaColumn<O> {

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 */
	@SuppressWarnings("javadoc")
	protected OnColumn(CriteriaContext context, Column column) {
		super(context, column);
	}
}