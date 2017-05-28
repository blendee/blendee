package org.blendee.internal;

import java.util.LinkedList;
import java.util.List;

import org.blendee.jdbc.Transaction;

/**
 * 内部使用ユーティリティクラス
 *
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public class Transactions implements Transaction {

	private final List<Transaction> list = new LinkedList<>();

	@Override
	public void commit() {
		try {
			for (Transaction transaction : list)
				transaction.commit();
		} finally {
			list.clear();
		}
	}

	@Override
	public void rollback() {
		try {
			for (Transaction transaction : list) {
				transaction.rollback();
			}
		} finally {
			list.clear();
		}
	}

	public void regist(Transaction transaction) {
		list.add(transaction);
	}

	public int size() {
		return list.size();
	}

	public void clear() {
		list.clear();
	}

	public Transaction[] getChildren() {
		return list.toArray(new Transaction[list.size()]);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
