package jp.ats.blendee.util;

import static jp.ats.blendee.util.ParsableOptionKey.OptionValueParser.TO_BOOLEAN;
import static jp.ats.blendee.util.ParsableOptionKey.OptionValueParser.TO_CLASS;
import static jp.ats.blendee.util.ParsableOptionKey.OptionValueParser.TO_STRING;
import static jp.ats.blendee.util.ParsableOptionKey.OptionValueParser.TO_STRING_ARRAY;

import jp.ats.blendee.jdbc.ErrorConverter;
import jp.ats.blendee.jdbc.MetadataFactory;
import jp.ats.blendee.jdbc.TransactionFactory;
import jp.ats.blendee.selector.ColumnRepositoryFactory;
import jp.ats.blendee.selector.ValueExtractors;

/**
 * Blendee の起動に必要となる情報のキーを定めた定数インターフェイスです
 */
public interface BlendeeConstants {

	/**
	 * (String[]) SCHEMA_NAMES
	 */
	public static final ParsableOptionKey<String[]> SCHEMA_NAMES = new ParsableOptionKey<>(
		"schema-names",
		TO_STRING_ARRAY);

	/**
	 * (Boolean) ENABLE_LOG
	 */
	public static final ParsableOptionKey<Boolean> ENABLE_LOG = new ParsableOptionKey<>("enable-log", TO_BOOLEAN);

	/**
	 * (Boolean) USE_METADATA_CACHE
	 */
	public static final ParsableOptionKey<Boolean> USE_METADATA_CACHE = new ParsableOptionKey<>(
		"use-metadata-cache",
		TO_BOOLEAN);

	/**
	 * (String) LOG_STACKTRACE_FILTER
	 */
	public static final ParsableOptionKey<String> LOG_STACKTRACE_FILTER = new ParsableOptionKey<>(
		"log-stacktrace-filter",
		TO_STRING);

	/**
	 * (Class&lt;ErrorConverter&gt;) ERROR_CONVERTER_CLASS
	 */
	public static final ParsableOptionKey<Class<? extends ErrorConverter>> ERROR_CONVERTER_CLASS = new ParsableOptionKey<>(
		"error-converter-class",
		TO_CLASS);

	/**
	 * (Class&lt;MetadataFactory&gt;) METADATA_FACTORY_CLASS
	 */
	public static final ParsableOptionKey<Class<? extends MetadataFactory>> METADATA_FACTORY_CLASS = new ParsableOptionKey<>(
		"metadata-factory-class",
		TO_CLASS);

	/**
	 * (String) COLUMN_REPOSITORY_FILE
	 */
	public static final ParsableOptionKey<String> METADATTA_XML_FILE = new ParsableOptionKey<>(
		"metadata-xml-file",
		TO_STRING);

	/**
	 * (Class&lt;TransactionFactory&gt;) TRANSACTION_FACTORY_CLASS
	 */
	public static final ParsableOptionKey<Class<? extends TransactionFactory>> TRANSACTION_FACTORY_CLASS = new ParsableOptionKey<>(
		"transaction-factory-class",
		TO_CLASS);

	/**
	 * (Class&lt;ValueExtractors&gt;) VALUE_EXTRACTORS_CLASS
	 */
	public static final ParsableOptionKey<Class<? extends ValueExtractors>> VALUE_EXTRACTORS_CLASS = new ParsableOptionKey<>(
		"value-extractors-class",
		TO_CLASS);

	/**
	 * (Boolean) CAN_ADD_NEW_ENTRIES
	 */
	public static final ParsableOptionKey<Boolean> CAN_ADD_NEW_ENTRIES = new ParsableOptionKey<>(
		"can_add_new_entries",
		TO_BOOLEAN);

	/**
	 * (String) COLUMN_REPOSITORY_FILE
	 */
	public static final ParsableOptionKey<String> COLUMN_REPOSITORY_FILE = new ParsableOptionKey<>(
		"column-repository-file",
		TO_STRING);

	/**
	 * (Class&lt;ColumnRepositoryFactory&gt;) COLUMN_REPOSITORY_FACTORY_CLASS
	 */
	public static final ParsableOptionKey<Class<? extends ColumnRepositoryFactory>> COLUMN_REPOSITORY_FACTORY_CLASS = new ParsableOptionKey<>(
		"column-repository-factory-class",
		TO_CLASS);

	/**
	 * (String) JDBC_DRIVER_CLASS
	 */
	public static final ParsableOptionKey<String> JDBC_DRIVER_CLASS_NAME = new ParsableOptionKey<>(
		"jdbc-driver-class-name",
		TO_STRING);

	/**
	 * (String) JDBC_URL
	 */
	public static final ParsableOptionKey<String> JDBC_URL = new ParsableOptionKey<>("jdbc-url", TO_STRING);

	/**
	 * (String) JDBC_USER
	 */
	public static final ParsableOptionKey<String> JDBC_USER = new ParsableOptionKey<>("jdbc-user", TO_STRING);

	/**
	 * (String) JDBC_PASSWORD
	 */
	public static final ParsableOptionKey<String> JDBC_PASSWORD = new ParsableOptionKey<>("jdbc-password", TO_STRING);

	/**
	 * (String[]) ANNOTATED_DTO_PACKAGES
	 */
	public static final ParsableOptionKey<String[]> ANNOTATED_DTO_PACKAGES = new ParsableOptionKey<>(
		"annotated-dto-packages",
		TO_STRING_ARRAY);
}
