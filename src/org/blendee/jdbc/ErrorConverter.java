package org.blendee.jdbc;

import java.sql.SQLException;

/**
 * {@link SQLException} を {@link BlendeeException} 及びそのサブクラスに変換します。<br>
 * {@link SQLException} は、 {@link Exception} のサブクラスなので、 {@link RuntimeException} のサブクラスである {@link BlendeeException} に変換します。
 * @author 千葉 哲嗣
 * @see Initializer#setErrorConverterClass(Class)
 */
@FunctionalInterface
public interface ErrorConverter {

	/**
	 * {@link SQLException} を {@link BlendeeException} 及びそのサブクラスに変換します。
	 * @param e Blendee 内で発生した {@link SQLException}
	 * @return 変換後の例外
	 */
	BlendeeException convert(SQLException e);

	/**
	 * ラップされている {@link SQLException} を解除します。
	 * @param e
	 * @return cause
	 */
	static SQLException strip(SQLException e) {
		Throwable cause = e.getCause();
		if (cause == null) return e;
		if (cause instanceof SQLException) return strip((SQLException) cause);
		return e;
	}
}
