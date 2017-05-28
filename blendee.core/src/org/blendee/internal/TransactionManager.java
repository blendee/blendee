package org.blendee.internal;

import java.io.PrintStream;

import org.blendee.jdbc.Transaction;

/**
 * 内部使用ユーティリティクラス
 *
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public class TransactionManager {

	private static final ThreadLocal<Transactions> current = new ThreadLocal<Transactions>() {

		@Override
		protected Transactions initialValue() {
			return new Transactions() {

				@Override
				public void regist(Transaction transaction) {
					throw new IllegalStateException("Shell の start が実行されていません");
				}
			};
		}
	};

	private static PrintStream stream = System.err;

	private TransactionManager() {}

	public static void start(Shell shell) throws Exception {
		Transactions transactions = new Transactions();
		Transactions oldTransactions = changeCurrent(transactions);
		try {
			shell.prepare();
			shell.execute();
			transactions.commit();
		} catch (Exception e) {
			try {
				transactions.rollback();
			} catch (RuntimeException ee) {
				ee.printStackTrace(getPrintStream());
			}

			throw e;
		} finally {
			try {
				shell.doFinally();
			} catch (RuntimeException e) {
				e.printStackTrace(getPrintStream());
			} finally {
				changeCurrent(oldTransactions);
			}
		}
	}

	public static void regist(Transaction transaction) {
		current.get().regist(transaction);
	}

	public static synchronized void setPrintStream(PrintStream stream) {
		TransactionManager.stream = stream;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private static Transactions changeCurrent(Transactions transactions) {
		Transactions oldTransactions = current.get();
		current.set(transactions);
		return oldTransactions;
	}

	private static synchronized PrintStream getPrintStream() {
		return stream;
	}
}
