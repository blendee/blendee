package org.blendee.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.blendee.jdbc.BlendeeException;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.Initializer;
import org.blendee.jdbc.MetadataFactory;
import org.blendee.jdbc.OptionKey;
import org.blendee.jdbc.Transaction;
import org.blendee.jdbc.TransactionFactory;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.ValueExtractorsConfigure;

/**
 * Blendee 実行環境を管理するクラスです。
 * @author 千葉 哲嗣
 */
public class BlendeeEnvironment {

	private Class<? extends MetadataFactory> defaultMetadataFactoryClass = AnnotationMetadataFactory.class;

	private Class<? extends TransactionFactory> defaultTransactionFactoryClass = DriverManagerTransactionFactory.class;

	private Class<? extends DriverTransactionFactory> defaultDriverTransactionFactoryClass = DriverTransactionFactory.class;

	private final Consumer<Initializer> consumer;

	private final String contextName;

	/**
	 * デフォルトコンストラクタ
	 */
	public BlendeeEnvironment() {
		this.consumer = null;
		contextName = ContextManager.DEFAULT_CONTEXT_NAME;
	}

	/**
	 * @param consumer {@link Consumer}
	 */
	public BlendeeEnvironment(Consumer<Initializer> consumer) {
		this.consumer = consumer;
		contextName = ContextManager.DEFAULT_CONTEXT_NAME;
	}

	/**
	 * @param contextName my context name
	 */
	public BlendeeEnvironment(String contextName) {
		this.consumer = null;
		this.contextName = contextName;
	}

	/**
	 * @param contextName my context name
	 * @param consumer {@link Consumer}
	 */
	public BlendeeEnvironment(String contextName, Consumer<Initializer> consumer) {
		this.consumer = consumer;
		this.contextName = contextName;
	}

	/**
	 * Blendee が既に使用可能な状態かを判定します。<br>
	 * {@link #start} は 2 度実行できないため、このメソッドで先に確認することが可能です。
	 * @return Blendee が既に使用可能な状態かどうか
	 */
	public boolean started() {
		try {
			ContextManager.setContext(contextName);
			return ContextManager.get(BlendeeManager.class).initialized();
		} finally {
			ContextManager.releaseContext();
		}
	}

	/**
	 * Blendee を使用可能な状態にします。
	 * @param initValues Blendee を初期化するための値
	 * @return self
	 */
	public BlendeeEnvironment start(Properties initValues) {
		var param = new HashMap<OptionKey<?>, Object>();

		initValues.forEach((k, v) -> {
			var key = BlendeeConstants.convert((String) k);
			param.put(key, key.parse((String) v).get());
		});

		return start(param);
	}

