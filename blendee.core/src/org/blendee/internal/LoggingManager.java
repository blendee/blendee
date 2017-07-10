package org.blendee.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 内部使用ユーティリティクラス
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public class LoggingManager {

	private static final String nameBase = LoggingManager.class.getCanonicalName();

	private static final Logger few = Logger.getLogger(nameBase + ".few");

	private static final Logger verbose = Logger.getLogger(nameBase + ".verbose");

	private static Logger logger;

	static {
		verbose.setLevel(Level.ALL);
		few.setLevel(Level.WARNING);
		logger = few;
	}

	public synchronized static void few() {
		logger = few;
	}

	public synchronized static void verbose() {
		logger = verbose;
	}

	public synchronized static Logger getLogger() {
		return logger;
	}
}
