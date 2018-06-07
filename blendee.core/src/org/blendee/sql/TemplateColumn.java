package org.blendee.sql;

import java.text.MessageFormat;

/**
 * テンプレートによりカラム表現を拡張したクラスです。
 * @author 千葉 哲嗣
 */
public class TemplateColumn extends RelationshipColumn {

	private final String template;

	/**
	 * コンストラクタです。
	 * @param template テンプレート
	 * @param base テンプレートに埋め込むカラム
	 */
	public TemplateColumn(String template, Column base) {
		super(base);
		this.template = template;
	}

	@Override
	public String getName() {
		return MessageFormat.format(template, super.getName());
	}

	@Override
	public String getComplementedName() {
		return MessageFormat.format(template, super.getComplementedName());
	}

	/**
	 * {@link Column} と混在させないために、使用できません。
	 */
	@Override
	public int hashCode() {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@link Column} と混在させないために、使用できません。
	 */
	@Override
	public boolean equals(Object o) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@link Column} と混在させないために、使用できません。
	 */
	@Override
	public int compareTo(Column target) {
		throw new UnsupportedOperationException();
	}
}
