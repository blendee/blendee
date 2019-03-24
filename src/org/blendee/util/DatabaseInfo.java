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

	public DatabaseInfo(String rootPackageName, ClassLoader loader) {
		Objects.requireNonNull(rootPackageName);
		this.rootPackageName = rootPackageName;
		this.loader = loader;
	}

	public Properties read() throws IOException {
		String path = rootPackageName.replace('.', '/') + "/" + fileName;

		Properties prop = new Properties();
		InputStream input = loader.getResourceAsStream(path);
		//path + " が存在しません"
		if (input == null) throw new IllegalStateException(path + " is not available.");

		try {
			prop.load(new BufferedReader(new InputStreamReader(input, defaultCharset)));
		} finally {
			input.close();
		}

		return prop;
	}

	public boolean write(File homeDir, Properties properties) throws IOException {
		if (!needsOverwrite(properties)) return false;

		File dir = new File(homeDir, String.join("/", rootPackageName.split("\\.")));

		try (Writer writer = new BufferedWriter(
			new OutputStreamWriter(
				new FileOutputStream(new File(dir, fileName)),
				defaultCharset))) {
			properties.store(writer, null);
		}

		return true;
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

	private boolean needsOverwrite(Properties newOne) throws IOException {
		Properties oldOne = read();

		if (hasStoredIdentifier(oldOne) && hasStoredIdentifier(newOne)) {
			if (getStoredIdentifier(oldOne).equals(getStoredIdentifier(newOne))) return false;
		}

		return true;
	}
}
