package org.blendee.dialect;

import org.blendee.sql.Effector;

/**
 * SELECT 時に行う行ロックのオプションです。
 * @author 千葉 哲嗣
 */
public enum RowLockOption implements Effector {

	/**
	 * 行ロックを行いません。
	 */
	NONE {

		@Override
		public String effect(String sql) {
			return sql;
		}
	},

	/**
	 * FOR UPDATE を使用します。
	 */
	FOR_UPDATE {

		@Override
		public String effect(String sql) {
			return sql.trim() + " FOR UPDATE";
		}
	},

	/**
	 * FOR UPDATE WAIT を使用します。
	 */
	FOR_UPDATE_WAIT {

		@Override
		public String effect(String sql) {
			return sql.trim() + " FOR UPDATE WAIT";
		}
	},

	/**
	 * FOR UPDATE NOWAIT を使用します。
	 */
	FOR_UPDATE_NOWAIT {

		@Override
		public String effect(String sql) {
			return sql.trim() + " FOR UPDATE NOWAIT";
		}
	};
}
