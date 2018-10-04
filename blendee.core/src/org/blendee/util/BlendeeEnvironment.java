package org.blendee.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.blendee.jdbc.AutoCloseableFinalizer;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.Initializer;
import org.blendee.jdbc.MetadataFactory;
import org.blendee.jdbc.OptionKey;
import org.blendee.jdbc.Transaction;
import org.blendee.jdbc.TransactionFactory;
import org.blendee.selector.AnchorOptimizerFactory;
import org.blendee.selector.ColumnRepositoryFactory;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.ValueExtractorsConfigure;

/**
 * Blendee 実行環境を管理するクラスです。
 * @author 千葉 哲嗣
 */
public class BlendeeEnvironment {

	private Class<? extends MetadataFactory> defaultMetadataFactoryClass = AnnotationMetadataFactory.class;

	private Class<? extends TransactionFactory> defaultTransactionFactoryClass = DriverTransactionFactory.class;

	private Class<? extends ColumnRepositoryFactory> defaultColumnRepositoryFactoryClass = FileColumnRepositoryFactory.class;

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
		Map<OptionKey<?>, Object> param = new HashMap<>();

		initValues.forEach((k, v) -> {
			ParsableOptionKey<?> key = BlendeeConstants.convert((String) k);
			param.put(key, key.parse((String) v).get());
		});

