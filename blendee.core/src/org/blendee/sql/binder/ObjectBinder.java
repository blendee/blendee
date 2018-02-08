package org.blendee.sql.binder;

import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.sql.Binder;

/**
 * {@link BlenPreparedStatement} に {@link Object} の値を設定するための {@link Binder} です。
 * @author 千葉 哲嗣
 */
public final class ObjectBinder extends Binder {

	private final Object value;

	/**
	 * パラメータの値を持つインスタンスを生成します。
	 * @param value このインスタンスの値
	 */
	public ObjectBinder(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public void bind(int index, BlenPreparedStatement statement) {
		statement.setObject(index, value);
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

	@Override
	protected Object getSpecificallyValue() {
		return value;
	}
}
