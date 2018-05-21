package org.blendee.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.nio.charset.Charset;

import org.blendee.internal.U;
import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.BlenResultSet;
import org.blendee.jdbc.BlenStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.sql.Binder;
import org.blendee.sql.ValueExtractors;
import org.blendee.sql.ValueExtractorsConfigure;

/**
 * あらかじめ用意しておいた SQL 文を実行する Proxy クラスを生成するビルダクラスです。<br>
 * あらかじめ用意しておいた SQL 文とは、一つの SQL 文を一つのファイルに記述し、そのファイル名を<br>
 * interface-name#method-name.sql<br>
 * として、インターフェイスと同じ場所に配備したもののことです。<br>
 * インターフェイスに定義するメソッドは、戻り値に<br>
 * {@link BlenResultSet}, int, boolean, void<br>
 * を使用することができます。<br>
 * メソッドのパラメータには、 SQL 文に記述したプレースホルダにセットする値複数か、 {@link PreparedStatementComplementer} を実装したオブジェクトのみを渡すように定義してください。
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

	private static final ThreadLocal<BatchStatement> batchStatement = new ThreadLocal<>();

	/**
	 * 現在実行中のスレッドに、引数の {@link BatchStatement} を紐づけます。<br>
	 * {@link BatchStatement} は、戻り値が int の、更新を指定された場合のみ使用されます。
	 * @param batch {@link BatchStatement}
	 */
	public static void setBatchStatement(BatchStatement batch) {
		batchStatement.set(batch);
	}

	/**
	 * 現在実行中のスレッドに紐づけられた {@link BatchStatement} を開放します。
	 */
	public static void removeBatchStatement() {
		batchStatement.remove();
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

			URL url = proxyClass.getResource(sqlFileName);
			if (url == null) throw new IllegalStateException(sqlFileName + " が見つかりません");

			String sql = new String(U.readBytes(url.openStream()), charset);

			Class<?>[] parameterTypes = method.getParameterTypes();

			PreparedStatementComplementer complementer;

			if (parameterTypes.length == 1 && PreparedStatementComplementer.class.isAssignableFrom(parameterTypes[0])) {
				//パラメータ数が1でそのクラスがPreparedStatementComplementerの場合、そのまま使用する
				complementer = (PreparedStatementComplementer) args[0];
			} else {
				//そうでなければ実際の引数であると判断しPreparedStatementComplementerを作成
				ValueExtractors extractors = ContextManager.get(ValueExtractorsConfigure.class).getValueExtractors();
				final Binder[] binders = new Binder[parameterTypes.length];
				for (int i = 0; i < parameterTypes.length; i++) {
					Class<?> parameterType = parameterTypes[i];
					binders[i] = extractors.selectValueExtractor(parameterType).extractAsBinder(args[i]);
				}

				complementer = s -> {
					for (int i = 0; i < binders.length; i++) {
						binders[i].bind(i + 1, s);
					}
				};
			}

			Class<?> returnType = method.getReturnType();

			if (returnType.equals(BlenResultSet.class)) {
				return statement(sql, complementer).executeQuery();
			} else if (returnType.equals(int.class)) {
				BatchStatement batch = batchStatement.get();
				if (batch == null) return statement(sql, complementer).executeUpdate();

				batch.addBatch(sql, complementer);
				return 0;
			} else if (returnType.equals(boolean.class)) {
				return statement(sql, complementer).execute();
			} else if (returnType.equals(void.class)) {
				return statement(sql, complementer).execute();
			} else {
				throw new IllegalStateException("戻り値の型が不正です");
			}
		}
	}

	private static BlenStatement statement(String sql, PreparedStatementComplementer complementer) {
		return BlendeeManager.getConnection().getStatement(sql, complementer);
	}
}
