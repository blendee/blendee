package org.blendee.sql.binder;

import java.sql.Clob;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.sql.Binder;

/**
 * {@link BPreparedStatement} に {@link Clob} の値を設定するための {@link Binder} です。
 * @author 千葉 哲嗣
 */
public final class ClobBinder extends Binder {

	private static final long serialVersionUID = 7840321773762641678L;

	private final Clob value;

	/**
	 * パラメータの値を持つインスタンスを生成します。
	 * @param value このインスタンスの値
	 */
	public ClobBinder(Clob value) {
		this.value = value;
	}

	@Override
	public void bind(int index, BPreparedStatement statement) {
		statement.setClob(index, value);
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public Binder replicate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canEvalValue() {
		return true;
	}

	@Override
	public Object getValue() {
		return value;
	}

	/**
	 * このインスタンスの持つ値を返します。
	 * @return 値
	 */
	public Clob getClobValue() {
		return value;
	}

	@Override
	protected Object getSpecificallyValue() {
		return value;
	}
}
