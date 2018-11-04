package org.blendee.sql.binder;

import java.io.Serializable;
import java.util.Arrays;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.sql.Binder;

/**
 * {@link BPreparedStatement} に byte 配列を設定するための {@link Binder} です。
 * @author 千葉 哲嗣
 */
public final class ByteArrayBinder extends Binder implements Serializable {

	private static final long serialVersionUID = -2881851076127712598L;

	private final ByteArray byteArray;

	/**
	 * パラメータの値を持つインスタンスを生成します。
	 * @param value このインスタンスの値
	 */
	public ByteArrayBinder(byte[] value) {
		this.byteArray = new ByteArray(value);
	}

	@Override
	public void bind(int index, BPreparedStatement statement) {
		statement.setBytes(index, byteArray.value);
	}

	@Override
	public String toString() {
		return String.valueOf(byteArray);
	}

	@Override
	public Binder replicate() {
		return new ByteArrayBinder(byteArray.getClone());
	}

	@Override
	public boolean canEvalValue() {
		return true;
	}

	@Override
	public Object getValue() {
		return byteArray.getClone();
	}

	/**
	 * このインスタンスの持つ値を返します。
	 * @return 値
	 */
	public byte[] getByteArrayValue() {
		return byteArray.getClone();
	}

	@Override
	protected Object getSpecificallyValue() {
		return byteArray;
	}

	private static class ByteArray implements Serializable {

		private static final long serialVersionUID = -4868414829167612853L;

		private final byte[] value;

		private ByteArray(byte[] value) {
			this.value = clone(value);
		}

		@Override
		public int hashCode() {
			if (value == null) return 0;
			int[] hashCodes = new int[value.length];
			for (int i = 0; i < hashCodes.length; i++) {
				hashCodes[i] = value[i];
			}

			return Arrays.hashCode(hashCodes);
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof ByteArray)) return false;
			byte[] targetValue = ((ByteArray) o).value;
			if (value == null && targetValue == null) return true;
			return Arrays.equals(value, targetValue);
		}

		@Override
		public String toString() {
			return value == null ? null : new String(value);
		}

		private byte[] getClone() {
			return clone(value);
		}

		private static byte[] clone(byte[] value) {
			return value == null ? null : value.clone();
		}
	}
}
