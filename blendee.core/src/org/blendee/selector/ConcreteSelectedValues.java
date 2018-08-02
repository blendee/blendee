package org.blendee.selector;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.blendee.internal.LoggingManager;
import org.blendee.internal.U;
import org.blendee.jdbc.Result;
import org.blendee.sql.Binder;
import org.blendee.sql.Column;
import org.blendee.sql.Relationship;
import org.blendee.sql.ValueExtractor;
import org.blendee.sql.ValueExtractors;
import org.blendee.sql.binder.NullBinder;

/**
 * @author 千葉 哲嗣
 */
class ConcreteSelectedValues implements SelectedValues {

	private final Map<Column, Object> values = new HashMap<>();

	private final Map<Column, NullBinder> nullValues = new HashMap<>();

	private final ValueExtractors extractors;

	ConcreteSelectedValues(
		Result result,
		Column[] selected,
		ValueExtractors extractors) {
		this.extractors = extractors;
		for (int i = 0; i < selected.length; i++) {
			Column column = selected[i];
			ValueExtractor extractor = extractors.selectValueExtractor(column.getType());
			values.put(column, extractor.extract(result, i + 1));
			if (result.wasNull()) nullValues.put(column, new NullBinder(result.getColumnType(i + 1)));
		}
	}

	ConcreteSelectedValues(
		Result result,
		Relationship relation,
		ValueExtractors extractors) {
		this.extractors = extractors;

		Map<String, Integer> columnMap = new LinkedHashMap<>();
		int columnCount = result.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			int index = i + 1;
			columnMap.put(result.getColumnName(index), index);
		}

		Arrays.stream(relation.getColumns())
			.filter(c -> columnMap.containsKey(c.getName()))
			.forEach(c -> {
				ValueExtractor extractor = extractors.selectValueExtractor(c.getType());
				int index = columnMap.get(c.getName());
				values.put(c, extractor.extract(result, index));
				if (result.wasNull()) nullValues.put(c, new NullBinder(result.getColumnType(index)));
			});
	}

	@Override
	public boolean getBoolean(Column column) {
		Boolean value = (Boolean) getObject(column);
		return value != null ? value.booleanValue() : false;
	}

	@Override
	public double getDouble(Column column) {
		Number value = (Number) getObject(column);
		return value != null ? value.doubleValue() : 0;
	}

	@Override
	public float getFloat(Column column) {
		Number value = (Number) getObject(column);
		return value != null ? value.floatValue() : 0;
	}

	@Override
	public int getInt(Column column) {
		Number value = (Number) getObject(column);
		return value != null ? value.intValue() : 0;
	}

	@Override
	public long getLong(Column column) {
		Number value = (Number) getObject(column);
		return value != null ? value.longValue() : 0;
	}

	@Override
	public String getString(Column column) {
		Object value = getObject(column);
		if (value != null) return value.toString();
		return null;
	}

	@Override
	public Timestamp getTimestamp(Column column) {
		return (Timestamp) getObject(column);
	}

	@Override
	public BigDecimal getBigDecimal(Column column) {
		return (BigDecimal) getObject(column);
	}

	@Override
	public UUID getUUID(Column column) {
		return (UUID) getObject(column);
	}

	@Override
	public byte[] getBytes(Column column) {
		return (byte[]) getObject(column);
	}

	@Override
	public Blob getBlob(Column column) {
		return (Blob) getObject(column);
	}

	@Override
	public Clob getClob(Column column) {
		return (Clob) getObject(column);
	}

	@Override
	public Binder getBinder(Column column) {
		NullBinder nullBinder = nullValues.get(column);
		if (nullBinder != null) return nullBinder;

		Object value = getObject(column);
		return extractors.selectValueExtractor(value.getClass()).extractAsBinder(value);
	}

	@Override
	public Object getObject(Column column) {
		checkContains(column);
		return values.get(column);
	}

	@Override
	public boolean isNull(Column column) {
		checkContains(column);
		return nullValues.containsKey(column);
	}

	@Override
	public Column[] getSelectedColumns() {
		return values.keySet().toArray(new Column[values.size()]);
	}

	@Override
	public boolean isSelected(Column column) {
		return values.containsKey(column);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private void checkContains(Column column) {
		if (!values.containsKey(column)) {
			String message = IllegalValueException.buildMessage(column);
			LoggingManager.getLogger().warning(message);
			throw new IllegalValueException(message);
		}
	}
}
