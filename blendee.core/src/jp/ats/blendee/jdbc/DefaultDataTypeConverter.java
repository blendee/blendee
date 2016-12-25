package jp.ats.blendee.jdbc;

import java.sql.Types;

import jp.ats.blendee.internal.U;

/**
 * http://java.sun.com/j2se/1.3/ja/docs/ja/guide/jdbc/spec/jdbc-spec.frame8.html
 *
 * @author 千葉 哲嗣
 */
public class DefaultDataTypeConverter implements DataTypeConverter {

	@Override
	public Class<?> convert(int type, String typeName) {
		switch (type) {
		case Types.CHAR:
			return STRING_TYPE;
		case Types.VARCHAR:
			return STRING_TYPE;
		case Types.NUMERIC:
			return BIG_DECIMAL_TYPE;
		case Types.DECIMAL:
			return BIG_DECIMAL_TYPE;
		case Types.BIT:
			return BOOLEAN_TYPE;
		case Types.TINYINT:
			return INT_TYPE;
		case Types.SMALLINT:
			return INT_TYPE;
		case Types.INTEGER:
			return INT_TYPE;
		case Types.BIGINT:
			return LONG_TYPE;
		case Types.REAL:
			return FLOAT_TYPE;
		case Types.FLOAT:
			return DOUBLE_TYPE;
		case Types.DOUBLE:
			return DOUBLE_TYPE;
		case Types.BINARY:
			return BYTE_ARRAY_TYPE;
		case Types.VARBINARY:
			return BYTE_ARRAY_TYPE;
		case Types.LONGVARBINARY:
			return BYTE_ARRAY_TYPE;
		case Types.DATE:
			return TIMESTAMP_TYPE;
		case Types.TIME:
			return TIMESTAMP_TYPE;
		case Types.TIMESTAMP:
			return TIMESTAMP_TYPE;
		case Types.OTHER:
			if ("uuid".equalsIgnoreCase(typeName))
				return UUID_TYPE;
		default:
			return OBJECT_TYPE;
		}
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
