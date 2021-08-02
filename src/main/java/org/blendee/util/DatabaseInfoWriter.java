package org.blendee.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

@SuppressWarnings("javadoc")
public class DatabaseInfoWriter {

	private final Path file;

	public DatabaseInfoWriter(Path homeDir, String rootPackageName) {
		Objects.requireNonNull(homeDir);
		Objects.requireNonNull(rootPackageName);
		file = homeDir.resolve(rootPackageName.replace('.', '/') + "/" + DatabaseInfo.fileName);
	}

	public boolean write(Properties properties) throws IOException {
		if (!needsOverwrite(properties)) return false;

		try (Writer writer = Files.newBufferedWriter(file, DatabaseInfo.defaultCharset)) {
			properties.store(writer, null);
		}

		return true;
	}

	public void mkdirs() throws IOException {
		Files.createDirectories(file.getParent());
	}

	public boolean exists() {
		return Files.exists(file);
	}

	private Properties read() throws IOException {
		Properties prop = new Properties();
		try (Reader reader = Files.newBufferedReader(file, DatabaseInfo.defaultCharset)) {
			prop.load(reader);
		}

		return prop;
	}

	private boolean needsOverwrite(Properties newOne) throws IOException {
		if (!exists()) return true;

		Properties oldOne = read();

		if (DatabaseInfo.hasStoredIdentifier(oldOne) && DatabaseInfo.hasStoredIdentifier(newOne)) {
			if (DatabaseInfo.getStoredIdentifier(oldOne).equals(DatabaseInfo.getStoredIdentifier(newOne))) return false;
		}

		return true;
	}
}
