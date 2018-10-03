package org.blendee.sql;

import java.util.Objects;

import org.blendee.jdbc.TablePath;

public class AliasTablePath extends TablePath {

	private final String alias;

	public AliasTablePath(TablePath table, String alias) {
		super(table.getSchemaName(), table.getTableName());
		Objects.requireNonNull(alias);
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}
}
