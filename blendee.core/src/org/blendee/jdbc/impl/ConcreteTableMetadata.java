package org.blendee.jdbc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.blendee.jdbc.CrossReference;
import org.blendee.jdbc.TableMetadata;

/**
 * {@link CrossReference} の簡易実装クラスです。
 *
 * @author 千葉 哲嗣
 */
class ConcreteTableMetadata implements TableMetadata {

	private final String schemaName;

	private final String name;

	private final String type;

	private final String remarks;

	ConcreteTableMetadata(ResultSet result) throws SQLException {
		schemaName = result.getString("TABLE_SCHEM");
		name = result.getString("TABLE_NAME");
		type = result.getString("TABLE_TYPE");
		remarks = result.getString("REMARKS");
	}

	@Override
	public String getSchemaName() {
		return schemaName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getRemarks() {
		return remarks;
	}
}
