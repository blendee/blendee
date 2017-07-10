package org.blendee.jdbc;

/**
 * {@link BTransaction} を生成するファクトリのインターフェイスです。
 * @author 千葉 哲嗣
 * @see Initializer#setTransactionFactoryClass(Class)
 */
@FunctionalInterface
public interface TransactionFactory {

	/**
	 * トランザクションを生成します。
	 * @return トランザクション
	 */
	BTransaction createTransaction();
}
