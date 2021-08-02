package org.blendee.sql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 一部分の SQL 文の中から、文字定数部とその他の部分を分離するクラスです。
 * @author 千葉 哲嗣
 */
public class SQLFragmentUtilities {

	private static final Pattern outerPattern = Pattern.compile("^([^']*)'");

	private static final Pattern innerPattern = Pattern.compile("^([^']*)(''?)");

	private SQLFragmentUtilities() {
	}

	/**
	 * SQL 文を解析し、結果を {@link SQLFragmentListener} に通知します。
	 * @param sqlFragment SQL 文
	 * @param listener 通知されるリスナ
	 */
	public static void traverseSQLFragment(String sqlFragment, SQLFragmentListener listener) {
		if (!hasStringConstants(sqlFragment)) {
			listener.receiveSQLFragment(sqlFragment);

			return;
		}

		for (Matcher outerMatcher = outerPattern.matcher(sqlFragment); outerMatcher
			.find(); outerMatcher = outerPattern.matcher(sqlFragment)) {
			String matched = outerMatcher.group(1);
			if (matched.length() > 0)
				listener.receiveSQLFragment(matched);

			listener.receiveQuote("'");

			sqlFragment = sqlFragment.substring(outerMatcher.end());

			StringBuilder constants = new StringBuilder();

			boolean terminated = false;

			for (Matcher innerMatcher = innerPattern.matcher(sqlFragment); innerMatcher.find(); innerMatcher = innerPattern.matcher(sqlFragment)) {
				sqlFragment = sqlFragment.substring(innerMatcher.end());

				if (innerMatcher.group(2).length() == 1) {
					String matchedConstants = innerMatcher.group(1);

					constants.append(matchedConstants);

					listener.receiveStringConstants(constants.toString());
					listener.receiveQuote("'");

					terminated = true;

					break;
				}

				constants.append(innerMatcher.group());
			}

			//文字定数が終了していません
			if (!terminated) throw new IllegalStateException("\"'\" not found");
		}

		if (sqlFragment.length() > 0) listener.receiveSQLFragment(sqlFragment);
	}

	private static boolean hasStringConstants(String sqlFragment) {
		return sqlFragment.indexOf('\'') != -1;
	}
}
