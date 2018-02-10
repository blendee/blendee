package org.blendee.sql.binder;

import java.math.BigDecimal;

import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.sql.Binder;

/**
 * {@link BlenPreparedStatement} に long の値を設定するための {@link Binder} です。
 * @author 千葉 哲嗣
 */
public final class LongBinder extends Binder {

	private final long value;

	/**
	 * パラメータの値を持つインスタンスを生成します。
	 * @param value このインスタンスの値
	 */
	public LongBinder(long value) {
		this.value = value;
	}

	@Override
	public void bind(int index, BlenPreparedStatement statement) {
		statement.setLong(index, value);
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
		return Long.valueOf(value);
	}

	/**
	 * このインスタンスの持つ値を返します。
	 * @return 値
	 */
	public long getLongValue() {
		return value;
	}

	@Override
	protected Object getSpecificallyValue() {
		return new BigDecimal(String.valueOf(value));
	}
}