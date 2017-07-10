package org.blendee.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.nio.charset.Charset;

import org.blendee.internal.U;
import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.BlendeeContext;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.sql.Binder;
import org.blendee.sql.ValueExtractors;
import org.blendee.sql.ValueExtractorsConfigure;

/**
 * あらかじめ用意しておいた SQL 文を実行する Proxy クラスを生成するビルダクラスです。<br>
 * あらかじめ用意しておいた SQL 文とは、一つの SQL 文を一つのファイルに記述し、そのファイル名を<br>
 * interface-name#method-name.sql<br>
 * として、インターフェイスと同じ場所に配備したもののことです。<br>
 * インターフェイスに定義するメソッドは、戻り値に<br>
 * {@link BResultSet}, int, boolean, void<br>
 * を使用することができます。<br>
 * メソッドのパラメータには、 SQL 文に記述したプレースホルダにセットする値を渡せるように定義してください。
 * @author 千葉 哲嗣
 */
public class SQLProxyBuilder {

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
			throw new IllegalArgumentException(sourceInterface + " はインターフェイスではありません");

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

	private static class SQLProxyInvocationHandler implements InvocationHandler {

		private final Charset charset;

		private SQLProxyInvocationHandler(Charset charset) {
			this.charset = charset;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Class<?> proxyClass = proxy.getClass().getInterfaces()[0];

			String sqlFileName = proxyClass.getName().replaceAll(".+?([^\\.]+)$", "$1")
				+ "#"
				+ method.getName()
				+ ".sql";

			URL url = U.getResourcePathByName(proxyClass, sqlFileName);

			if (url == null) throw new IllegalStateException(sqlFileName + " が見つかりません");

			String sql = new String(U.readBytes(url.openStream()), charset);

			ValueExtractors extractors = BlendeeContext.get(ValueExtractorsConfigure.class).getValueExtractors();
			Class<?>[] parameterTypes = method.getParameterTypes();
			final Binder[] binders = new Binder[parameterTypes.length];
			for (int i = 0; i < parameterTypes.length; i++) {
				Class<?> parameterType = parameterTypes[i];
				binders[i] = extractors.selectValueExtractor(parameterType).extractAsBinder(args[i]);
			}

			Class<?> returnType = method.getReturnType();

			BStatement statement = BlendeeContext.get(BlendeeManager.class).getConnection().getStatement(sql, s -> {
				for (int i = 0; i < binders.length; i++) {
					binders[i].bind(i + 1, s);
				}

				return binders.length;
			});

			if (returnType.equals(BResultSet.class)) {
				return statement.executeQuery();
			} else if (returnType.equals(int.class)) {
				return statement.executeUpdate();
			} else if (returnType.equals(boolean.class)) {
				return statement.execute();
			} else if (returnType.equals(void.class)) {
				return statement.execute();
			} else {
				throw new IllegalStateException("戻り値の型が不正です");
			}
		}
	}
}
