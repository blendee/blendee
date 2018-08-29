package org.blendee.util;

import static org.blendee.util.ParsableOptionKey.OptionValueParser.TO_BOOLEAN;
import static org.blendee.util.ParsableOptionKey.OptionValueParser.TO_CLASS;
import static org.blendee.util.ParsableOptionKey.OptionValueParser.TO_INTEGER;
import static org.blendee.util.ParsableOptionKey.OptionValueParser.TO_STRING;
import static org.blendee.util.ParsableOptionKey.OptionValueParser.TO_STRING_ARRAY;

import org.blendee.jdbc.DefaultErrorConverter;
import org.blendee.jdbc.ErrorConverter;
import org.blendee.jdbc.Initializer;
import org.blendee.jdbc.MetadataFactory;
import org.blendee.jdbc.TransactionFactory;
import org.blendee.selector.AnchorOptimizerFactory;
import org.blendee.selector.ColumnRepositoryFactory;
import org.blendee.sql.DefaultValueExtractors;
import org.blendee.sql.ValueExtractors;
import org.blendee.sql.ValueExtractorsConfigure;

/**
 * Blendee の起動に必要となる情報のキーを定めた定数インターフェイスです。
 */
public interface BlendeeConstants {

	/**
	 * required: true
	 * (String[]) SCHEMA_NAMES
	 * @see Initializer#addSchemaName(String)
	 */
	public static final ParsableOptionKey<String[]> SCHEMA_NAMES = new ParsableOptionKey<>(
		"schema-names",
		TO_STRING_ARRAY);

	/**
	 * default: false
	 * (Boolean) ENABLE_LOG
	 * @see Initializer#enableLog(boolean)
	 */
	public static final ParsableOptionKey<Boolean> ENABLE_LOG = new ParsableOptionKey<>("enable-log", TO_BOOLEAN);

	/**
	 * default: false
	 * (Boolean) USE_AUTO_COMMIT
	 * @see Initializer#setUseLazyTransaction(boolean)
	 */
	public static final ParsableOptionKey<Boolean> USE_AUTO_COMMIT = new ParsableOptionKey<>(
		"use-auto-commit",
		TO_BOOLEAN);

	/**
	 * default: false
	 * (Boolean) USE_LAZY_TRANSACTION
	 * @see Initializer#setUseLazyTransaction(boolean)
	 */
	public static final ParsableOptionKey<Boolean> USE_LAZY_TRANSACTION = new ParsableOptionKey<>(
		"use-lazy-transaction",
		TO_BOOLEAN);

	/**
	 * default: true
	 * (Boolean) USE_METADATA_CACHE
	 * @see Initializer#setUseMetadataCache(boolean)
	 */
	public static final ParsableOptionKey<Boolean> USE_METADATA_CACHE = new ParsableOptionKey<>(
		"use-metadata-cache",
		TO_BOOLEAN);

	/**
	 * default: 0
	 * (Boolean) AUTO_CLOSE_INTERVAL_MILLIS
	 * @see Initializer#setAutoCloseIntervalMillis(int)
	 */
	public static final ParsableOptionKey<Integer> AUTO_CLOSE_INTERVAL_MILLIS = new ParsableOptionKey<>(
		"auto-close-interval-millis",
		TO_INTEGER);

	/**
	 * default: ^(?!org\.blendee\.)
	 * (String) LOG_STACKTRACE_FILTER
	 * @see Initializer#setLogStackTraceFilter(java.util.regex.Pattern)
	 */
	public static final ParsableOptionKey<String> LOG_STACKTRACE_FILTER = new ParsableOptionKey<>(
		"log-stacktrace-filter",
		TO_STRING);

	/**
	 * default: {@link DefaultErrorConverter}
	 * (Class&lt;ErrorConverter&gt;) ERROR_CONVERTER_CLASS
	 * @see Initializer#setErrorConverterClass(Class)
	 */
	public static final ParsableOptionKey<Class<? extends ErrorConverter>> ERROR_CONVERTER_CLASS = new ParsableOptionKey<>(
		"error-converter-class",
		TO_CLASS);

	/**
	 * default: {@link AnnotationMetadataFactory}
	 * (Class&lt;MetadataFactory&gt;) METADATA_FACTORY_CLASS
	 * @see Initializer#setMetadataFactoryClass(Class)
	 */
	public static final ParsableOptionKey<Class<? extends MetadataFactory>> METADATA_FACTORY_CLASS = new ParsableOptionKey<>(
		"metadata-factory-class",
		TO_CLASS);

