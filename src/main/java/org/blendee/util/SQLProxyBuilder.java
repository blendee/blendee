package org.blendee.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.blendee.internal.U;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.Batch;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.sql.Bindable;
import org.blendee.sql.BindableConverter;
import org.blendee.sql.Binder;
import org.blendee.util.annotation.SQL;
import org.blendee.util.annotation.SQLProxy;
import org.blendee.util.annotation.processor.Methods;

/**
 * あらかじめ用意しておいた SQL 文を実行する Proxy クラスを生成するビルダクラスです。<br>
 * あらかじめ用意しておいた SQL 文とは、一つの SQL 文を一つのファイルに記述し、そのファイル名を<br>
 * interface-name.method-name.sql<br>
 * として、インターフェイスと同じ場所に配備したもののことです。<br>
 * インターフェイスに定義するメソッドは、戻り値に<br>
 * {@link BResultSet}, int, boolean, void<br>
 * を使用することができます。<br>
 * メソッドのパラメータには、 SQL 文に記述したプレースホルダにセットする値複数か、 {@link PreparedStatementComplementer} を実装したオブジェクトのみを渡すように定義してください。
 * @author 千葉 哲嗣
 */
public class SQLProxyBuilder {

	@SuppressWarnings("javadoc")
	public static final String METADATA_CLASS_SUFFIX = "$SQLProxyMetadata";

	/**
	 * SQL 文の文字セットとして、デフォルトの {@link Charset} を使用して ProxyObject を生成します。
	 * @param <T> 生成される ProxyObject の型
	 * @param sourceInterface 生成される ProxyObject のインターフェイス
	 * @return 生成された ProxyObject
	 */
	public static <T> T buildProxyObject(Class<T> sourceInterface) {
		return buildProxyObject(sourceInterface, Charset.defaultCharset());
	}

	/**
	 * ProxyObject を生成します。
	 * @param <T> 生成される ProxyObject の型
	 * @param sourceInterface 生成される ProxyObject のインターフェイス
	 * @param sqlCharset SQL 文の文字セット
	 * @return 生成された ProxyObject
	 */
	public static <T> T buildProxyObject(Class<T> sourceInterface, Charset sqlCharset) {
		if (!sourceInterface.isInterface())
			//sourceInterface + " はインターフェイスではありません"
			throw new IllegalArgumentException(sourceInterface + " not interface");

		@SuppressWarnings("unchecked")
		T result = (T) Proxy.newProxyInstance(
			SQLProxyBuilder.class.getClassLoader(),
			new Class<?>[] { sourceInterface },
			new SQLProxyInvocationHandler(sqlCharset));

		return result;
	}

	/**
	 * ProxyObject を生成します。
	 * @param <T> 生成される ProxyObject の型
	 * @param sourceInterface 生成される ProxyObject のインターフェイス
	 * @param sqlCharset SQL 文の文字セット
	 * @return 生成された ProxyObject
	 * @see Charset#forName(String)
	 */
	public static <T> T buildProxyObject(Class<T> sourceInterface, String sqlCharset) {
		return buildProxyObject(sourceInterface, Charset.forName(sqlCharset));
	}

	private static final ThreadLocal<Batch> batchThreadLocal = new ThreadLocal<>();

	/**
	 * 現在実行中のスレッドに、引数の {@link Batch} を紐づけます。<br>
	 * {@link Batch} は、戻り値が int の、更新を指定された場合のみ使用されます。
	 * @param batch {@link Batch}
	 */
	public static void setBatch(Batch batch) {
		batchThreadLocal.set(batch);
	}

	/**
	 * 現在実行中のスレッドに紐づけられた {@link Batch} を開放します。
	 */
	public static void removeBatch() {
		batchThreadLocal.remove();
	}

	/**
	 * @param consumer
	 */
	public static void execute(Consumer<Batch> consumer) {
		Batch batch = BlendeeManager.getConnection().getBatch();
		batchThreadLocal.set(batch);
		try {
			consumer.accept(batch);
		} finally {
			batchThreadLocal.remove();
		}
	}

