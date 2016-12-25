package jp.ats.blendee.selector;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.DataTypeConverter;
import jp.ats.blendee.jdbc.BResult;
import jp.ats.blendee.sql.Binder;
import jp.ats.blendee.sql.binder.BigDecimalBinder;
import jp.ats.blendee.sql.binder.BlobBinder;
import jp.ats.blendee.sql.binder.BooleanBinder;
import jp.ats.blendee.sql.binder.ByteArrayBinder;
import jp.ats.blendee.sql.binder.ClobBinder;
import jp.ats.blendee.sql.binder.DoubleBinder;
import jp.ats.blendee.sql.binder.FloatBinder;
import jp.ats.blendee.sql.binder.IntBinder;
import jp.ats.blendee.sql.binder.LongBinder;
import jp.ats.blendee.sql.binder.ObjectBinder;
import jp.ats.blendee.sql.binder.StringBinder;
import jp.ats.blendee.sql.binder.TimestampBinder;
import jp.ats.blendee.sql.binder.UUIDBinder;

/**
 * Blendee のデフォルト {@link ValueExtractors} です。
 *
 * @author 千葉 哲嗣
 */
public class DefaultValueExtractors implements ValueExtractors {

	private final Map<Class<?>, ValueExtractor> map = new HashMap<>();

	/**
	 * このクラスのインスタンスを生成します。
	 */
	public DefaultValueExtractors() {
		map.put(DataTypeConverter.BIG_DECIMAL_TYPE, BigDecimalValueExtractor.singleton);

		map.put(DataTypeConverter.BINARY_STREAM_TYPE, BinaryStreamValueExtractor.singleton);

		map.put(DataTypeConverter.BLOB_TYPE, BlobValueExtractor.singleton);

		map.put(DataTypeConverter.BOOLEAN_TYPE, BooleanValueExtractor.singleton);
		map.put(Boolean.class, BooleanValueExtractor.singleton);

		map.put(DataTypeConverter.BYTE_ARRAY_TYPE, ByteArrayValueExtractor.singleton);

		map.put(DataTypeConverter.CHARACTER_STREAM_TYPE, CharacterStreamValueExtractor.singleton);

		map.put(DataTypeConverter.CLOB_TYPE, ClobValueExtractor.singleton);

		map.put(DataTypeConverter.DOUBLE_TYPE, DoubleValueExtractor.singleton);
		map.put(Double.class, DoubleValueExtractor.singleton);

		map.put(DataTypeConverter.FLOAT_TYPE, FloatValueExtractor.singleton);
		map.put(Float.class, FloatValueExtractor.singleton);

		map.put(DataTypeConverter.INT_TYPE, IntValueExtractor.singleton);
		map.put(Integer.class, IntValueExtractor.singleton);

		map.put(DataTypeConverter.LONG_TYPE, LongValueExtractor.singleton);
		map.put(Long.class, LongValueExtractor.singleton);

		map.put(DataTypeConverter.OBJECT_TYPE, ObjectValueExtractor.singleton);

		map.put(DataTypeConverter.STRING_TYPE, StringValueExtractor.singleton);

		map.put(DataTypeConverter.TIMESTAMP_TYPE, TimestampValueExtractor.singleton);

		map.put(DataTypeConverter.UUID_TYPE, UUIDValueExtractor.singleton);
	}

