package jp.ats.blendee.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;

/**
 * 内部使用ユーティリティクラス
 *
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public class Command {

	private Command() {}

	public static int execute(String command) {
		return execute(command, null, System.out, System.err);
	}

	public static int execute(String command, PrintStream out, PrintStream err) {
		return execute(command, null, out, err);
	}

	public static int execute(String command, InputStream in, PrintStream out, PrintStream err) {
		try {
			Process process = Runtime.getRuntime().exec(command);
			InputStream outStream = new BufferedInputStream(process.getInputStream());
			InputStream errStream = new BufferedInputStream(process.getErrorStream());
			OutputStream sendStream = new BufferedOutputStream(process.getOutputStream());

			Thread outThread = skip(outStream, out);
			Thread errThread = skip(errStream, err);

			if (in != null) U.sendBytes(in, sendStream);

			process.waitFor();

			outThread.join();
			errThread.join();

			return process.exitValue();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private static Thread skip(InputStream stream, PrintStream printer) {
		Skipper skipper = new Skipper(stream, printer);
		Thread runner = new Thread(skipper);
		runner.start();
		return runner;
	}

	private static class Skipper implements Runnable {

		private final InputStream stream;

		private final PrintStream printer;

		private Skipper(InputStream stream, PrintStream printer) {
			this.stream = stream;
			this.printer = printer;
		}

		@Override
		public void run() {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
				for (String line; (line = reader.readLine()) != null;) {
					synchronized (printer) {
						printer.println(line);
					}
				}
				reader.close();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
}
