package org.blendee.sql.binder;

import java.io.Serializable;
import java.sql.Timestamp;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.sql.Binder;

/**
 * {@link BPreparedStatement} に {@link Timestamp} の値を設定するための {@link Binder} です。
 * @author 千葉 哲嗣
 */
public final class TimestampBinder extends Binder implements Serializable {

	private static final long serialVersionUID = -5124328334762735345L;

	private final Timestamp value;

	/**
	 * パラメータの値を持つインスタンスを生成します。
	 * @param value このインスタンスの値
	 */
	public TimestampBinder(Timestamp value) {
		this.value = clone(value);
	}

	@Override
	public void bind(int index, BPreparedStatement statement) {
		statement.setTimestamp(index, value);
	}

	@Override
	public String toString() {
		return value == null ? "null" : String.valueOf(value.getTime());
	}

	@Override
	public Binder replicate() {
		return new TimestampBinder(clone(value));
	}

	@Override
	public boolean canEvalValue() {
		return true;
	}

	@Override
	public Object getValue() {
		return clone(value);
	}

	/**
	 * このインスタンスの持つ値を返します。
	 * @return 値
	 */
	public Timestamp getTimestampValue() {
		return clone(value);
	}

	@Override
	protected Object getSpecificallyValue() {
		return value;
	}

	private static final Timestamp clone(Timestamp value) {
		return value == null ? null : (Timestamp) value.clone();
	}
}
