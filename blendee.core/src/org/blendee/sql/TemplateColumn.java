package org.blendee.sql;

import java.text.MessageFormat;

public class TemplateColumn extends Column {

	private final String template;

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

	@Override
	public int hashCode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int compareTo(Column target) {
		throw new UnsupportedOperationException();
	}
}
