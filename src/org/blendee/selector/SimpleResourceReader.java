package org.blendee.selector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 千葉 哲嗣
 */
final class SimpleResourceReader {

	//インスタンス化不可
	private SimpleResourceReader() {}

	static String[] readLines(InputStream input) {
		return readLines(input, Charset.defaultCharset());
	}

	static String[] readLines(InputStream input, Charset charset) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, charset))) {
			List<String> list = new LinkedList<>();
			for (String line; (line = reader.readLine()) != null;) {
				line = line.replaceAll("#.*$", "");
				line = line.trim();
				if (line.length() == 0) continue;
				list.add(line);
			}

			return list.toArray(new String[list.size()]);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