	/**
	 * システムプロパティから Blendee を使用可能な状態にします。
	 * @return self
	 */
	public BlendeeEnvironment start() {
		var param = new Map<OptionKey<?>, Object>() {

			@Override
			public int size() {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean isEmpty() {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean containsKey(Object key) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean containsValue(Object value) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Object get(Object object) {
				var key = (ParsableOptionKey<?>) object;
				var value = System.getProperty("org.blendee." + key.getKey());
				return key.parse(value);
			}

			@Override
			public Object put(OptionKey<?> key, Object value) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Object remove(Object key) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void putAll(Map<? extends OptionKey<?>, ? extends Object> m) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void clear() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Set<OptionKey<?>> keySet() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Collection<Object> values() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Set<Entry<OptionKey<?>, Object>> entrySet() {
				throw new UnsupportedOperationException();
			}
		};

		return start(param);
	}

	/**
	 * Blendee を使用可能な状態にします。
	 * @param initValues Blendee を初期化するための値
	 * @return self
	 */
	public BlendeeEnvironment start(Map<OptionKey<?>, ?> initValues) {
		try {
			ContextManager.setContext(contextName);
			var init = new Initializer();

			init.setOptions(new HashMap<>(initValues));

			BlendeeConstants.SCHEMA_NAMES.extract(initValues).ifPresent(names -> {
				for (var name : names) {
					init.addSchemaName(name);
				}
			});

			BlendeeConstants.USE_AUTO_COMMIT.extract(initValues).ifPresent(flag -> init.setUseAutoCommit(flag));

			BlendeeConstants.USE_LAZY_TRANSACTION.extract(initValues).ifPresent(flag -> init.setUseLazyTransaction(flag));

			BlendeeConstants.USE_METADATA_CACHE.extract(initValues).ifPresent(flag -> init.setUseMetadataCache(flag));

			BlendeeConstants.AUTO_CLOSE_INTERVAL_MILLIS.extract(initValues).ifPresent(millis -> init.setAutoCloseIntervalMillis(millis));

			BlendeeConstants.LOGGER_CLASS.extract(initValues).ifPresent(clazz -> init.setLoggerClass(clazz));

			BlendeeConstants.SQL_EXTRACTOR_CLASS.extract(initValues).ifPresent(clazz -> init.setSQLExtractorClass(clazz));

			BlendeeConstants.LOG_STACKTRACE_FILTER.extract(initValues).ifPresent(filter -> init.setLogStackTraceFilter(Pattern.compile(filter)));

			BlendeeConstants.ERROR_CONVERTER_CLASS.extract(initValues).ifPresent(clazz -> init.setErrorConverterClass(clazz));

			Optional.ofNullable(
				BlendeeConstants.METADATA_FACTORY_CLASS.extract(initValues)
					.orElseGet(
						() -> Optional.of(getDefaultMetadataFactoryClass())
							.filter(c -> BlendeeConstants.TABLE_FACADE_PACKAGE.extract(initValues).isPresent())
							.orElse(null)))
				.ifPresent(clazz -> init.setMetadataFactoryClass(clazz));

			Optional.ofNullable(
				BlendeeConstants.TRANSACTION_FACTORY_CLASS.extract(initValues)
					.orElseGet(() -> {
						if (!BlendeeConstants.JDBC_URL.extract(initValues).filter(s -> s.length() > 0).isPresent()) {
							return null;
						}

						if (BlendeeConstants.JDBC_DRIVER_CLASS_NAME.extract(initValues).filter(s -> s.length() > 0).isPresent()) {
							return getDefaultDriverTransactionFactoryClass();
						}

						return getDefaultTransactionFactoryClass();
					}))
				.ifPresent(clazz -> init.setTransactionFactoryClass(clazz));

			if (consumer != null) consumer.accept(init);

			ContextManager.get(BlendeeManager.class).initialize(init);

			BlendeeConstants.VALUE_EXTRACTORS_CLASS.extract(initValues)
				.ifPresent(clazz -> ContextManager.get(ValueExtractorsConfigure.class).setValueExtractorsClass(clazz));

			BlendeeManager.getLogger().log(Level.INFO, "Blendee [" + contextName + "] start");

			return this;
		} finally {
			ContextManager.releaseContext();
		}
	}

	/**
	 * 現在接続中の JDBC インスタンスをすべてクローズし、このコンテキストの Blendee を終了します。
	 */
	public void stop() {
		try {
			ContextManager.setContext(contextName);
			var manager = ContextManager.get(BlendeeManager.class);
			if (manager.initialized()) {
				var finalizer = manager.getAutoCloseableFinalizer();
				finalizer.stop();
				finalizer.closeAll();
			}

			clearCache();

			BlendeeManager.getLogger().log(Level.INFO, "Blendee [" + contextName + "] stopped.");
		} finally {
			ContextManager.releaseContext();
		}
	}

	/**
	 * トランザクション内で任意の処理を実行します。
	 * @param consumer {@link BlendeeEnvironmentConsumer} の実装
	 * @throws BlendeeException 処理内で起こった例外
	 */
	public void execute(BlendeeEnvironmentConsumer consumer) {
		try {
			ContextManager.setContext(contextName);
			var manager = ContextManager.get(BlendeeManager.class);

			boolean top;
			Transaction transaction;
			if (manager.startsTransaction()) {
				transaction = manager.getCurrentTransaction();
				top = false;
			} else {
				transaction = manager.startTransaction();
				top = true;
			}

			try {
				consumer.execute(transaction);
				if (top) transaction.commit();
			} catch (Throwable t) {
				try {
					if (top) transaction.rollback();
				} catch (Throwable tt) {
					BlendeeManager.getLogger().log(Level.SEVERE, tt);
				}

				if (t instanceof BlendeeException) throw (BlendeeException) t;

				throw new BlendeeException(t);
			} finally {
				if (top) {
					try {
						transaction.close();
					} catch (Throwable tt) {
						BlendeeManager.getLogger().log(Level.SEVERE, tt);
					}
				}
			}
		} finally {
			ContextManager.releaseContext();
		}
	}

	private static class Container<T> {

		T value;
	}

	/**
	 * トランザクション内で任意の処理を実行します。
	 * @param function {@link BlendeeEnvironmentConsumer} の実装
	 * @return 戻り値
	 * @throws BlendeeException 処理内で起こった例外
	 */
	public <T> T executeAndGet(BlendeeEnvironmentFunction<T> function) {
		var result = new Container<T>();
		execute(transaction -> {
			result.value = function.execute(transaction);
		});

		return result.value;
	}

	/**
	 * デフォルト {@link MetadataFactory} を返します。
	 * @return デフォルト {@link MetadataFactory}
	 */
	public synchronized Class<? extends MetadataFactory> getDefaultMetadataFactoryClass() {
		return defaultMetadataFactoryClass;
	}

	/**
	 * デフォルト {@link MetadataFactory} をセットします。
	 * @param defaultMetadataFactoryClass デフォルト {@link MetadataFactory}
	 */
	public synchronized void setDefaultMetadataFactoryClass(
		Class<? extends MetadataFactory> defaultMetadataFactoryClass) {
		this.defaultMetadataFactoryClass = defaultMetadataFactoryClass;
	}

	/**
	 * デフォルト {@link TransactionFactory} を返します。
	 * @return デフォルト {@link TransactionFactory}
	 */
	public synchronized Class<? extends TransactionFactory> getDefaultTransactionFactoryClass() {
		return defaultTransactionFactoryClass;
	}

	/**
	 * デフォルト {@link TransactionFactory} をセットします。
	 * @param defaultTransactionFactoryClass デフォルト {@link TransactionFactory}
	 */
	public synchronized void setDefaultTransactionFactoryClass(
		Class<? extends TransactionFactory> defaultTransactionFactoryClass) {
		this.defaultTransactionFactoryClass = defaultTransactionFactoryClass;
	}

	/**
	 * デフォルト {@link TransactionFactory} を返します。
	 * @return デフォルト {@link TransactionFactory}
	 */
	public synchronized Class<? extends DriverTransactionFactory> getDefaultDriverTransactionFactoryClass() {
		return defaultDriverTransactionFactoryClass;
	}

	/**
	 * デフォルト {@link TransactionFactory} をセットします。
	 * @param defaultDriverTransactionFactoryClass デフォルト {@link DriverTransactionFactory}
	 */
	public synchronized void setDefaultDriverTransactionFactoryClass(
		Class<? extends DriverTransactionFactory> defaultDriverTransactionFactoryClass) {
		this.defaultDriverTransactionFactoryClass = defaultDriverTransactionFactoryClass;
	}

	/**
	 * Blendee が持つ定義情報の各キャッシュをクリアします。
	 */
	public void clearCache() {
		try {
			ContextManager.setContext(contextName);

			var manager = ContextManager.get(BlendeeManager.class);

			manager.clearMetadataCache();

			RelationshipFactory.getInstance().clearCache();
		} finally {
			ContextManager.releaseContext();
		}
	}

	/**
	 * トランザクション内で行う任意の処理を表します。
	 */
	@FunctionalInterface
	public interface BlendeeEnvironmentConsumer {

		/**
		 * トランザクション内で呼び出されます。 <br>
		 * 処理が終了した時点で commit が行われます。 <br>
		 * 例外を投げた場合は rollback が行われます。
		 * @param transaction この処理のトランザクション
		 * @throws Exception 処理内で起こった例外
		 */
		void execute(Transaction transaction) throws Exception;
	}

	/**
	 * トランザクション内で行う任意の処理を表します。
	 * @param <T> 戻り値
	 */
	@FunctionalInterface
	public interface BlendeeEnvironmentFunction<T> {

		/**
		 * トランザクション内で呼び出されます。 <br>
		 * 処理が終了した時点で commit が行われます。 <br>
		 * 例外を投げた場合は rollback が行われます。
		 * @param transaction この処理のトランザクション
		 * @return 戻り値
		 * @throws Exception 処理内で起こった例外
		 */
		T execute(Transaction transaction) throws Exception;
	}
}
