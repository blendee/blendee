package jp.ats.blendee.jdbc;

import java.io.PrintStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import jp.ats.blendee.internal.U;

/**
 * Blendee の現在の設定を使用するためのクラスです。
 *
 * @author 千葉 哲嗣
 * @see BlendeeManager#getConfigure()
 */
public final class Configure {

	private final Class<? extends TransactionFactory> transactionFactoryClass;

	private final Class<? extends ErrorConverter> errorConverterClass;

	private final Class<? extends DataTypeConverter> dataTypeConverterClass;

	private final Class<? extends MetadataFactory> metadataFactoryClass;

	private final String[] schemaNames;

	private final boolean useMetadataCache;

	private final boolean enableLog;

	private final PrintStream logOutput;

	private final Pattern logStackTracePattern;

	private final int maxStatementsPerConnection;

	private final Map<OptionKey<?>, ?> options;

	private TransactionFactory transactionFactory;

	private ErrorConverter errorConverter;

	private DataTypeConverter dataTypeConverter;

	private Metadata[] metadatas;

	private boolean initialized = false;

	private boolean metadatasInitialized = false;

	Configure(
		Class<? extends TransactionFactory> transactionFactoryClass,
		Class<? extends ErrorConverter> errorConverterClass,
		Class<? extends DataTypeConverter> dataTypeConverterClass,
		Class<? extends MetadataFactory> metadataFactoryClass,
		String[] schemaNames,
		boolean useMetadataCache,
		boolean enableLog,
		PrintStream logOutput,
		Pattern logStackTracePattern,
		int maxStatementsPerConnection,
		Map<OptionKey<?>, ?> options) {
		this.transactionFactoryClass = transactionFactoryClass;
		this.errorConverterClass = errorConverterClass;
		this.dataTypeConverterClass = dataTypeConverterClass;
		this.metadataFactoryClass = metadataFactoryClass;
		this.schemaNames = schemaNames.clone();
		this.useMetadataCache = useMetadataCache;
		this.enableLog = enableLog;
		this.logOutput = logOutput;
		this.logStackTracePattern = logStackTracePattern;
		this.maxStatementsPerConnection = maxStatementsPerConnection;
		this.options = options;
	}

	/**
	 * この設定で使用している {@link TransactionFactory} を返します。
	 *
	 * @return この設定で使用している {@link TransactionFactory}
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public TransactionFactory getTransactionFactory() {
		check();
		return getTransactionFactoryWithoutCheck();
	}

	/**
	 * この設定で使用している {@link ErrorConverter} を返します。
	 *
	 * @return この設定で使用している {@link ErrorConverter}
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public synchronized ErrorConverter getErrorConverter() {
		check();
		return errorConverter;
	}

	/**
	 * この設定で使用している {@link DataTypeConverter} を返します。
	 *
	 * @return この設定で使用している {@link DataTypeConverter}
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public synchronized DataTypeConverter getDataTypeConverter() {
		check();
		return dataTypeConverter;
	}

	/**
	 * この設定で使用している {@link Metadata} を返します。
	 *
	 * @return この設定で使用している {@link Metadata}
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public Metadata[] getMetadatas() {
		check();
		return getMetadatasWithoutCheck();
	}

	/**
	 * この設定で使用しているスキーマ名を返します。
	 *
	 * @return この設定で使用しているスキーマ名
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public String[] getSchemaNames() {
		check();
		return schemaNames.clone();
	}

	/**
	 * この設定で使用しているスキーマ名にパラメータのスキーマ名が含まれるかを検査します。
	 *
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
	 * データベースの定義情報をキャッシュするかどうかを検査します。
	 *
	 * @return データベースの定義情報をキャッシュするかどうか
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public boolean usesMetadataCache() {
		check();
		return useMetadataCache;
	}

	/**
	 * Blendee が実行する SQL 文を出力するかどうかを検査します。
	 *
	 * @return SQL 文を出力するかどうか
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public boolean enablesLog() {
		check();
		return enableLog;
	}

	/**
	 * Blendee が生成する SQL 文を出力する先を返します。
	 *
	 * @return SQL 文を出力する先
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public PrintStream getLogOutput() {
		check();
		return logOutput;
	}

	/**
	 * SQL 文生成箇所のスタックトレースをフィルタするパターンを返します。
	 *
	 * @return スタックトレースをフィルタするパターン
	 * @throws IllegalStateException 古い設定を使用している場合
	 */
	public Pattern getLogStackTraceFilter() {
		check();
		return logStackTracePattern;
	}

	/**
	 * 一つの接続で最大いくつのステートメントを同時に使用できるかを返します。
	 *
	 * @return 一接続あたりの最大ステートメント数
	 */
	public int getMaxStatementsPerConnection() {
		check();
		return maxStatementsPerConnection;
	}

	/**
	 * この設定が現在の Blendee の設定かどうか検査します。
	 *
	 * @return 現在の Blendee の設定かどうか
	 */
	public boolean isCurrent() {
		return BContext.get(BlendeeManager.class).isCurrent(this);
	}

	/**
	 * Initializer から渡されたオプションの値を取得します。
	 *
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

	synchronized void initialize() {
		if (initialized) return;
		transactionFactory = createInstance(transactionFactoryClass);
		errorConverter = createInstance(errorConverterClass);
		dataTypeConverter = createInstance(dataTypeConverterClass);
		initialized = true;
	}

	synchronized void initializeMetadatas(Metadata depends) {
		if (metadatasInitialized) return;
		metadatas = createInstance(metadataFactoryClass).createMetadatas(depends);
		metadatasInitialized = true;
	}

	synchronized TransactionFactory getTransactionFactoryWithoutCheck() {
		return transactionFactory;
	}

	synchronized Metadata[] getMetadatasWithoutCheck() {
		return metadatas;
	}

	boolean usesMetadataCacheWithoutCheck() {
		return useMetadataCache;
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
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
