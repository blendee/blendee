package org.blendee.develop.ormgen;

import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link MessageFormat} を使用してコードを組み立てるデフォルトのフォーマッタです。
 * @author 千葉 哲嗣
 */
class Formatter {

	private static final Pattern pattern = Pattern.compile(
		"\\[\\[([^\\]]+)\\]\\]",
		Pattern.MULTILINE + Pattern.DOTALL);

	static String format(String template, Map<String, String> arguments) {
		StringBuilder buffer = new StringBuilder();

		Matcher matcher = pattern.matcher(template);

		int start = 0;
		while (matcher.find()) {
			buffer.append(template.substring(start, matcher.start()));
			buffer.append(arguments.get(matcher.group(1)));
			start = matcher.end();
		}

		buffer.append(template.substring(start));

		return buffer.toString();
	}
}
