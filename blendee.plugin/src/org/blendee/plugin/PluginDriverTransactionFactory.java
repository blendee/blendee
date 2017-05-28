package org.blendee.plugin;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import org.blendee.util.DriverTransactionFactory;

public class PluginDriverTransactionFactory extends DriverTransactionFactory {

	private static JavaProjectClassLoader loader;

	private static Class<?> proxyClass;

	public PluginDriverTransactionFactory() throws Exception {
		if (proxyClass != null) return;

		URL url = PluginDriverTransactionFactory.class
			.getResource("/org/blendee/plugin/ProxyDriverManager.class");
		proxyClass = loader.defineClass(url);
	}

	static void setClassLoader(JavaProjectClassLoader loader) {
		PluginDriverTransactionFactory.loader = loader;
	}

	@Override
	protected ClassLoader getClassLoader() {
		return loader;
	}

	@Override
	protected Connection getConnection(String url, String user, String password)
		throws SQLException {
		try {
			return (Connection) proxyClass
				.getMethod(
					"getConnection",
					String.class,
					String.class,
					String.class)
				.invoke(null, url, user, password);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
