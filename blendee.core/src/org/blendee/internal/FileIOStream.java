package org.blendee.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;

/**
 * 内部使用ユーティリティクラス
 *
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public class FileIOStream implements IOStream {

	private final File file;

	private InputStream input;

	private OutputStream output;

	public FileIOStream(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (input == null) input = new FileInputStream(file);
		return input;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		if (output == null) output = new FileOutputStream(file);
		return output;
	}

	@Override
	public void close() {
		try {
			if (input != null) input.close();
			if (output != null) output.close();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		input = null;
		output = null;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
