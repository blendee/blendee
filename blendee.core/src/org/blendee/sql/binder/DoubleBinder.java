package org.blendee.sql.binder;

import java.math.BigDecimal;

import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.sql.Binder;

/**
 * {@link BlenPreparedStatement} に double の値を設定するための {@link Binder} です。
 * @author 千葉 哲嗣
 */
public final class DoubleBinder extends Binder {

	private final double value;

	/**
	 * パラメータの値を持つインスタンスを生成します。
	 * @param value このインスタンスの値
	 */
	public DoubleBinder(double value) {
		this.value = value;
	}

	@Override
	public void bind(int index, BlenPreparedStatement statement) {
		statement.setDouble(index, value);
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
		return Double.valueOf(value);
	}

	/**
	 * このインスタンスの持つ値を返します。
	 * @return 値
	 */
	public double getDoubleValue() {
		return value;
	}

	@Override
	protected Object getSpecificallyValue() {
		return new BigDecimal(String.valueOf(value));
	}
}
