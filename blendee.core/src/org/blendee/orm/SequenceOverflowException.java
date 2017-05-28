package org.blendee.orm;

import org.blendee.jdbc.BlendeeException;
import org.blendee.jdbc.ResourceLocator;
import org.blendee.sql.Condition;
import org.blendee.sql.SQLAdjuster;
import org.blendee.sql.Updatable;

/**
 * 連続値の最大を超えた場合にスローされる例外です。
 *
 * @author 千葉 哲嗣
 * @see SequenceGenerator#next(Condition)
 * @see DataAccessHelper#insert(ResourceLocator, SequenceGenerator, Updatable, int, SQLAdjuster)
 */
public class SequenceOverflowException extends BlendeeException {

	private static final long serialVersionUID = 3904838153376025915L;

	/**
	 * エラーメッセージとして最大値を超えた項目名を持つインスタンスを生成します。
	 *
	 * @param column 最大値を超えた項目名
	 */
	public SequenceOverflowException(String column) {
		super(column);
	}
}
