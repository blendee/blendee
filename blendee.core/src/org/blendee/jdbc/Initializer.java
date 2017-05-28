package org.blendee.jdbc;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.blendee.internal.U;

/**
 * Blendee を初期化するための情報をまとめるクラスです。
 *
 * @author 千葉 哲嗣
 * @see BlendeeManager#initialize(Initializer)
 */
public final class Initializer {

	private final List<String> schemaNames = new LinkedList<>();

	private Class<? extends TransactionFactory> transactionFactoryClass = DataSourceTransactionFactory.class;

	private Class<? extends ErrorConverter> errorConverterClass = DefaultErrorConverter.class;

	private Class<? extends DataTypeConverter> dataTypeConverterClass = DefaultDataTypeConverter.class;

	private Class<? extends MetadataFactory> metadataFactoryClass = DefaultMetadataFactory.class;

	private boolean useMetadataCache = true;

	private boolean enableLog = false;

	private PrintStream logOutput = System.out;

	private Pattern logStackTracePattern = Pattern.compile("^(?!jp\\.ats\\.)");

	private int maxStatementsPerConnection = Integer.MAX_VALUE;

	private Map<OptionKey<?>, ?> options;

	private boolean freeze = false;

	/**
	 * Blendee が使用する {@link TransactionFactory} を設定します。
	 * <br>
	 * {@link TransactionFactory} は、必ず設定する必要があります。
	 *
	 * @param transactionFactoryClass {@link TransactionFactory} を実装したクラス
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void setTransactionFactoryClass(
		Class<? extends TransactionFactory> transactionFactoryClass) {
		if (freeze) throw new IllegalStateException();
		if (transactionFactoryClass == null) throw new NullPointerException();
		this.transactionFactoryClass = transactionFactoryClass;
	}

	/**
	 * Blendee が使用する {@link ErrorConverter} を設定します。
	 *
	 * @param errorConverterClass {@link ErrorConverter} を実装したクラス
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void setErrorConverterClass(
		Class<? extends ErrorConverter> errorConverterClass) {
		if (freeze) throw new IllegalStateException();
		if (errorConverterClass == null) throw new NullPointerException();
		this.errorConverterClass = errorConverterClass;
	}

	/**
	 * Blendee が使用する {@link DataTypeConverter} を設定します。
	 *
	 * @param dataTypeConverterClass {@link DataTypeConverter} を実装したクラス
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void setDataTypeConverterClass(
		Class<? extends DataTypeConverter> dataTypeConverterClass) {
		if (freeze) throw new IllegalStateException();
		if (dataTypeConverterClass == null) throw new NullPointerException();
		this.dataTypeConverterClass = dataTypeConverterClass;
	}

	/**
	 * Blendee が使用する {@link MetadataFactory} を設定します。
	 *
	 * @param metadataFactoryClass {@link MetadataFactory} を実装したクラス
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void setMetadataFactoryClass(
		Class<? extends MetadataFactory> metadataFactoryClass) {
		if (freeze) throw new IllegalStateException();
		if (metadataFactoryClass == null) throw new NullPointerException();
		this.metadataFactoryClass = metadataFactoryClass;
	}

	/**
	 * Blendee が使用するスキーマ名を登録します。
	 * <br>
	 * Blendee を動作させるには、最低でも一つスキーマ名が必要です。
	 *
	 * @param schemaName 使用するスキーマ名
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void addSchemaName(String schemaName) {
		if (freeze) throw new IllegalStateException();
		if (schemaName == null) throw new NullPointerException();
		schemaName = schemaName.trim();
		if (schemaNames.size() == 1 && (schemaNames.get(0).length() == 0 || schemaName.length() == 0))
			throw new IllegalArgumentException("スキーマ名を複数件使用する場合、空のスキーマ名は使用できません");
		schemaNames.add(schemaName);
	}

	/**
	 * データベースの定義情報をキャッシュするかどうかを設定します。
	 *
	 * @param useMetadataCache データベースの定義情報をキャッシュするかどうか
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void setUseMetadataCache(boolean useMetadataCache) {
		if (freeze) throw new IllegalStateException();
		this.useMetadataCache = useMetadataCache;
	}

	/**
	 * Blendee が実行する SQL 文を、 {@link Initializer#setLogOutput(PrintStream)} で定義した出力先に出力するかどうかを設定します。
	 *
	 * @param enableLog SQL 文を出力するかどうか
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void enableLog(boolean enableLog) {
		if (freeze) throw new IllegalStateException();
		this.enableLog = enableLog;
	}

	/**
	 * Blendee が生成する SQL 文の出力する先を設定します。
	 * <br>
	 * なにも設定されない場合は {@link System#out} が使用されます。
	 *
	 * @param logOutput SQL文の出力する先
	 * @throws IllegalStateException 既に {@link BlendeeManager#initialize(Initializer)} を実行している場合
	 */
	public synchronized void setLogOutput(PrintStream logOutput) {
		if (freeze) throw new IllegalStateException();
		if (logOutput == null) throw new NullPointerException();
		this.logOutput = logOutput;
	}

	/**
	 * {@link Initializer#enableLog(boolean)} に true を設定した場合に出力されるSQL文生成箇所のスタックトレースをフィルタしたい場合に、そのパターンを設定します。
	 *
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
	 *
	 * @param max 一接続あたりの最大ステートメント数
	 */
	public synchronized void setMaxStatementsPerConnection(int max) {
		if (max < 1) throw new IllegalArgumentException("0 以下の値は設定できません");
		maxStatementsPerConnection = max;
	}

	/**
	 * Blendee 内のクラスが使用するオプションをセットします。
	 *
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
		if (schemaNames.size() == 0) throw new IllegalArgumentException("スキーマ名は最低一つ必要です");
		freeze = true;
		return new Configure(
			transactionFactoryClass,
			errorConverterClass,
			dataTypeConverterClass,
			metadataFactoryClass,
			schemaNames.toArray(new String[schemaNames.size()]),
			useMetadataCache,
			enableLog,
			logOutput,
			logStackTracePattern,
			maxStatementsPerConnection,
			options);
	}
}
