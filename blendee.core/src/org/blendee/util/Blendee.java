package org.blendee.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;

import org.blendee.internal.TransactionManager;
import org.blendee.internal.TransactionShell;
import org.blendee.jdbc.AutoCloseableFinalizer;
import org.blendee.jdbc.BTransaction;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.Initializer;
import org.blendee.jdbc.MetadataFactory;
import org.blendee.jdbc.OptionKey;
import org.blendee.jdbc.TransactionFactory;
import org.blendee.selector.AnchorOptimizerFactory;
import org.blendee.selector.ColumnRepositoryFactory;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.ValueExtractorsConfigure;

/**
 * Blendee 全体を対象とする、簡易操作クラスです。
 * @author 千葉 哲嗣
 */
public class Blendee {

	private Class<? extends MetadataFactory> defaultMetadataFactoryClass = AnnotationMetadataFactory.class;

	private Class<? extends TransactionFactory> defaultTransactionFactoryClass = DriverTransactionFactory.class;

	private Class<? extends ColumnRepositoryFactory> defaultColumnRepositoryFactoryClass = FileColumnRepositoryFactory.class;

	/**
	 * Blendee を使用可能な状態にします。
	 * @param initValues Blendee を初期化するための値
	 */
	public void start(Properties initValues) {
		Map<OptionKey<?>, Object> param = new HashMap<>();

		initValues.forEach((k, v) -> {
			ParsableOptionKey<?> key = BlendeeConstants.convert((String) k);
			param.put(key, key.parse((String) v).get());
		});

		start(param);
	}

	/**
	 * Blendee を使用可能な状態にします。
	 * @param initValues Blendee を初期化するための値
	 */
	public void start(Map<OptionKey<?>, ?> initValues) {
		Initializer init = new Initializer();

		init.setOptions(new HashMap<>(initValues));

		BlendeeConstants.SCHEMA_NAMES.extract(initValues).ifPresent(names -> {
			for (String name : names) {
				init.addSchemaName(name);
			}
		});

		BlendeeConstants.ENABLE_LOG.extract(initValues).ifPresent(flag -> init.enableLog(flag));

		BlendeeConstants.USE_LAZY_TRANSACTION.extract(initValues).ifPresent(flag -> init.setUseLazyTransaction(flag));

		BlendeeConstants.USE_METADATA_CACHE.extract(initValues).ifPresent(flag -> init.setUseMetadataCache(flag));

		BlendeeConstants.AUTO_CLOSE_INTERVAL_MILLIS.extract(initValues).ifPresent(millis -> init.setAutoCloseIntervalMillis(millis));

		BlendeeConstants.LOG_STACKTRACE_FILTER.extract(initValues).ifPresent(filter -> init.setLogStackTraceFilter(Pattern.compile(filter)));

		BlendeeConstants.ERROR_CONVERTER_CLASS.extract(initValues).ifPresent(clazz -> init.setErrorConverterClass(clazz));

		Optional.ofNullable(
			BlendeeConstants.METADATA_FACTORY_CLASS.extract(initValues).orElseGet(
				() -> Optional.of(getDefaultMetadataFactoryClass())
					.filter(c -> BlendeeConstants.ANNOTATED_ROW_PACKAGES.extract(initValues).isPresent())
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

		ContextManager.get(BlendeeManager.class).initialize(init);

		BlendeeConstants.VALUE_EXTRACTORS_CLASS.extract(initValues)
			.ifPresent(clazz -> ContextManager.get(ValueExtractorsConfigure.class).setValueExtractorsClass(clazz));

		AnchorOptimizerFactory anchorOptimizerFactory = ContextManager.get(AnchorOptimizerFactory.class);

		BlendeeConstants.CAN_ADD_NEW_ENTRIES.extract(initValues)
			.ifPresent(flag -> anchorOptimizerFactory.setCanAddNewEntries(flag));

		anchorOptimizerFactory.setColumnRepositoryFactoryClass(
			BlendeeConstants.COLUMN_REPOSITORY_FACTORY_CLASS.extract(initValues)
				.orElseGet(() -> getDefaultColumnRepositoryFactoryClass()));
	}

	/**
	 * 現在接続中の JDBC インスタンスをすべてクローズし、このコンテキストの Blendee を終了します。
	 */
	public static void stop() {
		AutoCloseableFinalizer finalizer = ContextManager.get(BlendeeManager.class).getAutoCloseableFinalizer();
		finalizer.stop();
		finalizer.closeAll();
	}

	/**
	 * トランザクション内で任意の処理を実行します。
	 * @param function {@link Function} の実装
	 * @throws Exception 処理内で起こった例外
	 */
	public static void execute(Function function) throws Exception {
		if (ContextManager.get(BlendeeManager.class).hasConnection()) throw new IllegalStateException("既にトランザクションが開始されています");

		TransactionManager.start(new TransactionShell() {

			@Override
			public void execute() throws Exception {
				function.execute(getTransaction());
			}
		});
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
	public static void clearCache() {
		ContextManager.get(BlendeeManager.class).clearMetadataCache();
		ContextManager.get(RelationshipFactory.class).clearCache();
	}

	/**
	 * トランザクション内で行う任意の処理を表しています。
	 */
	@FunctionalInterface
	public interface Function {

		/**
		 * トランザクション内で呼び出されます。 <br>
		 * 処理が終了した時点で commit が行われます。 <br>
		 * 例外を投げた場合は rollback が行われます。
		 * @param transaction この処理のトランザクション
		 * @throws Exception 処理内で起こった例外
		 */
		void execute(BTransaction transaction) throws Exception;
	}
}
