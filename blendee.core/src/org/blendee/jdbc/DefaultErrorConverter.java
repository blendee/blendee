package org.blendee.jdbc;

import java.sql.DataTruncation;
import java.sql.SQLException;

import org.blendee.internal.U;

/**
 * 独自の {@link ErrorConverter} を設定しなかった場合に使用されるデフォルトの {@link ErrorConverter} です。
 * @author 千葉 哲嗣
 */
public class DefaultErrorConverter implements ErrorConverter {

	/**
	 * {@link Throwable#printStackTrace()} を実行し、パラメータで渡された {@link SQLException} を {@link BlendeeException} でラップして返します。
	 * @throws BlendeeException {@link SQLException} をラップした例外
	 */
	@Override
	public BlendeeException convert(SQLException e) {
		BLogger logger = BlendeeManager.get().getConfigure().getLogger();

		if (e instanceof DataTruncation) {
			DataTruncation warning = (DataTruncation) e;
			String prefix = "data truncation: ";
			logger.println(prefix + "index: " + warning.getIndex());
			logger.println(prefix + "parameter: " + warning.getParameter());
			logger.println(prefix + "read: " + warning.getRead());
			logger.println(prefix + "data size: " + warning.getDataSize());
			logger.println(prefix + "transfer size: " + warning.getTransferSize());
		}

		logger.log(e);

		throw new BlendeeException(e);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
