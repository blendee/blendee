package org.blendee.util;

import java.util.Map;
import java.util.Properties;

import org.blendee.jdbc.BlendeeException;
import org.blendee.jdbc.MetadataFactory;
import org.blendee.jdbc.OptionKey;
import org.blendee.jdbc.TransactionFactory;
import org.blendee.selector.ColumnRepositoryFactory;
import org.blendee.util.BlendeeEnvironment.BlendeeEnvironmentConsumer;
import org.blendee.util.BlendeeEnvironment.BlendeeEnvironmentFunction;

/**
 * Blendee 全体を対象とする、簡易操作クラスです。
 * @author 千葉 哲嗣
 */
public class Blendee {

	private static final BlendeeEnvironment environment = new BlendeeEnvironment();

	/**
	 * Blendee が既に使用可能な状態かを判定します。<br>
	 * {@link #start} は 2 度実行できないため、このメソッドで先に確認することが可能です。
	 * @return Blendee が既に使用可能な状態かどうか
	 */
	public static boolean started() {
		return environment.started();
	}

	/**
	 * Blendee を使用可能な状態にします。
	 * @param initValues Blendee を初期化するための値
	 */
	public static void start(Properties initValues) {
		environment.start(initValues);
	}

	/**
	 * Blendee を使用可能な状態にします。
	 * @param initValues Blendee を初期化するための値
	 */
	public static void start(Map<OptionKey<?>, ?> initValues) {
		environment.start(initValues);
	}

	/**
	 * 現在接続中の JDBC インスタンスをすべてクローズし、このコンテキストの Blendee を終了します。
	 */
	public static void stop() {
		environment.stop();
	}

	/**
	 * トランザクション内で任意の処理を実行します。
	 * @param process {@link BlendeeEnvironmentConsumer} の実装
	 * @throws BlendeeException 処理内で起こった例外
	 */
	public static void execute(BlendeeEnvironmentConsumer process) {
		environment.execute(process);
	}

	/**
	 * トランザクション内で任意の処理を実行します。
	 * @param process {@link BlendeeEnvironmentConsumer} の実装
	 * @return 戻り値
	 * @throws BlendeeException 処理内で起こった例外
	 */
	public static <T> T executeAndGet(BlendeeEnvironmentFunction<T> process) {
		return environment.executeAndGet(process);
	}

	/**
	 * Blendee が持つ定義情報の各キャッシュをクリアします。
	 */
	public static void clearCache() {
		environment.clearCache();
	}

	/**
	 * デフォルト {@link MetadataFactory} を返します。
	 * @return デフォルト {@link MetadataFactory}
	 */
	public static Class<? extends MetadataFactory> getDefaultMetadataFactoryClass() {
		return environment.getDefaultMetadataFactoryClass();
	}

	/**
	 * デフォルト {@link MetadataFactory} をセットします。
	 * @param defaultMetadataFactoryClass デフォルト {@link MetadataFactory}
	 */
	public static void setDefaultMetadataFactoryClass(
		Class<? extends MetadataFactory> defaultMetadataFactoryClass) {
		environment.setDefaultMetadataFactoryClass(defaultMetadataFactoryClass);
	}

	/**
	 * デフォルト {@link TransactionFactory} を返します。
	 * @return デフォルト {@link TransactionFactory}
	 */
	public static Class<? extends TransactionFactory> getDefaultTransactionFactoryClass() {
		return environment.getDefaultTransactionFactoryClass();
	}

	/**
	 * デフォルト {@link TransactionFactory} をセットします。
	 * @param defaultTransactionFactoryClass デフォルト {@link TransactionFactory}
	 */
	public static void setDefaultTransactionFactoryClass(
		Class<? extends TransactionFactory> defaultTransactionFactoryClass) {
		environment.setDefaultTransactionFactoryClass(defaultTransactionFactoryClass);
	}

	/**
	 * デフォルト {@link ColumnRepositoryFactory} を返します。
	 * @return デフォルト {@link ColumnRepositoryFactory}
	 */
	public static Class<? extends ColumnRepositoryFactory> getDefaultColumnRepositoryFactoryClass() {
		return environment.getDefaultColumnRepositoryFactoryClass();
	}

	/**
	 * デフォルト {@link ColumnRepositoryFactory} をセットします。
	 * @param defaultColumnRepositoryFactoryClass デフォルト {@link ColumnRepositoryFactory}
	 */
	public static void setDefaultColumnRepositoryFactoryClass(
		Class<? extends ColumnRepositoryFactory> defaultColumnRepositoryFactoryClass) {
		environment.setDefaultColumnRepositoryFactoryClass(defaultColumnRepositoryFactoryClass);
	}

	/**
	 * デフォルト {@link BlendeeEnvironment} を返します。
	 * @return デフォルト {@link BlendeeEnvironment}
	 */
	public static BlendeeEnvironment getEnvironment() {
		return environment;
	}
}