	/**
	 * default: {@link DriverTransactionFactory}
	 * (Class&lt;TransactionFactory&gt;) TRANSACTION_FACTORY_CLASS
	 * @see Initializer#setTransactionFactoryClass(Class)
	 */
	public static final ParsableOptionKey<Class<? extends TransactionFactory>> TRANSACTION_FACTORY_CLASS = new ParsableOptionKey<>(
		"transaction-factory-class",
		TO_CLASS);

	/**
	 * default: /blendee-metadata.xml
	 * (String) METADATA_XML_FILE
	 * @see FileMetadataFactory
	 */
	public static final ParsableOptionKey<String> METADATA_XML_FILE = new ParsableOptionKey<>(
		"metadata-xml-file",
		TO_STRING);

	/**
	 * default: {@link DefaultValueExtractors}
	 * (Class&lt;ValueExtractors&gt;) VALUE_EXTRACTORS_CLASS
	 * @see ValueExtractorsConfigure#setValueExtractorsClass(Class)
	 */
	public static final ParsableOptionKey<Class<? extends ValueExtractors>> VALUE_EXTRACTORS_CLASS = new ParsableOptionKey<>(
		"value-extractors-class",
		TO_CLASS);

	/**
	 * default: false
	 * (Boolean) CAN_ADD_NEW_ENTRIES
	 * @see AnchorOptimizerFactory#setCanAddNewEntries(boolean)
	 */
	public static final ParsableOptionKey<Boolean> CAN_ADD_NEW_ENTRIES = new ParsableOptionKey<>(
		"can-add-new-entries",
		TO_BOOLEAN);

	/**
	 * default: DEFAULT
	 * (String) HOME_STORAGE_IDENTIFIER
	 * @see FileColumnRepositoryFactory
	 */
	public static final ParsableOptionKey<String> HOME_STORAGE_IDENTIFIER = new ParsableOptionKey<>(
		"home-storage-identifier",
		TO_STRING);

	/**
	 * default: org.blendee.repository
	 * (String) COLUMN_REPOSITORY_FILE
	 * @see FileColumnRepositoryFactory
	 */
	public static final ParsableOptionKey<String> COLUMN_REPOSITORY_FILE = new ParsableOptionKey<>(
		"column-repository-file",
		TO_STRING);

	/**
	 * default: {@link FileColumnRepositoryFactory}
	 * (Class&lt;ColumnRepositoryFactory&gt;) COLUMN_REPOSITORY_FACTORY_CLASS
	 * @see AnchorOptimizerFactory#setColumnRepositoryFactoryClass(Class)
	 */
	public static final ParsableOptionKey<Class<? extends ColumnRepositoryFactory>> COLUMN_REPOSITORY_FACTORY_CLASS = new ParsableOptionKey<>(
		"column-repository-factory-class",
		TO_CLASS);

	/**
	 * (String) JDBC_DRIVER_CLASS
	 * @see DriverTransactionFactory
	 */
	public static final ParsableOptionKey<String> JDBC_DRIVER_CLASS_NAME = new ParsableOptionKey<>(
		"jdbc-driver-class-name",
		TO_STRING);

	/**
	 * (String) JDBC_URL
	 * @see DriverTransactionFactory
	 */
	public static final ParsableOptionKey<String> JDBC_URL = new ParsableOptionKey<>("jdbc-url", TO_STRING);

	/**
	 * (String) JDBC_USER
	 * @see DriverTransactionFactory
	 */
	public static final ParsableOptionKey<String> JDBC_USER = new ParsableOptionKey<>("jdbc-user", TO_STRING);

	/**
	 * (String) JDBC_PASSWORD
	 * @see DriverTransactionFactory
	 */
	public static final ParsableOptionKey<String> JDBC_PASSWORD = new ParsableOptionKey<>("jdbc-password", TO_STRING);

	/**
	 * (String) TABLE_FACADE_PACKAGE
	 * @see AnnotationMetadataFactory
	 */
	public static final ParsableOptionKey<String> TABLE_FACADE_PACKAGE = new ParsableOptionKey<>(
		"table-facade-packages",
		TO_STRING_ARRAY);

	/**
	 * 文字列のキーからこのクラスのメンバーに変換します。
	 * @param <T> オプション値の型
	 * @param key 変換前のキー
	 * @return 変換後のキー
	 */
	public static <T> ParsableOptionKey<T> convert(String key) {
		ParsableOptionKey<T> converted = ParsableOptionKey.convert(key);
		if (converted == null) throw new IllegalStateException("[" + key + "] は不明なキーです");
		return converted;
	}
}
