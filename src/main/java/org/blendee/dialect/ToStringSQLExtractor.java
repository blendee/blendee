package org.blendee.dialect;

import org.blendee.jdbc.BConnection;
import org.blendee.jdbc.SQLExtractor;
import org.blendee.jdbc.SQLLogger;

/**
 * @author 千葉 哲嗣
 */
public class ToStringSQLExtractor implements SQLExtractor {

	@Override
	public BConnection newLoggingConnection(BConnection base, SQLLogger logger) {
		return new ToStringLoggingConnection(base, logger);
	}
}
