package org.blendee.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.blendee.jdbc.StoredIdentifier;

@SuppressWarnings("javadoc")
public class DatabaseInfo {

	static final String fileName = "database-info";

	static final Charset defaultCharset = StandardCharsets.UTF_8;

	static final String storedIdentifierKey = "stored-identifier";

	public static void setStoredIdentifier(Properties properties, StoredIdentifier value) {
		properties.setProperty(storedIdentifierKey, value.name());
	}

	public static boolean hasStoredIdentifier(Properties properties) {
		var value = properties.getProperty(storedIdentifierKey);
		return value != null;
	}

	public static StoredIdentifier getStoredIdentifier(Properties properties) {
		return StoredIdentifier.valueOf(properties.getProperty(DatabaseInfo.storedIdentifierKey).toUpperCase());
	}

}