	private static class SQLProxyInvocationHandler implements InvocationHandler {

		private final Charset charset;

		private SQLProxyInvocationHandler(Charset charset) {
			this.charset = charset;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Class<?> proxyClass = proxy.getClass().getInterfaces()[0];

			if (!proxyClass.isAnnotationPresent(SQLProxy.class)) throw new IllegalStateException("annotation SQLProxy not found");

			String proxyClassName = proxyClass.getName();

			String sql;

			SQL sqlContainer = method.getAnnotation(SQL.class);
			if (sqlContainer != null) {
				sql = sqlContainer.value();
			} else {
				int packageNameLength = proxyClass.getPackage().getName().length();

				String sqlFileName = proxyClassName.substring(packageNameLength == 0 ? 0 : packageNameLength + 1)
					+ "."
					+ method.getName()
					+ ".sql";

				URL url = proxyClass.getResource(sqlFileName);
				//sqlFileName + " が見つかりません"
				if (url == null) throw new IllegalStateException(sqlFileName + " not found");

				sql = new String(U.readBytes(url.openStream()), charset);
			}

			Methods methods = Class.forName(proxyClassName + METADATA_CLASS_SUFFIX).getAnnotation(Methods.class);
			SQLProxyHelper helper = new SQLProxyHelper(
				sql,
				Arrays.asList(methods.value()).stream().filter(m -> m.name().equals(method.getName())).findFirst().get().args(),
				args);

			Class<?> returnType = method.getReturnType();

			if (returnType.equals(BResultSet.class)) {
				return statement(helper).executeQuery();
			} else if (returnType.equals(SQLProxy.ResultSet.class)) {
				return new SQLProxy.ResultSet(statement(helper).executeQuery());
			} else if (returnType.equals(int.class)) {
				Batch batch = batchThreadLocal.get();
				if (batch == null) return statement(helper).executeUpdate();

				batch.add(sql, helper);
				return 0;
			} else if (returnType.equals(boolean.class)) {
				return statement(helper).execute();
			} else if (returnType.equals(void.class)) {
				return statement(helper).execute();
			} else {
				//戻り値の型が不正です
				throw new IllegalStateException("Return type is incorrect: " + returnType);
			}
		}
	}

	private static class SQLProxyHelper implements PreparedStatementComplementer {

		private static final Pattern placeholder = Pattern.compile("\\$\\{ *([a-zA-Z_$][a-zA-Z\\d_$]*) *\\}");

		private final String sql;

		private final List<Binder> binders = new ArrayList<>();

		private SQLProxyHelper(String sql, String[] argNames, Object[] args) {
			Bindable[] bindables = BindableConverter.convertAllTypes(args);

			Map<String, Bindable> argMap = new HashMap<>();
			for (int i = 0; i < argNames.length; i++) {
				argMap.put(argNames[i], bindables[i]);
			}

			int position = 0;
			StringBuilder converted = new StringBuilder();
			while (true) {
				Matcher matcher = placeholder.matcher(sql);

				if (!matcher.find()) break;

				converted.append(sql.substring(0, matcher.start()));
				converted.append("?");

				position = matcher.end();

				sql = sql.substring(position);

				String placeholder = matcher.group(1);
				Bindable value = argMap.get(placeholder);

				if (value == null) throw new IllegalStateException("place holder [" + placeholder + "] was not found");

				binders.add(value.toBinder());
			}

			converted.append(sql);

			this.sql = converted.toString();
		}

		@Override
		public void complement(BPreparedStatement statement) {
			int i = 0;
			int size = binders.size();
			for (; i < size; i++) {
				binders.get(i).bind(i + 1, statement);
			}
		}
	}

	private static BStatement statement(SQLProxyHelper helper) {
		return BlendeeManager.getConnection().getStatement(helper.sql, helper);
	}
}
