package org.blendee.sql.binder;

import java.math.BigDecimal;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.sql.Binder;

/**
 * {@link BPreparedStatement} に int の値を設定するための {@link Binder} です。
 * @author 千葉 哲嗣
 */
public final class IntBinder extends Binder {

	private final int value;

	/**
	 * パラメータの値を持つインスタンスを生成します。
	 * @param value このインスタンスの値
	 */
	public IntBinder(int value) {
		this.value = value;
	}

	@Override
	public void bind(int index, BPreparedStatement statement) {
		statement.setInt(index, value);
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public Binder replicate() {
		return this;
	}

	@Override
	public boolean canEvalValue() {
		return true;
	}

	@Override
	public Object getValue() {
		return new Integer(value);
	}

	/**
	 * このインスタンスの持つ値を返します。
	 * @return 値
	 */
	public int getIntValue() {
		return value;
	}

	@Override
	protected Object getSpecificallyValue() {
		return new BigDecimal(String.valueOf(value));
	}
}
