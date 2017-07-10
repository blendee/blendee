package org.blendee.sql.binder;

import java.math.BigDecimal;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.sql.Binder;

/**
 * {@link BPreparedStatement} に float の値を設定するための {@link Binder} です。
 * @author 千葉 哲嗣
 */
public final class FloatBinder extends Binder {

	private final float value;

	/**
	 * パラメータの値を持つインスタンスを生成します。
	 * @param value このインスタンスの値
	 */
	public FloatBinder(float value) {
		this.value = value;
	}

	@Override
	public void bind(int index, BPreparedStatement statement) {
		statement.setFloat(index, value);
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
		return new Float(value);
	}

	/**
	 * このインスタンスの持つ値を返します。
	 * @return 値
	 */
	public float getFloatValue() {
		return value;
	}

	@Override
	protected Object getSpecificallyValue() {
		return new BigDecimal(String.valueOf(value));
	}
}
