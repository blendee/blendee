package org.blendee.support;

import org.blendee.sql.SQLDecorator;

/**
 * {@link SQLDecorator} を複数持つことができるコンテナクラスです。
 * @author 千葉 哲嗣
 */
public class SQLDecorators {

	/**
	 * 空のインスタンス
	 */
	public static final SQLDecorators EMPTY_OPTIONS = new SQLDecorators();

	private final SQLDecorator[] decorators;

	/**
	 * 短縮記述用メソッドです。<br>
	 * import static した場合、短縮して記述できます。
	 * @param decorators 複数の {@link SQLDecorator}
	 * @return instance
	 */
	public static SQLDecorators of(SQLDecorator... decorators) {
		if (decorators.length == 0) return EMPTY_OPTIONS;
		return new SQLDecorators(decorators);
	}

	/**
	 * {@link SQLDecorator} を持つインスタンスを生成します。
	 * @param decorators 複数の {@link SQLDecorator}
	 */
	public SQLDecorators(SQLDecorator... decorators) {
		this.decorators = decorators;
	}

	/**
	 * 保持する {@link SQLDecorator} を返します。
	 * @return {@link SQLDecorator} の配列
	 */
	public SQLDecorator[] get() {
		return decorators.clone();
	}
}
