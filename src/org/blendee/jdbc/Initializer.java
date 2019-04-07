package org.blendee.jdbc;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.blendee.internal.U;

/**
 * Blendee を初期化するための情報をまとめるクラスです。
 * @author 千葉 哲嗣
 * @see BlendeeManager#initialize(Initializer)
 */
public final class Initializer {

	private final List<String> schemaNames = new LinkedList<>();

	private Class<? extends TransactionFactory> transactionFactoryClass = DataSourceTransactionFactory.class;

	private Class<? extends ErrorConverter> errorConverterClass = DefaultErrorConverter.class;

	private Class<? extends DataTypeConverter> dataTypeConverterClass = DefaultDataTypeConverter.class;

	private Class<? extends MetadataFactory> metadataFactoryClass = DefaultMetadataFactory.class;

	private Class<? extends BLogger> loggerClass = DefaultLogger.class;

	private boolean useAutoCommit = false;

	private boolean useLazyTransaction = false;

	private boolean useMetadataCache = true;

	private int autoCloseIntervalMillis = 0;

	private Pattern logStackTracePattern = Pattern.compile("^(?!org\\.blendee\\.)");

	private int maxStatementsPerConnection = Integer.MAX_VALUE;

	private Map<OptionKey<?>, ?> options;

	private boolean freeze = false;

	/**
	 * Blendee が使用する {@link TransactionFactory} を設定します。<br>
	 * {@link TransactionFactory} は、必ず設定する必要があります。
	 * @param transactionFactoryClass {@link TransactionFactory} を実装したクラス
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void setTransactionFactoryClass(
		Class<? extends TransactionFactory> transactionFactoryClass) {
		if (freeze) throw new IllegalStateException();
		Objects.requireNonNull(transactionFactoryClass);
		this.transactionFactoryClass = transactionFactoryClass;
	}

	/**
	 * Blendee が使用する {@link ErrorConverter} を設定します。
	 * @param errorConverterClass {@link ErrorConverter} を実装したクラス
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void setErrorConverterClass(
		Class<? extends ErrorConverter> errorConverterClass) {
		if (freeze) throw new IllegalStateException();
		Objects.requireNonNull(errorConverterClass);
		this.errorConverterClass = errorConverterClass;
	}

	/**
	 * Blendee が使用する {@link DataTypeConverter} を設定します。
	 * @param dataTypeConverterClass {@link DataTypeConverter} を実装したクラス
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void setDataTypeConverterClass(
		Class<? extends DataTypeConverter> dataTypeConverterClass) {
		if (freeze) throw new IllegalStateException();
		Objects.requireNonNull(dataTypeConverterClass);
		this.dataTypeConverterClass = dataTypeConverterClass;
	}

	/**
	 * Blendee が使用する {@link MetadataFactory} を設定します。
	 * @param metadataFactoryClass {@link MetadataFactory} を実装したクラス
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void setMetadataFactoryClass(
		Class<? extends MetadataFactory> metadataFactoryClass) {
		if (freeze) throw new IllegalStateException();
		Objects.requireNonNull(metadataFactoryClass);
		this.metadataFactoryClass = metadataFactoryClass;
	}

	/**
	 * Blendee が使用する {@link BLogger} を設定します。
	 * @param loggerClass {@link BLogger} を実装したクラス
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void setLoggerClass(
		Class<? extends BLogger> loggerClass) {
		if (freeze) throw new IllegalStateException();
		Objects.requireNonNull(loggerClass);
		this.loggerClass = loggerClass;
	}

	/**
	 * Blendee が使用するスキーマ名を登録します。<br>
	 * Blendee を動作させるには、最低でも一つスキーマ名が必要です。
	 * @param schemaName 使用するスキーマ名
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void addSchemaName(String schemaName) {
		if (freeze) throw new IllegalStateException();
		if (schemaName == null) throw new NullPointerException();
		schemaName = schemaName.trim();
		if (schemaNames.size() == 1 && (schemaNames.get(0).length() == 0 || schemaName.length() == 0))
			//スキーマ名を複数件使用する場合、空のスキーマ名は使用できません
			throw new IllegalArgumentException("Can not use an empty schema name in multiple schema names.");
		schemaNames.add(schemaName);
	}

	/**
	 * 自動コミットを行うかを設定します。
	 * @param useAutoCommit 自動コミットを行う場合、 true
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void setUseAutoCommit(boolean useAutoCommit) {
		if (freeze) throw new IllegalStateException();
		this.useAutoCommit = useAutoCommit;
	}

	/**
	 * 遅延実行トランザクションを使用するかどうかを設定します。
	 * @param useLazyTransaction 遅延実行トランザクションを使用するかどうか
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void setUseLazyTransaction(boolean useLazyTransaction) {
		if (freeze) throw new IllegalStateException();
		this.useLazyTransaction = useLazyTransaction;
	}

	/**
	 * データベースの定義情報をキャッシュするかどうかを設定します。
	 * @param useMetadataCache データベースの定義情報をキャッシュするかどうか
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void setUseMetadataCache(boolean useMetadataCache) {
		if (freeze) throw new IllegalStateException();
		this.useMetadataCache = useMetadataCache;
	}

	/**
	 * {@link AutoCloseable} を実装した Blendee の各インスタンスを、もう使用されなくなったときに close() するためのチェック間隔（ミリ秒）を設定します。<br>
	 * 0 以下の値を設定した場合、自動クローズは行われません。
	 * @param autoCloseIntervalMillis close() するためのチェック間隔（ミリ秒）
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void setAutoCloseIntervalMillis(int autoCloseIntervalMillis) {
		if (freeze) throw new IllegalStateException();
		this.autoCloseIntervalMillis = autoCloseIntervalMillis;
	}

	/**
	 * ログに出力されるSQL文生成箇所のスタックトレースをフィルタしたい場合に、そのパターンを設定します。
	 * @param logStackTracePattern スタックトレースをフィルタするパターン
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void setLogStackTraceFilter(Pattern logStackTracePattern) {
		if (freeze) throw new IllegalStateException();
		if (logStackTracePattern == null) throw new NullPointerException();
		this.logStackTracePattern = logStackTracePattern;
	}

	/**
	 * 一つの接続で最大いくつのステートメントを同時に使用できるかを設定します。
	 * @param max 一接続あたりの最大ステートメント数
	 */
	public synchronized void setMaxStatementsPerConnection(int max) {
		//0 以下の値は設定できません
		if (max < 1) throw new IllegalArgumentException("max < 1");
		maxStatementsPerConnection = max;
	}

	/**
	 * Blendee 内のクラスが使用するオプションをセットします。
	 * @param options オプション
	 */
	public synchronized void setOptions(Map<OptionKey<?>, ?> options) {
		this.options = new HashMap<>(options);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	synchronized Configure createConfigure() {
		//スキーマ名は最低一つ必要です
		if (schemaNames.size() == 0) throw new IllegalArgumentException("At least one schema name required.");
		freeze = true;
		return new Configure(
			transactionFactoryClass,
			errorConverterClass,
			dataTypeConverterClass,
			metadataFactoryClass,
			loggerClass,
			schemaNames.toArray(new String[schemaNames.size()]),
			useAutoCommit,
			useLazyTransaction,
			useMetadataCache,
			autoCloseIntervalMillis,
			logStackTracePattern,
			maxStatementsPerConnection,
			options);
	}
}
