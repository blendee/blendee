package org.blendee.orm;

import org.blendee.selector.Selector;

/**
 * {@link DataObject} 取得時に行う行ロックのオプションです。
 *
 * @author 千葉 哲嗣
 */
public enum RowLockOption implements QueryOption {

	/**
	 * 行ロックを行いません。
	 */
	NONE {

		@Override
		public void process(Selector selector) {}
	},

	/**
	 * FOR UPDATE を使用します。
	 */
	FOR_UPDATE_WAIT {

		@Override
		public void process(Selector selector) {
			selector.forUpdate(true);
			selector.nowait(false);
		}
	},

	/**
	 * FOR UPDATE NOWAIT を使用します。
	 */
	FOR_UPDATE_NOWAIT {

		@Override
		public void process(Selector selector) {
			selector.forUpdate(true);
			selector.nowait(true);
		}
	};
}
