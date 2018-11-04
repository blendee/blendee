package org.blendee.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.UUID;

/**
 * {@link Types} で定義されているタイプ値を Blendee で使用する型に変換する機能を提供します。
 * @author 千葉 哲嗣
 */
public interface DataTypeConverter {

	/**
	 * {@link #convert(int, String)} で返すことが推奨される値です。
	 * {@link BigDecimal} のクラスオブジェクト
	 */
	static final Class<BigDecimal> BIG_DECIMAL_TYPE = BigDecimal.class;

	/**
	 * {@link #convert(int, String)} で返すことが推奨される値です。
	 * {@link InputStream} のクラスオブジェクト
	 */
	static final Class<InputStream> BINARY_STREAM_TYPE = InputStream.class;

	/**
	 * {@link #convert(int, String)} で返すことが推奨される値です。
	 * {@link Blob} のクラスオブジェクト
	 */
	static final Class<Blob> BLOB_TYPE = Blob.class;

	/**
	 * {@link #convert(int, String)} で返すことが推奨される値です。
	 * {@link Boolean#TYPE}
	 */
	static final Class<Boolean> BOOLEAN_TYPE = Boolean.TYPE;

	/**
	 * {@link #convert(int, String)} で返すことが推奨される値です。
	 * byte[] のクラスオブジェクト
	 */
	static final Class<byte[]> BYTE_ARRAY_TYPE = byte[].class;

	/**
	 * {@link #convert(int, String)} で返すことが推奨される値です。
	 * {@link Reader} のクラスオブジェクト
	 */
	static final Class<Reader> CHARACTER_STREAM_TYPE = Reader.class;

	/**
	 * {@link #convert(int, String)} で返すことが推奨される値です。
	 * {@link Clob} のクラスオブジェクト
	 */
	static final Class<Clob> CLOB_TYPE = Clob.class;

	/**
	 * {@link #convert(int, String)} で返すことが推奨される値です。
	 * {@link Double#TYPE}
	 */
	static final Class<Double> DOUBLE_TYPE = Double.TYPE;

	/**
	 * {@link #convert(int, String)} で返すことが推奨される値です。
	 * {@link Float#TYPE}
	 */
	static final Class<Float> FLOAT_TYPE = Float.TYPE;

	/**
	 * {@link #convert(int, String)} で返すことが推奨される値です。
	 * {@link Integer#TYPE}
	 */
	static final Class<Integer> INT_TYPE = Integer.TYPE;

	/**
	 * {@link #convert(int, String)} で返すことが推奨される値です。
	 * {@link Long#TYPE}
	 */
	static final Class<Long> LONG_TYPE = Long.TYPE;

	/**
	 * {@link #convert(int, String)} で返すことが推奨される値です。
	 * {@link Object} のクラスオブジェクト
	 */
	static final Class<Object> OBJECT_TYPE = Object.class;

	/**
	 * {@link #convert(int, String)} で返すことが推奨される値です。
	 * {@link String} のクラスオブジェクト
	 */
	static final Class<String> STRING_TYPE = String.class;

	/**
	 * {@link #convert(int, String)} で返すことが推奨される値です。
	 * {@link Timestamp} のクラスオブジェクト
	 */
	static final Class<Timestamp> TIMESTAMP_TYPE = Timestamp.class;

	/**
	 * {@link #convert(int, String)} で返すことが推奨される値です。
	 * {@link UUID} のクラスオブジェクト
	 */
	static final Class<UUID> UUID_TYPE = UUID.class;

	/**
	 * {@link Types} で定義されているタイプ値を Blendee で使用する型に変換します。
	 * @param type {@link Types} で定義されているタイプ値
	 * @param typeName データベースでの型名
	 * @return Blendee で使用する型
	 */
	Class<?> convert(int type, String typeName);
}
