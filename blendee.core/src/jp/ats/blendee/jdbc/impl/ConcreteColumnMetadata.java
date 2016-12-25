package jp.ats.blendee.jdbc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.ColumnMetadata;

/**
 * @author 千葉 哲嗣
 */
class ConcreteColumnMetadata implements ColumnMetadata {

	private final String schemaName;

	private final String tableName;

	private final String name;

	private final int type;

	private final String typeName;

	private final int size;

	private final boolean hasDecimalDigits;

	private final int decimalDigits;

	private final String remarks;

	private final String defaultValue;

	private final int ordinalPosition;

	private final boolean isNotNull;

	ConcreteColumnMetadata(ResultSet result) throws SQLException {
		schemaName = result.getString("TABLE_SCHEM");
		tableName = result.getString("TABLE_NAME");
		name = result.getString("COLUMN_NAME");
		type = result.getInt("DATA_TYPE");
		typeName = result.getString("TYPE_NAME");
		size = result.getInt("COLUMN_SIZE");
		decimalDigits = result.getInt("DECIMAL_DIGITS");
		remarks = result.getString("REMARKS");
		hasDecimalDigits = !result.wasNull();
		defaultValue = result.getString("COLUMN_DEF");
		ordinalPosition = result.getInt("ORDINAL_POSITION");
		isNotNull = "NO".equals(result.getString("IS_NULLABLE"));
	}

	@Override
	public String getSchemaName() {
		return schemaName;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public String getTypeName() {
		return typeName;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public boolean hasDecimalDigits() {
		return hasDecimalDigits;
	}

	@Override
	public int getDecimalDigits() {
		return decimalDigits;
	}

	@Override
	public String getRemarks() {
		return remarks;
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public int getOrdinalPosition() {
		return ordinalPosition;
	}

	@Override
	public boolean isNotNull() {
		return isNotNull;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
