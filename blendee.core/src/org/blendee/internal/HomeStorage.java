package org.blendee.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
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

	private final String fileName;

	public HomeStorage() {
		this(DEFAULT_IDENTIFIER);
	}

	public HomeStorage(String identifier) {
		if (!U.isAvailable(identifier)) throw new IllegalArgumentException(
			"identifier " + identifier + " が存在しません");
		fileName = ".blendee-" + identifier + ".properties";
	}

	public String getFileName() {
		return fileName;
	}

	public Properties loadProperties() {
		Properties properties = new Properties();
		try (InputStream input = new BufferedInputStream(
			new FileInputStream(preparePropertiesFile()))) {
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
			new FileOutputStream(preparePropertiesFile()))) {
			properties.store(output, "");
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public File getPropertiesFile() {
		return new File(System.getProperty("user.home"), fileName);
	}

	private File preparePropertiesFile() throws IOException {
		File file = getPropertiesFile();
		if (!file.exists()) {
			file.createNewFile();

			try (OutputStream output = new BufferedOutputStream(new FileOutputStream(file))) {
				new Properties().store(output, "");
			}
		}

		return file;
	}
}
