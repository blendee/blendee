package jp.ats.blendee.jdbc;

import java.sql.SQLException;

/**
 * {@link SQLException} を {@link BlendeeException} 及びそのサブクラスに変換します。
 * <br>
 * {@link SQLException} は、 {@link Exception} のサブクラスなので、 {@link RuntimeException} のサブクラスである {@link BlendeeException} に変換します。
 *
 * @author 千葉 哲嗣
 * @see Initializer#setErrorConverterClass(Class)
 */
@FunctionalInterface
public interface ErrorConverter {

	/**
	 * {@link SQLException} を {@link BlendeeException} 及びそのサブクラスに変換します。
	 *
	 * @param e Blendee 内で発生した {@link SQLException}
	 * @return 変換後の例外
	 */
	BlendeeException convert(SQLException e);
}
