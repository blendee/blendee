package org.blendee.internal;

import java.util.LinkedList;
import java.util.List;

import org.blendee.jdbc.Committable;

/**
 * 内部使用ユーティリティクラス
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public class Transactions implements Committable {

	private final List<Committable> list = new LinkedList<>();

	@Override
	public void commit() {
		try {
			for (Committable transaction : list)
				transaction.commit();
		} finally {
			list.clear();
		}
	}

	@Override
	public void rollback() {
		try {
			for (Committable transaction : list) {
				transaction.rollback();
			}
		} finally {
			list.clear();
		}
	}

	public void regist(Committable transaction) {
		list.add(transaction);
	}

	public int size() {
		return list.size();
	}

	public void clear() {
		list.clear();
	}

	public Committable[] getChildren() {
		return list.toArray(new Committable[list.size()]);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
