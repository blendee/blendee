package org.blendee.jdbc;

/**
 * {@link Transaction} を生成するファクトリのインターフェイスです。
 * @author 千葉 哲嗣
 * @see Initializer#setTransactionFactoryClass(Class)
 */
@FunctionalInterface
public interface TransactionFactory {

	/**
	 * トランザクションを生成します。
	 * @return トランザクション
	 */
	Transaction createTransaction();
}