	@Override
	public ValueExtractor selectValueExtractor(Class<?> valueClass) {
		ValueExtractor extractor = map.get(valueClass);
		if (extractor == null) return ObjectValueExtractor.singleton;
		return extractor;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private static class BigDecimalValueExtractor implements ValueExtractor {

		private static final BigDecimalValueExtractor singleton = new BigDecimalValueExtractor();

		private BigDecimalValueExtractor() {}

		@Override
		public Object extract(BResult result, int columnIndex) {
			return result.getBigDecimal(columnIndex);
		}

		@Override
		public Binder extractAsBinder(Object value) {
			return new BigDecimalBinder((BigDecimal) value);
		}
	}

	private static class BinaryStreamValueExtractor implements ValueExtractor {

		private static final BinaryStreamValueExtractor singleton = new BinaryStreamValueExtractor();

		private BinaryStreamValueExtractor() {}

		@Override
		public Object extract(BResult result, int columnIndex) {
			return result.getBinaryStream(columnIndex);
		}

		@Override
		public Binder extractAsBinder(Object value) {
			throw new UnsupportedOperationException();
		}
	}

	private static class BlobValueExtractor implements ValueExtractor {

		private static final BlobValueExtractor singleton = new BlobValueExtractor();

		private BlobValueExtractor() {}

		@Override
		public Object extract(BResult result, int columnIndex) {
			return result.getBlob(columnIndex);
		}

		@Override
		public Binder extractAsBinder(Object value) {
			return new BlobBinder((Blob) value);
		}
	}

	private static class BooleanValueExtractor implements ValueExtractor {

		private static final BooleanValueExtractor singleton = new BooleanValueExtractor();

		private BooleanValueExtractor() {}

		@Override
		public Object extract(BResult result, int columnIndex) {
			return Boolean.valueOf(result.getBoolean(columnIndex));
		}

		@Override
		public Binder extractAsBinder(Object value) {
			return new BooleanBinder(((Boolean) value).booleanValue());
		}
	}

	private static class ByteArrayValueExtractor implements ValueExtractor {

		private static final ByteArrayValueExtractor singleton = new ByteArrayValueExtractor();

		private ByteArrayValueExtractor() {}

		@Override
		public Object extract(BResult result, int columnIndex) {
			return result.getBytes(columnIndex);
		}

		@Override
		public Binder extractAsBinder(Object value) {
			return new ByteArrayBinder((byte[]) value);
		}
	}

	private static class CharacterStreamValueExtractor implements ValueExtractor {

		private static final CharacterStreamValueExtractor singleton = new CharacterStreamValueExtractor();

		private CharacterStreamValueExtractor() {}

		@Override
		public Object extract(BResult result, int columnIndex) {
			return result.getCharacterStream(columnIndex);
		}

		@Override
		public Binder extractAsBinder(Object value) {
			throw new UnsupportedOperationException();
		}
	}

	private static class ClobValueExtractor implements ValueExtractor {

		private static final ClobValueExtractor singleton = new ClobValueExtractor();

		private ClobValueExtractor() {}

		@Override
		public Object extract(BResult result, int columnIndex) {
			return result.getClob(columnIndex);
		}

		@Override
		public Binder extractAsBinder(Object value) {
			return new ClobBinder((Clob) value);
		}
	}

	private static class DoubleValueExtractor implements ValueExtractor {

		private static final DoubleValueExtractor singleton = new DoubleValueExtractor();

		private DoubleValueExtractor() {}

		@Override
		public Object extract(BResult result, int columnIndex) {
			return Double.valueOf(result.getDouble(columnIndex));
		}

		@Override
		public Binder extractAsBinder(Object value) {
			return new DoubleBinder(((Double) value).doubleValue());
		}
	}

	private static class FloatValueExtractor implements ValueExtractor {

		private static final FloatValueExtractor singleton = new FloatValueExtractor();

		private FloatValueExtractor() {}

		@Override
		public Object extract(BResult result, int columnIndex) {
			return Float.valueOf(result.getFloat(columnIndex));
		}

		@Override
		public Binder extractAsBinder(Object value) {
			return new FloatBinder(((Float) value).floatValue());
		}
	}

	private static class IntValueExtractor implements ValueExtractor {

		private static final IntValueExtractor singleton = new IntValueExtractor();

		private IntValueExtractor() {}

		@Override
		public Object extract(BResult result, int columnIndex) {
			return Integer.valueOf(result.getInt(columnIndex));
		}

		@Override
		public Binder extractAsBinder(Object value) {
			return new IntBinder(((Integer) value).intValue());
		}
	}

	private static class LongValueExtractor implements ValueExtractor {

		private static final LongValueExtractor singleton = new LongValueExtractor();

		private LongValueExtractor() {}

		@Override
		public Object extract(BResult result, int columnIndex) {
			return Long.valueOf(result.getLong(columnIndex));
		}

		@Override
		public Binder extractAsBinder(Object value) {
			return new LongBinder(((Long) value).longValue());
		}
	}

	private static class ObjectValueExtractor implements ValueExtractor {

		private static final ObjectValueExtractor singleton = new ObjectValueExtractor();

		private ObjectValueExtractor() {}

		@Override
		public Object extract(BResult result, int columnIndex) {
			return result.getObject(columnIndex);
		}

		@Override
		public Binder extractAsBinder(Object value) {
			return new ObjectBinder(value);
		}
	}

	private static class StringValueExtractor implements ValueExtractor {

		private static final StringValueExtractor singleton = new StringValueExtractor();

		private StringValueExtractor() {}

		@Override
		public Object extract(BResult result, int columnIndex) {
			return result.getString(columnIndex);
		}

		@Override
		public Binder extractAsBinder(Object value) {
			return new StringBinder((String) value);
		}
	}

	private static class TimestampValueExtractor implements ValueExtractor {

		private static final TimestampValueExtractor singleton = new TimestampValueExtractor();

		private TimestampValueExtractor() {}

		@Override
		public Object extract(BResult result, int columnIndex) {
			return result.getTimestamp(columnIndex);
		}

		@Override
		public Binder extractAsBinder(Object value) {
			return new TimestampBinder((Timestamp) value);
		}
	}

	private static class UUIDValueExtractor implements ValueExtractor {

		private static final UUIDValueExtractor singleton = new UUIDValueExtractor();

		private UUIDValueExtractor() {}

		@Override
		public Object extract(BResult result, int columnIndex) {
			return result.getObject(columnIndex);
		}

		@Override
		public Binder extractAsBinder(Object value) {
			return new UUIDBinder((UUID) value);
		}
	}
}
