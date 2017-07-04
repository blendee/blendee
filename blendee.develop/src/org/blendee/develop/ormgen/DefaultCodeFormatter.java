package org.blendee.develop.ormgen;

import java.text.MessageFormat;

/**
 * {@link MessageFormat} を使用してコードを組み立てるデフォルトのフォーマッタです。
 *
 * @author 千葉 哲嗣
 */
public class DefaultCodeFormatter implements CodeFormatter {

	@Override
	public String formatRowManager(String template, String... arguments) {
		return MessageFormat.format(template, (Object[]) arguments);
	}

	@Override
	public String formatRow(String template, String... arguments) {
		return MessageFormat.format(template, (Object[]) arguments);
	}

	@Override
	public String formatRowPropertyAccessorPart(String template, String... arguments) {
		return MessageFormat.format(template, (Object[]) arguments);
	}

	@Override
	public String formatRowRelationshipPart(String template, String... arguments) {
		return MessageFormat.format(template, (Object[]) arguments);
	}

	@Override
	public String formatQuery(String template, String... arguments) {
		return MessageFormat.format(template, (Object[]) arguments);
	}

	@Override
	public String formatQueryColumnPart1(String template, String... arguments) {
		return MessageFormat.format(template, (Object[]) arguments);
	}

	@Override
	public String formatQueryColumnPart2(String template, String... arguments) {
		return MessageFormat.format(template, (Object[]) arguments);
	}

	@Override
	public String formatQueryRelationshipPart1(String template, String... arguments) {
		return MessageFormat.format(template, (Object[]) arguments);
	}

	@Override
	public String formatQueryRelationshipPart2(String template, String... arguments) {
		return MessageFormat.format(template, (Object[]) arguments);
	}

	@Override
	public String formatQueryRelationshipPart3(String template, String... arguments) {
		return MessageFormat.format(template, (Object[]) arguments);
	}
}
