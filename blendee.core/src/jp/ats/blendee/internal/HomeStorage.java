package jp.ats.blendee.internal;

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

	public static final String FILE_NAME = ".blendee.properties";

	public static Properties loadProperties() {
		Properties properties = new Properties();
		try {
			InputStream input = new BufferedInputStream(
				new FileInputStream(preparePropertiesFile()));
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

	public static void storeProperties(Properties properties) {
		try (OutputStream output = new BufferedOutputStream(
			new FileOutputStream(preparePropertiesFile()))) {
			properties.store(output, "");
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static File getPropertiesFile() {
		return new File(System.getProperty("user.home"), FILE_NAME);
	}

	private static File preparePropertiesFile() throws IOException {
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
