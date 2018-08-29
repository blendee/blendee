package org.blendee.jdbc;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.blendee.internal.U;

/**
 * Blendee の現在の設定を使用するためのクラスです。
 * @author 千葉 哲嗣
 * @see BlendeeManager#getConfigure()
 */
public final class Configure {

	private final String[] schemaNames;

	private final boolean useAutoCommit;

	private final boolean useLazyTransaction;

	private final boolean useMetadataCache;

	private final int autoCloseIntervalMillis;

	private final boolean enableLog;

	private final PrintStream logOutput;

	private final Pattern logStackTracePattern;

	private final int maxStatementsPerConnection;

	private final Map<OptionKey<?>, ?> options;

	private final Metadata metadata;

	private final TransactionFactory transactionFactory;

	private final ErrorConverter errorConverter;

	private final DataTypeConverter dataTypeConverter;

	Configure(
		Class<? extends TransactionFactory> transactionFactoryClass,
		Class<? extends ErrorConverter> errorConverterClass,
		Class<? extends DataTypeConverter> dataTypeConverterClass,
		Class<? extends MetadataFactory> metadataFactoryClass,
		String[] schemaNames,
		boolean useAutoCommit,
		boolean useLazyTransaction,
		boolean useMetadataCache,
		int autoCloseIntervalMillis,
		boolean enableLog,
		PrintStream logOutput,
		Pattern logStackTracePattern,
		int maxStatementsPerConnection,
		Map<OptionKey<?>, ?> options) {
		this.schemaNames = schemaNames.clone();
		this.useAutoCommit = useAutoCommit;
		this.useLazyTransaction = useLazyTransaction;
		this.useMetadataCache = useMetadataCache;
		this.autoCloseIntervalMillis = autoCloseIntervalMillis;
		this.enableLog = enableLog;
		this.logOutput = logOutput;
		this.logStackTracePattern = logStackTracePattern;
		this.maxStatementsPerConnection = maxStatementsPerConnection;
		this.options = Collections.unmodifiableMap(options);

		transactionFactory = createInstance(transactionFactoryClass);
		errorConverter = createInstance(errorConverterClass);
		dataTypeConverter = createInstance(dataTypeConverterClass);

		Metadata metadata = createInstance(metadataFactoryClass).createMetadata();
		if (useMetadataCache) metadata = new CacheMetadata(metadata);
		this.metadata = metadata;
	}

	/**
	 * この設定で使用している {@link TransactionFactory} を返します。
	 * @return この設定で使用している {@link TransactionFactory}
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public TransactionFactory getTransactionFactory() {
		check();
		return getTransactionFactoryWithoutCheck();
	}

	/**
	 * この設定で使用している {@link ErrorConverter} を返します。
	 * @return この設定で使用している {@link ErrorConverter}
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public ErrorConverter getErrorConverter() {
		check();
		return errorConverter;
	}

	/**
	 * この設定で使用している {@link DataTypeConverter} を返します。
	 * @return この設定で使用している {@link DataTypeConverter}
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public DataTypeConverter getDataTypeConverter() {
		check();
		return dataTypeConverter;
	}

	/**
	 * この設定で使用している {@link Metadata} を返します。
	 * @return この設定で使用している {@link Metadata}
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public Metadata getMetadata() {
		check();
		return getMetadataWithoutCheck();
	}

	/**
	 * この設定で使用しているスキーマ名を返します。
	 * @return この設定で使用しているスキーマ名
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public String[] getSchemaNames() {
		check();
		return schemaNames.clone();
	}

	/**
	 * この設定で使用しているスキーマ名にパラメータのスキーマ名が含まれるかを検査します。
	 * @param schemaName 検査するスキーマ名
	 * @return 含まれている場合、 true
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public boolean containsSchemaName(String schemaName) {
		check();
		for (int i = 0; i < schemaNames.length; i++) {
			if (schemaNames[i].equals(schemaName)) return true;
		}

		return false;
	}

	/**
	 * 自動コミットを行うかを設定します。
	 * @return 自動コミットを行う場合、 true
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public boolean usesAutoCommit() {
		check();
		return useAutoCommit;
	}

	/**
	 * 遅延実行トランザクションを使用するかどうかを検査します。
	 * @return 遅延実行トランザクションを使用するかどうか
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public boolean usesLazyTransaction() {
		check();
		return useLazyTransaction;
	}

	/**
	 * データベースの定義情報をキャッシュするかどうかを検査します。
	 * @return データベースの定義情報をキャッシュするかどうか
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public boolean usesMetadataCache() {
		check();
		return useMetadataCache;
	}

	/**
	 * {@link AutoCloseable} を実装した Blendee の各インスタンスを、もう使用されなくなったときに close() するためのチェック間隔（ミリ秒）を検査します。
	 * @return close() するためのチェック間隔（ミリ秒）
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public int getAutoCloseIntervalMillis() {
		check();
		return autoCloseIntervalMillis;
	}

	/**
	 * Blendee が実行する SQL 文を出力するかどうかを検査します。
	 * @return SQL 文を出力するかどうか
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public boolean enablesLog() {
		check();
		return enableLog;
	}

	/**
	 * Blendee が生成する SQL 文を出力する先を返します。
	 * @return SQL 文を出力する先
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public PrintStream getLogOutput() {
		check();
		return logOutput;
	}

	/**
	 * SQL 文生成箇所のスタックトレースをフィルタするパターンを返します。
	 * @return スタックトレースをフィルタするパターン
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public Pattern getLogStackTraceFilter() {
		check();
		return logStackTracePattern;
	}

	/**
	 * 一つの接続で最大いくつのステートメントを同時に使用できるかを返します。
	 * @return 一接続あたりの最大ステートメント数
	 */
	public int getMaxStatementsPerConnection() {
		check();
		return maxStatementsPerConnection;
	}

	/**
	 * この設定が現在の Blendee の設定かどうか検査します。
	 * @return 現在の Blendee の設定かどうか
	 */
	public boolean isCurrent() {
		return ContextManager.get(BlendeeManager.class).isCurrent(this);
	}

	/**
	 * Initializer から渡されたオプションの値を取得します。
	 * @param <T> オプション値の型
	 * @param key オプションのキー
	 * @return 値
	 */
	public <T> Optional<T> getOption(OptionKey<T> key) {
		return Objects.requireNonNull(key).extract(options);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	void check() {
		if (!isCurrent()) throw new IllegalStateException("古い設定を使用しています");
	}

	TransactionFactory getTransactionFactoryWithoutCheck() {
		return transactionFactory;
	}

	Metadata getMetadataWithoutCheck() {
		return metadata;
	}

	boolean enablesLogWithoutCheck() {
		return enableLog;
	}

	PrintStream getLogOutputWithoutCheck() {
		return logOutput;
	}

	Pattern getLogStackTracePatternWithoutCheck() {
		return logStackTracePattern;
	}

	private static <T> T createInstance(Class<? extends T> clazz) {
		try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
