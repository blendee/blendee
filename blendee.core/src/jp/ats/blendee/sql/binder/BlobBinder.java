package jp.ats.blendee.sql.binder;

import java.sql.Blob;

import jp.ats.blendee.jdbc.BPreparedStatement;
import jp.ats.blendee.sql.Binder;

/**
 * {@link BPreparedStatement} に {@link Blob} の値を設定するための {@link Binder} です。
 *
 * @author 千葉 哲嗣
 */
public final class BlobBinder extends Binder {

	private final Blob value;

	/**
	 * パラメータの値を持つインスタンスを生成します。
	 *
	 * @param value このインスタンスの値
	 */
	public BlobBinder(Blob value) {
		this.value = value;
	}

	@Override
	public void bind(int index, BPreparedStatement statement) {
		statement.setBlob(index, value);
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
	 *
	 * @return 値
	 */
	public Blob getBlobValue() {
		return value;
	}

	@Override
	protected Object getSpecificallyValue() {
		return value;
	}
}
