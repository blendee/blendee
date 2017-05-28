package org.blendee.sql;

import java.text.MessageFormat;

/**
 * SQL 文の文字定数以外の部分に対して、 {@link MessageFormat} の仕様に従った変換を行います。
 *
 * @author 千葉 哲嗣
 * @see MessageFormat
 */
public class SQLFragmentFormat {

	private SQLFragmentFormat() {}

	/**
	 * 変換を実行します。
	 *
	 * @param template {@link MessageFormat} の仕様に従ったテンプレート
	 * @param arguments 埋め込む値
	 * @return 変換された文字列
	 */
	public static String execute(final String template, final String[] arguments) {
		final StringBuilder builder = new StringBuilder();

		SQLFragmentUtilities.traverseSQLFragment(template, new SQLFragmentListener() {

			@Override
			public void receiveSQLFragment(String fragment) {
				builder.append(MessageFormat.format(fragment, (Object[]) arguments));
			}

			@Override
			public void receiveQuote(String quote) {
				builder.append(quote);
			}

			@Override
			public void receiveStringConstants(String constants) {
				builder.append(constants);
			}
		});

		return builder.toString();
	}
}
