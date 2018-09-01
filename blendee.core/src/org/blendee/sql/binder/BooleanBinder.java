package org.blendee.sql.binder;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.sql.Binder;

/**
 * {@link BPreparedStatement} に boolean の値を設定するための {@link Binder} です。
 * @author 千葉 哲嗣
 */
public final class BooleanBinder extends Binder {

	private static final long serialVersionUID = 5936012973826527763L;

	private final boolean value;

	/**
	 * パラメータの値を持つインスタンスを生成します。
	 * @param value このインスタンスの値
	 */
	public BooleanBinder(boolean value) {
		this.value = value;
	}

	@Override
	public void bind(int index, BPreparedStatement statement) {
		statement.setBoolean(index, value);
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
		return Boolean.valueOf(value);
	}

	/**
	 * このインスタンスの持つ値を返します。
	 * @return 値
	 */
	public boolean getBooleanValue() {
		return value;
	}

	@Override
	protected Object getSpecificallyValue() {
		return Boolean.valueOf(value);
	}
}
