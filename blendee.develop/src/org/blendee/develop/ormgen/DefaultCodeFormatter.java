package org.blendee.develop.ormgen;

import java.text.MessageFormat;

/**
 * {@link MessageFormat} を使用してコードを組み立てるデフォルトのフォーマッタです。
 *
 * @author 千葉 哲嗣
 */
public class DefaultCodeFormatter implements CodeFormatter {

	@Override
	public String formatConstants(String template, String... arguments) {
		return MessageFormat.format(template, (Object[]) arguments);
	}

	@Override
	public String formatConstantsColumnPart(String template, String... arguments) {
		return MessageFormat.format(template, (Object[]) arguments);
	}

	@Override
	public String formatConstantsRelationshipPart(String template, String... arguments) {
		return MessageFormat.format(template, (Object[]) arguments);
	}

	@Override
	public String formatEntityManager(String template, String... arguments) {
		return MessageFormat.format(template, (Object[]) arguments);
	}

	@Override
	public String formatEntity(String template, String... arguments) {
		return MessageFormat.format(template, (Object[]) arguments);
	}

	@Override
	public String formatEntityPropertyAccessorPart(String template, String... arguments) {
		return MessageFormat.format(template, (Object[]) arguments);
	}

	@Override
	public String formatEntityRelationshipPart(String template, String... arguments) {
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
