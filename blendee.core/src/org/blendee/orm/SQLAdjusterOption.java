package org.blendee.orm;

import org.blendee.selector.Selector;
import org.blendee.sql.SQLAdjuster;

/**
 * {@link QueryOption} として {@link SQLAdjuster} を使用するためのインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface SQLAdjusterOption extends SQLAdjuster, QueryOption {

	@Override
	default void process(Selector selector) {
		selector.setSQLAdjuster(this);
	}
}
