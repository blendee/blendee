package org.blendee.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

import org.blendee.jdbc.StoredIdentifier;

@SuppressWarnings("javadoc")
public class DatabaseInfo {

	private static final String fileName = "database-info";

	private static final Charset defaultCharset = StandardCharsets.UTF_8;

	private static final String storedIdentifierKey = "stored-identifier";

	private final String rootPackageName;

	private final ClassLoader loader;

	public DatabaseInfo(String rootPackageName) {
		Objects.requireNonNull(rootPackageName);
		this.rootPackageName = rootPackageName;
		loader = getClass().getClassLoader();
	}

	DatabaseInfo(String rootPackageName, ClassLoader loader) {
		Objects.requireNonNull(rootPackageName);
		this.rootPackageName = rootPackageName;
		this.loader = loader;
	}

	public Properties read() throws IOException {
		String path = rootPackageName.replace('.', '/') + "/" + fileName;

		Properties prop = new Properties();
		InputStream input = loader.getResourceAsStream(path);
		if (input == null) return prop;

		prop.load(new BufferedReader(new InputStreamReader(input, defaultCharset)));

		return prop;
	}

	public void write(File homeDir, Properties properties) throws IOException {
		File dir = new File(homeDir, String.join("/", rootPackageName.split("\\.")));

		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dir, fileName)), defaultCharset))) {
			properties.store(writer, DatabaseInfo.class.getName());
		}
	}

	public void setStoredIdentifier(Properties properties, StoredIdentifier value) {
		properties.setProperty(storedIdentifierKey, value.name());
	}

	public boolean hasStoredIdentifier(Properties properties) {
		String value = properties.getProperty(storedIdentifierKey);
		return value != null;
	}

	public StoredIdentifier getStoredIdentifier(Properties properties) {
		return StoredIdentifier.valueOf(properties.getProperty(storedIdentifierKey).toUpperCase());
	}
}
