package org.blendee.util;

import org.blendee.internal.TransactionManager;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.impl.SimpleContextStrategy;
import org.blendee.orm.DataAccessHelper;

/**
 * Blendee 内部で使用している {@link ThreadLocal} を全て削除するクラスです。
 * @author 千葉 哲嗣
 *
 */
class ThreadLocalSweeper {

	static void execute() {
		TransactionManager.removeThreadLocal();
		BlendeeManager.removeThreadLocal();
		ContextManager.removeThreadLocal();
		SimpleContextStrategy.removeThreadLocal();
		DataAccessHelper.removeThreadLocal();
	}
}
