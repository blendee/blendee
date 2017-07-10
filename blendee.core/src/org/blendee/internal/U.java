package org.blendee.internal;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 内部使用ユーティリティクラス
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public class U {

	public static final String FILE_SEPARATOR = System.getProperty("file.separator");

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static final String[] STRING_EMPTY_ARRAY = {};

	private static final byte[] BYTE_EMPTY_ARRAY = {};

	private static final int BUFFER_SIZE = 8192;

	private static final ThreadLocal<Set<Container>> cycleCheckerThreadLocal = new ThreadLocal<Set<Container>>();

	private U() {}

	public static boolean equals(Object[] objects, Object[] others) {
		if (objects == null && others == null) return true;
		if (objects == null || others == null) return false;
		if (objects.length != others.length) return false;
		objects = objects.clone();
		others = others.clone();
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] == null && others[i] == null) continue;
			if (objects[i] == null || !objects[i].equals(others[i])) return false;
		}

		return true;
	}

	/**
	 * デバッグ用の、簡易文字列化メソッドです。
	 * <br>
	 * リフレクションを利用して、内部のフィールド値を出力します。
	 * <br>
	 * 循環参照が発生している場合、二度目の出力時に {repetition} と出力されます。
	 * <br>
	 * 使用上の注意点：
	 * <br>
	 * このメソッドを使用するのはあくまでも用途をデバッグに限定してください。
	 * <br>
	 * また、{@link Object} 以外の親クラスを持つクラスでは、親クラスの toString() メソッドをオーバーライドする可能性があるので、このメソッドを呼ぶ toString() を定義しないほうが無難です。
	 * @param object 文字列化対象
	 * @return object の文字列表現
	 */
	public static String toString(Object object) {
		Map<String, Object> map = new TreeMap<>();

		boolean top = false;
		Set<Container> checker = cycleCheckerThreadLocal.get();
		if (checker == null) {
			checker = new HashSet<>();
			checker.add(new Container(object));
			cycleCheckerThreadLocal.set(checker);
			top = true;
		}

		try {
			getFields(object.getClass(), object, map, checker);
			return "{id:" + System.identityHashCode(object) + " " + map.toString() + "}";
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} finally {
			if (top) cycleCheckerThreadLocal.set(null);
		}
	}

	public static boolean isAvailable(String value) {
		return value != null && !value.equals("");
	}

	/**
	 * 指定されたクラスと同パッケージで指定された名前のファイルのパスを返します。
	 * @param resourceBase リソースファイルと同パッケージ
	 * @param fileName ロードするファイル名
	 * @return fileName の URL
	 */
	public static URL getResourcePathByName(Class<?> resourceBase, String fileName) {
		String path = "/" + resourceBase.getPackage().getName().replace('.', '/') + "/" + fileName;
		return U.class.getResource(path);
	}

	public static void close(Closeable object) {
		if (object == null) return;
		try {
			object.close();
		} catch (IOException e) {
			throw new CloseFailedException(e);
		}
	}

	/**
	 * このストリームから読み込めるだけ読み込み、byte 配列として返します。
	 * @param in 読み込むストリーム
	 * @return 読み込んだデータ
	 * @throws IOException 読み込み時に発生した例外
	 */
	public static byte[] readBytes(InputStream in) throws IOException {
		byte[] concat = BYTE_EMPTY_ARRAY;
		byte[] b = new byte[BUFFER_SIZE];
		int readed;
		while ((readed = in.read(b, 0, BUFFER_SIZE)) > 0) {
			concat = concatByteArray(concat, concat.length, b, readed);
		}
		return concat;
	}

	/**
	 * in から読み込めるだけ読み込み、out へ出力します。
	 * @param in input
	 * @param out output
	 * @throws IOException I/O 中の例外
	 */
	public static void sendBytes(InputStream in, OutputStream out) throws IOException {
		byte[] b = new byte[BUFFER_SIZE];
		int readed;
		while ((readed = in.read(b, 0, BUFFER_SIZE)) > 0) {
			out.write(b, 0, readed);
		}
		out.flush();
	}

	/**
	 * クラスをロードするために使用される検索パスから、指定された名前のリソースをファイルとして取得します。
	 * このメソッドは実ファイルを対象とする場合のみ有効です。
	 * @param resource リソースの名前
	 * @return リソースに対応する {@link File} オブジェクト。リソースが見つからなかった場合は null
	 */
	public static File getResourceAsFile(String resource) {
		return getResourceAsFile(U.class.getResource(resource));
	}

	public static void close(Connection connection) {
		if (connection == null) return;
		try {
			if (!connection.isClosed()) connection.close();
		} catch (SQLException e) {
			throw new JDBCCloseFailedException(e);
		}
	}

	public static void close(PreparedStatement statement) {
		if (statement == null) return;
		try {
			statement.close();
		} catch (SQLException e) {
			throw new JDBCCloseFailedException(e);
		}
	}

	public static void close(ResultSet result) {
		if (result == null) return;
		try {
			result.close();
		} catch (SQLException e) {
			throw new JDBCCloseFailedException(e);
		}
	}

	private static byte[] concatByteArray(byte[] array1, int lengthof1, byte[] array2, int lengthof2) {
		byte[] concat = new byte[lengthof1 + lengthof2];
		System.arraycopy(array1, 0, concat, 0, lengthof1);
		System.arraycopy(array2, 0, concat, lengthof1, lengthof2);
		return concat;
	}

	/**
	 * {@link URL} として指定されたリソースをファイルとして取得します。
	 * このメソッドは実ファイルを対象とする場合のみ有効です。
	 * @param resource リソースの {@link URL}
	 * @return リソースに対応する {@link File} オブジェクト。リソースが見つからなかった場合は null
	 */
	private static File getResourceAsFile(URL resource) {
		if (resource == null) return null;

		Matcher matcher = Pattern.compile("^file:(.*)$").matcher(resource.toString());
		if (matcher.find()) {
			try {
				return new File(URLDecoder.decode(matcher.group(1), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new Error(e);
			}
		}
		return null;
	}

	private static void getFields(
		Class<?> clazz,
		Object object,
		Map<String, Object> map,
		Set<Container> checker)
		throws IllegalAccessException {
		Class<?> superclass = clazz.getSuperclass();
		if (superclass != null) getFields(superclass, object, map, checker);
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers())) continue;
			field.setAccessible(true);
			Object value = field.get(object);
			//循環参照を避けるため、一度調査したオブジェクトは使用しない
			if (value != null) {
				Container container = new Container(value);
				if (checker.contains(container)) {
					map.put(field.getName(), "{repetition}");
					continue;
				}
				checker.add(container);
			}

			map.put(field.getName(), value);
		}
	}

	public static class CloseFailedException extends RuntimeException {

		private static final long serialVersionUID = -748991448426048317L;

		private CloseFailedException(IOException e) {
			super(e);
		}

		private CloseFailedException(String message) {
			super(message);
		}
	}

	public static class JDBCCloseFailedException extends RuntimeException {

		private static final long serialVersionUID = 1616600547104822010L;

		private JDBCCloseFailedException(SQLException e) {
			super(e);
		}
	}

	public static <T> T call(Callable<T> callable) {
		try {
			return callable.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String care(String target) {
		return target == null ? "" : target;
	}

	public static String trim(String target) {
		return target == null ? "" : target.trim();
	}

	private static class Container {

		private final Object value;

		private Container(Object value) {
			this.value = value;
		}

		@Override
		public boolean equals(Object o) {
			return value == ((Container) o).value;
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(value);
		}
	}
}
