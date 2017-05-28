package org.blendee.orm;

import java.util.Objects;

import org.blendee.selector.Selector;
import org.blendee.sql.SQLAdjuster;

/**
 * {@link SQLAdjuster} を使用するためのラッパークラス です。
 *
 * @author 千葉 哲嗣
 */
public class SQLAdjusterOption implements QueryOption {

	private final SQLAdjuster adjuster;

	/**
	 * インスタンスを生成します。
	 *
	 * @param adjuster {@link SQLAdjuster}
	 */
	public SQLAdjusterOption(SQLAdjuster adjuster) {
		this.adjuster = Objects.requireNonNull(adjuster);
	}

	@Override
	public void process(Selector selector) {
		selector.setSQLAdjuster(adjuster);
	}
}