		return start(param);
	}

	/**
	 * システムプロパティから Blendee を使用可能な状態にします。
	 * @return self
	 */
	public BlendeeEnvironment start() {
		Map<OptionKey<?>, Object> param = new Map<OptionKey<?>, Object>() {

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
				ParsableOptionKey<?> key = (ParsableOptionKey<?>) object;
				String value = System.getProperty("org.blendee." + key.getKey());
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
			Initializer init = new Initializer();

			init.setOptions(new HashMap<>(initValues));

			BlendeeConstants.SCHEMA_NAMES.extract(initValues).ifPresent(names -> {
				for (String name : names) {
					init.addSchemaName(name);
				}
			});

			BlendeeConstants.USE_AUTO_COMMIT.extract(initValues).ifPresent(flag -> init.setUseAutoCommit(flag));

			BlendeeConstants.USE_LAZY_TRANSACTION.extract(initValues).ifPresent(flag -> init.setUseLazyTransaction(flag));

			BlendeeConstants.USE_METADATA_CACHE.extract(initValues).ifPresent(flag -> init.setUseMetadataCache(flag));

			BlendeeConstants.AUTO_CLOSE_INTERVAL_MILLIS.extract(initValues).ifPresent(millis -> init.setAutoCloseIntervalMillis(millis));

			BlendeeConstants.LOGGER_CLASS.extract(initValues).ifPresent(clazz -> init.setLoggerClass(clazz));

			BlendeeConstants.LOG_STACKTRACE_FILTER.extract(initValues).ifPresent(filter -> init.setLogStackTraceFilter(Pattern.compile(filter)));

			BlendeeConstants.ERROR_CONVERTER_CLASS.extract(initValues).ifPresent(clazz -> init.setErrorConverterClass(clazz));

			Optional.ofNullable(
				BlendeeConstants.METADATA_FACTORY_CLASS.extract(initValues).orElseGet(
					() -> Optional.of(getDefaultMetadataFactoryClass())
						.filter(c -> BlendeeConstants.TABLE_FACADE_PACKAGE.extract(initValues).isPresent())
						.orElse(null)))
				.ifPresent(clazz -> init.setMetadataFactoryClass(clazz));

			Optional.ofNullable(
				BlendeeConstants.TRANSACTION_FACTORY_CLASS.extract(initValues).orElseGet(
					() -> Optional.of(getDefaultTransactionFactoryClass())
						.filter(
							c -> BlendeeConstants.JDBC_DRIVER_CLASS_NAME.extract(initValues)
								.filter(name -> name.length() > 0)
								.isPresent())
						.orElse(null)))
				.ifPresent(clazz -> init.setTransactionFactoryClass(clazz));

			if (consumer != null) consumer.accept(init);

			ContextManager.get(BlendeeManager.class).initialize(init);

			BlendeeConstants.VALUE_EXTRACTORS_CLASS.extract(initValues)
				.ifPresent(clazz -> ContextManager.get(ValueExtractorsConfigure.class).setValueExtractorsClass(clazz));

			AnchorOptimizerFactory anchorOptimizerFactory = ContextManager.get(AnchorOptimizerFactory.class);

			BlendeeConstants.CAN_ADD_NEW_ENTRIES.extract(initValues)
				.ifPresent(flag -> anchorOptimizerFactory.setCanAddNewEntries(flag));

			anchorOptimizerFactory.setColumnRepositoryFactoryClass(
				BlendeeConstants.COLUMN_REPOSITORY_FACTORY_CLASS.extract(initValues)
					.orElseGet(() -> getDefaultColumnRepositoryFactoryClass()));

			anchorOptimizerFactory.setColumnRepositoryFactoryClass(
				BlendeeConstants.COLUMN_REPOSITORY_FACTORY_CLASS.extract(initValues)
					.orElseGet(() -> getDefaultColumnRepositoryFactoryClass()));

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
			BlendeeManager manager = ContextManager.get(BlendeeManager.class);
			if (manager.initialized()) {
				AutoCloseableFinalizer finalizer = manager.getAutoCloseableFinalizer();
				finalizer.stop();
				finalizer.closeAll();
			}

			clearCache();
		} finally {
			ContextManager.releaseContext();
		}
	}

	/**
	 * トランザクション内で任意の処理を実行します。
	 * @param function {@link EnvironmentFunction} の実装
	 * @throws Exception 処理内で起こった例外
	 */
	public void execute(EnvironmentFunction function) throws Exception {
		try {
			ContextManager.setContext(contextName);
			BlendeeManager manager = ContextManager.get(BlendeeManager.class);

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
				function.execute(transaction);
				if (top) transaction.commit();
			} catch (Throwable t) {
				try {
					if (top) transaction.rollback();
				} catch (Throwable tt) {
					BlendeeManager.get().getConfigure().getLogger().log(tt);
				}

				throw t;
			} finally {
				if (top) {
					doFinally(
						() -> manager.getAutoCloseableFinalizer().closeAll(),
						() -> doFinally(
							() -> transaction.close(),
							ContextManager::releaseContext));
				}
			}
		} finally {
			ContextManager.releaseContext();
		}
	}

	private static void doFinally(Runnable mainFunction, Runnable finallyFunction) {
		try {
			mainFunction.run();
		} catch (Throwable t) {
			BlendeeManager.get().getConfigure().getLogger().log(t);
		} finally {
			finallyFunction.run();
		}
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
	 * デフォルト {@link ColumnRepositoryFactory} を返します。
	 * @return デフォルト {@link ColumnRepositoryFactory}
	 */
	public synchronized Class<? extends ColumnRepositoryFactory> getDefaultColumnRepositoryFactoryClass() {
		return defaultColumnRepositoryFactoryClass;
	}

	/**
	 * デフォルト {@link ColumnRepositoryFactory} をセットします。
	 * @param defaultColumnRepositoryFactoryClass デフォルト {@link ColumnRepositoryFactory}
	 */
	public synchronized void setDefaultColumnRepositoryFactoryClass(
		Class<? extends ColumnRepositoryFactory> defaultColumnRepositoryFactoryClass) {
		this.defaultColumnRepositoryFactoryClass = defaultColumnRepositoryFactoryClass;
	}

	/**
	 * Blendee が持つ定義情報の各キャッシュをクリアします。
	 */
	public void clearCache() {
		try {
			ContextManager.setContext(contextName);

			BlendeeManager manager = ContextManager.get(BlendeeManager.class);

			manager.clearMetadataCache();

			ContextManager.get(RelationshipFactory.class).clearCache();
		} finally {
			ContextManager.releaseContext();
		}
	}

	/**
	 * トランザクション内で行う任意の処理を表します。
	 */
	@FunctionalInterface
	public interface EnvironmentFunction {

		/**
		 * トランザクション内で呼び出されます。 <br>
		 * 処理が終了した時点で commit が行われます。 <br>
		 * 例外を投げた場合は rollback が行われます。
		 * @param transaction この処理のトランザクション
		 * @throws Exception 処理内で起こった例外
		 */
		void execute(Transaction transaction) throws Exception;
	}
}
