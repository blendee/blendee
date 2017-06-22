package org.blendee.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * ユーザーのホームディレクトリに 情報を格納するためのツールです。
 * <br>
 * 内部使用ユーティリティクラス
 *
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public class HomeStorage {

	private static final String DEFAULT_IDENTIFIER = "DEFAULT";

	private final Path directory = Paths.get(System.getProperty("user.home"), ".blendee");

	private final String fileName;

	public HomeStorage() {
		this(DEFAULT_IDENTIFIER);
	}

	public HomeStorage(String identifier) {
		if (!U.isAvailable(identifier)) throw new IllegalArgumentException(
			"identifier " + identifier + " が存在しません");
		fileName = identifier + ".properties";
	}

	public String getFileName() {
		return fileName;
	}

	public Properties loadProperties() {
		Properties properties = new Properties();
		try (InputStream input = new BufferedInputStream(
			Files.newInputStream(preparePropertiesFile()))) {
			try {
				properties.load(input);
			} finally {
				input.close();
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		return properties;
	}

	public void storeProperties(Properties properties) {
		try (OutputStream output = new BufferedOutputStream(
			Files.newOutputStream(preparePropertiesFile()))) {
			properties.store(output, "");
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public Path getPropertiesFile() {
		return directory.resolve(fileName);
	}

	private Path preparePropertiesFile() throws IOException {
		if (!Files.exists(directory)) Files.createDirectory(directory);

		Path file = getPropertiesFile();
		if (!Files.exists(file)) {
			Files.createFile(file);

			try (OutputStream output = new BufferedOutputStream(Files.newOutputStream(file))) {
				new Properties().store(output, "");
			}
		}

		return file;
	}
}
