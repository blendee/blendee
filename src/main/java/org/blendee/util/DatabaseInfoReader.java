package org.blendee.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Properties;

@SuppressWarnings("javadoc")
public class DatabaseInfoReader {

	private final String rootPackageName;

	private final ClassLoader loader;

	public DatabaseInfoReader(String rootPackageName) {
		Objects.requireNonNull(rootPackageName);
		this.rootPackageName = rootPackageName;
		loader = getClass().getClassLoader();
	}

	public DatabaseInfoReader(String rootPackageName, ClassLoader loader) {
		Objects.requireNonNull(rootPackageName);
		this.rootPackageName = rootPackageName;
		this.loader = loader;
	}

	public Properties read() throws IOException {
		var path = rootPackageName.replace('.', '/') + "/" + DatabaseInfo.fileName;

		var prop = new Properties();
		var input = loader.getResourceAsStream(path);
		//path + " が存在しません"
		if (input == null) throw new IllegalStateException(path + " not found.");

		try {
			prop.load(new BufferedReader(new InputStreamReader(input, DatabaseInfo.defaultCharset)));
		} finally {
			input.close();
		}

		return prop;
	}

	public boolean exists() {
		var path = rootPackageName.replace('.', '/') + "/" + DatabaseInfo.fileName;
		return loader.getResource(path) != null;
	}
}
